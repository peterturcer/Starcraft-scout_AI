package sk.hackcraft.bwu.production;

import java.util.HashSet;
import java.util.Set;

import jnibwapi.Unit;
import jnibwapi.types.UnitType;
import jnibwapi.types.UnitType.UnitTypes;
import jnibwapi.util.BWColor;
import sk.hackcraft.bwu.Drawable;
import sk.hackcraft.bwu.Graphics;
import sk.hackcraft.bwu.UnitOwning;
import sk.hackcraft.bwu.Updateable;

public class LarvaProductionAgent implements Updateable, Drawable
{
	private final UnitOwning hatcheriesOwning;
	
	private final Set<Unit> hatcheries;
	
	private final Set<Unit> availableLarvae;
	
	private final Set<MorphProductionTask> productionTasks;
	
	public LarvaProductionAgent()
	{
		this.hatcheriesOwning = new UnitOwning()
		{
			@Override
			public void unitRemoved(Unit unit)
			{
				hatcheries.remove(unit);
			}
			
			@Override
			public void unitAdded(Unit unit)
			{
				hatcheries.add(unit);
			}
			
			@Override
			public boolean owns(Unit unit)
			{
				return hatcheries.contains(unit);
			}
		};
		
		this.hatcheries = new HashSet<>();
		
		this.availableLarvae = new HashSet<>();
		
		this.productionTasks = new HashSet<>();
	}
	
	/**
	 * Gets {@link UnitOwning} for hatcheries.
	 * @return {@link UnitOwning} for hatcheries
	 */
	public UnitOwning getHatcheriesOwning()
	{
		return hatcheriesOwning;
	}
	
	@Override
	public void update()
	{
		Set<MorphProductionTask> productionTasksCopy = new HashSet<>(productionTasks);
		for (MorphProductionTask productionTask : productionTasksCopy)
		{
			productionTask.update();
			
			if (productionTask.isEnded())
			{
				productionTasks.remove(productionTask);
			}
		}

		availableLarvae.clear();
		for (Unit hatchery : hatcheries)
		{
			availableLarvae.addAll(hatchery.getLarva());
		}
	}
	
	/**
	 * Returns available production capacity. That means, for how many units of specified type can production start immediately.
	 * @param type specified type
	 * @return production capacity
	 */
	public int getAvailableProductionCapacity(UnitType type)
	{
		if (type.getWhatBuild() == UnitTypes.Zerg_Larva)
		{
			return availableLarvae.size();
		}
		else
		{
			return 0;
		}
	}

	@Override
	public void draw(Graphics graphics)
	{
		for (Unit larva : availableLarvae)
		{
			graphics.setColor(BWColor.Yellow);
			
			if (larva.getType() != UnitTypes.Zerg_Larva)
			{
				graphics.setColor(BWColor.Red);
			}
			
			graphics.drawCircle(larva, 3);
		}
	}
	
	/**
	 * Returns available larvae.
	 * @return available larvae
	 */
	public Set<Unit> getAvailableLarvae()
	{
		return availableLarvae;
	}

	/**
	 * Issue producton of specified unit type.
	 * @param type specified unit type
	 * @return <code>true</code> if producton was started, <code>false</code> otherwise
	 */
	public boolean produce(UnitType type)
	{
		Set<Unit> larvae = availableLarvae;
		
		if (larvae.isEmpty())
		{
			return false;
		}
		
		Unit larva = larvae.iterator().next();

		return produce(type, larva);
	}
	
	/**
	 * Issue producton of specified unit type with specified production unit.
	 * @param type specified unit type
	 * @param producer unit used for producton
	 * @return <code>true</code> if producton was started, <code>false</code> otherwise
	 */
	public boolean produce(UnitType type, Unit producer)
	{
		Set<Unit> larvae = availableLarvae;
		
		if (!larvae.contains(producer))
		{
			throw new IllegalStateException("Production doesn't own this larva.");
		}
		
		return producer.morph(type);
	}
	
	/**
	 * Issue producton of specified unit type with specified listener.
	 * @param type specified unit type
	 * @param producer production listener
	 * @return <code>true</code> if producton was started, <code>false</code> otherwise
	 */
	public boolean produce(UnitType type, ProductionListener listener)
	{
		Set<Unit> larvae = availableLarvae;
		
		if (larvae.isEmpty())
		{
			return false;
		}
		
		Unit larva = larvae.iterator().next();
		
		boolean result = larva.morph(type);
		
		if (result)
		{
			availableLarvae.remove(larva);

			MorphProductionTask task = new MorphProductionTask(type, larva, listener);
			
			productionTasks.add(task);
		}
		
		return result;
	}
	
	/**
	 * Production listener.
	 */
	public interface ProductionListener
	{
		/**
		 * Called when production was started.
		 * @param productionStatus production status for produced unit
		 */
		void started(ProductionStatus productionStatus);
		/**
		 * Called when production was finished.
		 */
		void finished();
		/**
		 * Called when production was terminated.
		 */
		void terminated();
	}
	
	/**
	 * Production status.
	 */
	public abstract class ProductionStatus
	{
		/**
		 * Returns production percentage status.
		 * @return production percentage status
		 */
		public abstract int getPercentage();
	}
	
	private class MorphProductionTask implements Updateable
	{
		private final UnitType typeToProduce;
		private final Unit sourceUnit;
		private final ProductionListener listener;

		private boolean ended;
		
		public MorphProductionTask(UnitType typeToProduce, final Unit sourceUnit, ProductionListener listener)
		{
			this.typeToProduce = typeToProduce;
			this.sourceUnit = sourceUnit;
			this.listener = listener;
			
			final int buildTime = typeToProduce.getBuildTime();
			
			ProductionStatus productionStatus = new ProductionStatus()
			{
				@Override
				public int getPercentage()
				{
					return buildTime * sourceUnit.getRemainingBuildTimer() / 100;
				}
			};

			listener.started(productionStatus);
		}
		
		public boolean isEnded()
		{
			return ended;
		}
		
		@Override
		public void update()
		{
			if (sourceUnit.getType().equals(typeToProduce))
			{
				listener.finished();
				ended = true;
			}
			
			if (!sourceUnit.isExists())
			{
				listener.terminated();
				ended = true;
			}
		}
	}
}
