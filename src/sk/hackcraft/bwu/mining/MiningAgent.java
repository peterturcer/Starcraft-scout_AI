package sk.hackcraft.bwu.mining;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jnibwapi.JNIBWAPI;
import jnibwapi.Position;
import jnibwapi.Unit;
import jnibwapi.types.OrderType.OrderTypes;
import jnibwapi.types.UnitType.UnitTypes;
import jnibwapi.util.BWColor;
import sk.hackcraft.bwu.Convert;
import sk.hackcraft.bwu.Drawable;
import sk.hackcraft.bwu.EntitiesContract;
import sk.hackcraft.bwu.EntitiesContract.ContractListener;
import sk.hackcraft.bwu.Graphics;
import sk.hackcraft.bwu.Updateable;
import sk.hackcraft.bwu.Vector2D;
import sk.hackcraft.bwu.selection.UnitSelector;
import sk.hackcraft.bwu.selection.UnitSet;

public class MiningAgent implements Updateable, Drawable
{
	private final JNIBWAPI bwapi;

	private final Unit resourceDepot;

	private final UnitSet resources;

	private final UnitSet miners;
	private final Map<Unit, Unit> minersToResourcesAssignments;

	private final Map<Unit, Integer> actualSaturations;
	private final Map<Unit, Integer> fullSaturations;

	private final EntitiesContract<Unit> minersContract;

	public MiningAgent(JNIBWAPI bwapi, Unit resourceDepot, Set<Unit> resources, EntitiesContract<Unit> minersContract)
	{
		this.bwapi = bwapi;

		this.resourceDepot = resourceDepot;
		this.resources = new UnitSet(resources);

		this.miners = new UnitSet();

		this.minersToResourcesAssignments = new HashMap<>();
		this.actualSaturations = new HashMap<>();
		this.fullSaturations = new HashMap<>();

		for (Unit resource : resources)
		{
			actualSaturations.put(resource, 0);

			int fullSaturation = (resource.getType() == UnitTypes.Resource_Vespene_Geyser) ? 0 : 3;
			fullSaturations.put(resource, fullSaturation);
		}

		this.minersContract = minersContract;
		minersContract.setListener(new ContractListener<Unit>()
		{
			@Override
			public void entityRemoved(Unit entity)
			{
				removeMiner(entity);
			}

			@Override
			public void entityAdded(Unit entity)
			{
				addMiner(entity);
			}
		});
	}

	public Unit getResourceDepot()
	{
		return resourceDepot;
	}

	public UnitSet getResources()
	{
		return resources;
	}

	public UnitSet getUnusedGeysers()
	{
		return resources.where(UnitSelector.IS_GAS_GEYSER);
	}

	private void addMiner(Unit miner)
	{
		Unit resource = selectResourceForAssignment();

		if (resource != null)
		{
			minersToResourcesAssignments.put(miner, resource);

			int actualSaturation = actualSaturations.get(resource);
			actualSaturations.put(resource, actualSaturation + 1);

			miners.add(miner);
		}
		else
		{
			minersContract.returnEntity(miner);
		}
	}

	private void removeMiner(Unit miner)
	{
		freeMiner(miner);

		miners.remove(miner);
	}

	public Set<Unit> getMiners()
	{
		return miners;
	}

	public int getSaturationDeficit()
	{
		return getFullSaturation() - getActualSaturation();
	}

	public int getFullSaturation()
	{
		int fullSaturation = 0;

		for (int resourceFullSaturaton : fullSaturations.values())
		{
			fullSaturation += resourceFullSaturaton;
		}

		return fullSaturation;
	}

	public int getActualSaturation()
	{
		return miners.size();
	}

	@Override
	public void update()
	{
		checkResources();
		checkGasProcessing();
		checkWorkers();
	}

	@Override
	public void draw(Graphics graphics)
	{
		graphics.setGameCoordinates();

		for (Unit miner : miners)
		{
			graphics.setColor(BWColor.Green);
			graphics.drawCircle(miner, 3);

			if (miner.isSelected())
			{
				Vector2D resourceDepotVector = Convert.toPositionVector(resourceDepot.getPosition());
				Vector2D resourceVector = Convert.toPositionVector(miner.getPosition());

				graphics.setColor(BWColor.Red);
				graphics.drawLine(resourceDepotVector, resourceVector);
			}

			if (miner.getOrder() == OrderTypes.MiningMinerals)
			{
				graphics.setColor(BWColor.Green);
				graphics.fillCircle(miner, 3);
			}

			if (miner.getOrder() == OrderTypes.WaitForMinerals)
			{
				graphics.setColor(BWColor.Orange);
				graphics.fillCircle(miner, 3);
			}

			Unit target = miner.getTarget();
			if (target != null)
			{
				Position targetPosition = target.getPosition();
				Position minerPosition = miner.getPosition();

				if (targetPosition.getPDistance(minerPosition) < 500)
				{
					graphics.setColor(BWColor.Green);

					Vector2D from = Convert.toPositionVector(targetPosition);
					Vector2D to = Convert.toPositionVector(minerPosition);

					graphics.drawLine(from, to);
				}
			}
		}

		graphics.drawText(resourceDepot, "A: " + miners.size() + "/" + getFullSaturation());

		if (resourceDepot.isSelected())
		{
			for (Unit resource : resources)
			{
				Vector2D resourceDepotVector = Convert.toPositionVector(resourceDepot.getPosition());
				Vector2D resourceVector = Convert.toPositionVector(resource.getPosition());

				graphics.setColor(BWColor.Grey);
				graphics.drawLine(resourceDepotVector, resourceVector);
			}

			for (Unit miner : miners)
			{
				Vector2D resourceDepotVector = Convert.toPositionVector(resourceDepot.getPosition());
				Vector2D resourceVector = Convert.toPositionVector(miner.getPosition());

				graphics.setColor(BWColor.Red);
				graphics.drawLine(resourceDepotVector, resourceVector);
			}
		}
	}

	private void checkGasProcessing()
	{
		UnitSet refineries = resources.where(UnitSelector.IS_GAS_EXTRACTION_BUILDING).where(UnitSelector.IS_COMPLETED);
		for (Unit refinery : refineries)
		{
			int fullSaturation = fullSaturations.get(refinery);

			if (fullSaturation == 0)
			{
				fullSaturations.put(refinery, 3);
			}
		}

		UnitSet geysers = resources.where(UnitSelector.IS_GAS_GEYSER);
		for (Unit geyser : geysers)
		{
			int fullSaturation = fullSaturations.get(geyser);

			if (fullSaturation != 0)
			{
				fullSaturations.put(geyser, 0);
			}
		}
	}

	private void checkWorkers()
	{
		for (Unit miner : miners)
		{
			Unit resource = minersToResourcesAssignments.get(miner);

			if (resource == null)
			{
				continue;
			}

			if (miner.isIdle())
			{
				miner.gather(resource, false);
				continue;
			}

			Unit target = miner.getTarget();

			if (target != null && target.getType().isResourceContainer())
			{
				if (!resource.equals(target))
				{
					miner.gather(resource, false);
				}
			}
		}
	}

	private void checkResources()
	{
		Set<Unit> resourcesCopy = new HashSet<>(resources);
		for (Unit resource : resourcesCopy)
		{
			if (UnitSelector.IS_GAS_SOURCE.isTrueFor(resource))
			{
				continue;
			}

			if (!bwapi.isVisible(resource.getPosition()))
			{
				continue;
			}

			if (isResourceDepleted(resource))
			{
				removeResource(resource);
			}
		}
	}

	private boolean isResourceDepleted(Unit resource)
	{
		UnitSet resourcesOnTile = new UnitSet(bwapi.getUnitsOnTile(resource.getPosition())).where(UnitSelector.IS_RESOURCE);
		return resourcesOnTile.isEmpty();
	}

	private void removeResource(Unit resource)
	{
		Map<Unit, Unit> assignmentsCopy = new HashMap<>(minersToResourcesAssignments);
		for (Map.Entry<Unit, Unit> entry : assignmentsCopy.entrySet())
		{
			if (entry.getValue() == resource)
			{
				Unit miner = entry.getKey();
				freeMiner(miner);
			}
		}

		resources.remove(resource);
		actualSaturations.remove(resource);
		fullSaturations.remove(resource);
	}

	private void freeMiner(Unit miner)
	{
		Unit assignedResource = minersToResourcesAssignments.get(miner);

		int saturation = actualSaturations.get(assignedResource);
		actualSaturations.put(assignedResource, saturation - 1);

		minersToResourcesAssignments.remove(miner);
	}

	private Unit selectResourceForAssignment()
	{
		UnitSet refineries = resources.where(UnitSelector.IS_GAS_EXTRACTION_BUILDING);
		Unit resource = getLeastSaturatedResource(refineries);

		if (resource == null)
		{
			UnitSet minerals = resources.where(UnitSelector.IS_MINERAL);
			resource = getLeastSaturatedResource(minerals);
		}

		return resource;
	}

	private Unit getLeastSaturatedResource(UnitSet resources)
	{
		Unit leastSaturatedResource = null;
		int lowestSaturation = Integer.MAX_VALUE;

		for (Unit resource : resources)
		{
			int actualSaturation = actualSaturations.get(resource);

			int fullSaturation = fullSaturations.get(resource);

			if (actualSaturation < fullSaturation && actualSaturation < lowestSaturation)
			{
				leastSaturatedResource = resource;
				lowestSaturation = actualSaturation;
			}
		}

		return leastSaturatedResource;
	}
}
