package sk.hackcraft.bwu.production;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import jnibwapi.JNIBWAPI;
import jnibwapi.Position;
import jnibwapi.Position.PosType;
import jnibwapi.Unit;
import jnibwapi.types.RaceType;
import jnibwapi.types.RaceType.RaceTypes;
import jnibwapi.types.UnitType;
import jnibwapi.util.BWColor;
import sk.hackcraft.bwu.Constants;
import sk.hackcraft.bwu.Convert;
import sk.hackcraft.bwu.Drawable;
import sk.hackcraft.bwu.EnvironmentTime;
import sk.hackcraft.bwu.Game;
import sk.hackcraft.bwu.Graphics;
import sk.hackcraft.bwu.Updateable;
import sk.hackcraft.bwu.Vector2D;
import sk.hackcraft.bwu.resource.EntityPool;
import sk.hackcraft.bwu.resource.EntityPool.ContractListener;
import sk.hackcraft.bwu.selection.NearestPicker;
import sk.hackcraft.bwu.selection.Pickers;
import sk.hackcraft.bwu.selection.UnitSelector;
import sk.hackcraft.bwu.selection.UnitSet;

public class BuildingConstructionAgent implements Updateable, Drawable
{
	private final JNIBWAPI bwapi;
	private final EnvironmentTime time;

	private Position center;

	private final EntityPool.Contract<Unit> unitsContract;

	private final Set<ConstructionTask> tasks;
	private final Map<Unit, ConstructionTask> workersTasks;

	public BuildingConstructionAgent(JNIBWAPI bwapi, EnvironmentTime time, EntityPool.Contract<Unit> unitsContract)
	{
		this.bwapi = bwapi;
		this.time = time;
		this.unitsContract = unitsContract;

		tasks = new HashSet<>();
		workersTasks = new HashMap<Unit, ConstructionTask>();
	}

	public void setCenterPosition(Position center)
	{
		this.center = center;
	}

	@Override
	public void update()
	{
		Set<ConstructionTask> tasksCopy = new HashSet<>(tasks);
		for (ConstructionTask task : tasksCopy)
		{
			Unit worker = task.getWorker();
			if (worker != null && !task.isWorkerStillNeeded())
			{
				returnWorker(task);
				workersTasks.remove(worker);
			}

			if (task.isEnded())
			{
				tasks.remove(task);

				if (worker != null)
				{
					workersTasks.remove(task.getWorker());
				}
			}

			if (task.getActualState() == ConstructionTaskState.STARTING_CONSTRUCTION && task.getConstructedBuilding() == null)
			{
				Position buildPosition = task.getBuildPosition();
				UnitSet unitsOnTile = new UnitSet(bwapi.getUnitsOnTile(buildPosition)).where(UnitSelector.IS_BUILDING);

				if (!unitsOnTile.isEmpty())
				{
					UnitType buildingType = task.getBuildingType();
					Unit building = unitsOnTile.whereType(buildingType).pick(Pickers.FIRST);

					if (building != null)
					{
						task.addConstructedBuilding(building);
					}
				}
			}

			task.update();
		}
	}

	@Override
	public void draw(Graphics graphics)
	{
		for (ConstructionTask task : tasks)
		{
			task.draw(graphics);
		}
	}

	private void returnWorker(ConstructionTask task)
	{
		Unit worker = task.getWorker();
		task.setWorker(null);
		unitsContract.returnEntity(worker);
	}

	/**
	 * Issue build order.
	 * 
	 * @param buildingType
	 *            building type to construct
	 * @param listener
	 *            construction listener
	 * @param urgent
	 *            <code>true</code> if construction is urgent
	 * @param position
	 *            specified position to build. May be <code>null</code>
	 * @param worker
	 *            specified worker used to constructon. May be <code>null</code>
	 * @return
	 */
	public boolean construct(UnitType buildingType, ConstructionListener listener, boolean urgent, Position position, Unit worker)
	{
		if (position == null)
		{
			position = selectBuildPosition(buildingType);

			if (position == null)
			{
				return false;
			}
		}

		if (worker == null)
		{
			worker = selectWorker(buildingType, position, urgent);

			if (worker == null)
			{
				return false;
			}
		}

		ContractListener<Unit> contractListener = new ContractListener<Unit>()
		{
			@Override
			public void entityRemoved(Unit entity)
			{
				if (!entity.isConstructing() && !entity.isMorphing())
				{
					// TODO if entity is drone, check if its morphed
					ConstructionTask task = workersTasks.get(entity);
					task.cancel();

					workersTasks.remove(entity);
				}
			}
		};

		unitsContract.requestEntity(worker, contractListener, urgent);

		ConstructionTask task = new ConstructionTask(position, worker, buildingType, listener);
		tasks.add(task);
		workersTasks.put(worker, task);

		return true;
	}

	private Unit selectWorker(UnitType buildingType, Position buildPosition, boolean urgent)
	{
		UnitType workerType = buildingType.getRaceType().getWorkerType();
		UnitSet workers = new UnitSet(unitsContract.getAcquirableEntities(urgent)).whereType(workerType);

		if (workers.isEmpty())
		{
			return null;
		}

		return workers.pick(new NearestPicker(buildPosition));
	}

	private Position selectBuildPosition(UnitType buildingType)
	{
		// TODO implement properly
		Random random = new Random();

		for (int i = 0; i < 1000; i++)
		{
			int x = random.nextInt(20) - 10 + center.getBX();
			int y = random.nextInt(20) - 10 + center.getBY();

			Position position = new Position(x, y, PosType.BUILD);

			if (!bwapi.canBuildHere(position, buildingType, true))
			{
				continue;
			}

			return position;
		}

		return null;
	}

	/**
	 * Construction listener.
	 */
	public interface ConstructionListener
	{
		/**
		 * Called when construction failed.
		 */
		void failed();

		/**
		 * Called when building was created. Building can be still under
		 * construction.
		 * 
		 * @param building
		 *            created building
		 */
		void buildingCreated(Unit building);

		/**
		 * Called when construction was succesfully finished.
		 */
		void finished();
	}

	public enum ConstructionTaskState
	{
		MOVING_TO_POSITION(1), STARTING_CONSTRUCTION(2), CONSTRUCTING(3), COMPLETED(4), TERMINATED(5);

		private int order;

		private ConstructionTaskState(int order)
		{
			this.order = order;
		}

		public int getOrder()
		{
			return order;
		}
	};

	private class ConstructionTask implements Updateable, Drawable
	{
		private static final double NEAR_BUILD_POSITION = 100;

		private final Position buildPosition;
		private final Position buildCenterPosition;
		private final UnitType buildingType;
		private final ConstructionListener constructionListener;

		private Unit worker;
		private Unit constructedBuilding;
		private ConstructionTaskState actualState;

		public ConstructionTask(Position buildPosition, Unit worker, UnitType buildingType, ConstructionListener listener)
		{
			this.buildPosition = buildPosition;

			int width = (buildingType.getTileWidth() * Game.TILE_SIZE) / 2;
			int height = (buildingType.getTileHeight() * Game.TILE_SIZE) / 2;

			buildCenterPosition = new Position(buildPosition.getPX() + width, buildPosition.getPY() + height);

			this.worker = worker;
			this.buildingType = buildingType;
			this.constructionListener = listener;

			actualState = ConstructionTaskState.MOVING_TO_POSITION;

			worker.stop(false);
		}

		public Position getBuildPosition()
		{
			return buildPosition;
		}

		public UnitType getBuildingType()
		{
			return buildingType;
		}

		public Unit getWorker()
		{
			return worker;
		}

		public void setWorker(Unit worker)
		{
			this.worker = worker;
		}

		public boolean isConsumingWorker()
		{
			return buildingType.getRaceType() == RaceTypes.Zerg;
		}

		public boolean isWorkerStillNeeded()
		{
			RaceType raceType = buildingType.getRaceType();
			if (raceType == RaceTypes.Protoss)
			{
				return actualState.getOrder() < ConstructionTaskState.CONSTRUCTING.getOrder();
			}
			else if (raceType == RaceTypes.Terran)
			{
				return actualState.getOrder() < ConstructionTaskState.COMPLETED.getOrder();
			}
			else
			{
				return worker.getType() != buildingType;
			}
		}

		public ConstructionTaskState getActualState()
		{
			return actualState;
		}

		public Unit getConstructedBuilding()
		{
			return constructedBuilding;
		}

		public void addConstructedBuilding(Unit building)
		{
			this.constructedBuilding = building;
			actualState = ConstructionTaskState.CONSTRUCTING;

			constructionListener.buildingCreated(building);
		}

		@Override
		public void update()
		{
			if (actualState != ConstructionTaskState.TERMINATED && constructionTerminated())
			{
				actualState = ConstructionTaskState.TERMINATED;
				constructionListener.failed();
				return;
			}

			switch (actualState)
			{
				case MOVING_TO_POSITION:
				{
					if (isInPosition())
					{
						actualState = ConstructionTaskState.STARTING_CONSTRUCTION;
					}
					else
					{
						moveWorkerToPosition();
					}

					break;
				}
				case STARTING_CONSTRUCTION:
				{
					commenceConstruction(time);
					break;
				}
				case CONSTRUCTING:
				{
					if (finishedConstruction())
					{
						actualState = ConstructionTaskState.COMPLETED;
						constructionListener.finished();
					}
					break;
				}
				case COMPLETED:
				{
					break;
				}
				case TERMINATED:
				{
					break;
				}
			}
		}

		@Override
		public void draw(Graphics graphics)
		{
			if (worker != null)
			{
				Vector2D workerPositionVector = Convert.toPositionVector(worker.getPosition());

				graphics.setColor(BWColor.Yellow);
				graphics.fillCircle(worker, 4);

				Vector2D buildCenterPositionVector = Convert.toPositionVector(buildCenterPosition);
				Vector2D vectorToBuildPositon = buildCenterPositionVector.sub(workerPositionVector);

				if (vectorToBuildPositon.getLength() > Constants.LINE_DRAW_VISUAL_LIMIT)
				{
					vectorToBuildPositon = vectorToBuildPositon.normalize().scale((float) Constants.LINE_DRAW_VISUAL_LIMIT);
					buildCenterPositionVector = workerPositionVector.add(vectorToBuildPositon);
				}

				graphics.drawLine(workerPositionVector, buildCenterPositionVector);

				graphics.drawText(worker, actualState);
			}

			graphics.setColor(BWColor.Yellow);

			float width = buildingType.getTileWidth() * Game.TILE_SIZE;
			float height = buildingType.getTileHeight() * Game.TILE_SIZE;

			Vector2D buildPositionVector = Convert.toPositionVector(buildPosition);
			Vector2D topLeftCorner = new Vector2D(buildPositionVector.getX(), buildPositionVector.getY());
			Vector2D bottomRightCorner = new Vector2D(buildPositionVector.getX() + width, buildPositionVector.getY() + height);

			graphics.drawBox(topLeftCorner, bottomRightCorner);
		}

		private boolean constructionTerminated()
		{
			if (constructedBuilding == null && workerDestroyed())
			{
				return true;
			}

			if (constructedBuilding != null && buildingDestroyed())
			{
				return true;
			}

			return false;
		}

		private boolean workerDestroyed()
		{
			return !worker.isExists();
		}

		private boolean buildingDestroyed()
		{
			return !constructedBuilding.isExists();
		}

		private void moveWorkerToPosition()
		{
			if (!worker.isMoving() && time.getFrame() - worker.getLastCommandFrame() > 30)
			{
				worker.move(buildCenterPosition, false);
			}
		}

		private void commenceConstruction(EnvironmentTime time)
		{
			if (time.getFrame() % 30 == 0)
			{
				worker.build(buildPosition, buildingType);
			}
		}

		private boolean finishedConstruction()
		{
			return constructedBuilding.isCompleted();
		}

		private boolean isInPosition()
		{
			double distance = worker.getPosition().getPDistance(buildCenterPosition);
			return distance < NEAR_BUILD_POSITION;
		}

		public boolean isEnded()
		{
			return actualState == ConstructionTaskState.COMPLETED || actualState == ConstructionTaskState.TERMINATED;
		}

		public boolean getStatus()
		{
			return actualState != ConstructionTaskState.TERMINATED;
		}

		public void cancel()
		{
			worker.stop(false);
		}
	}
}
