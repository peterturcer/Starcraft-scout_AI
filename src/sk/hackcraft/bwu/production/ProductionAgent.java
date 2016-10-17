package sk.hackcraft.bwu.production;

import jnibwapi.types.UnitType;

public interface ProductionAgent
{
	public boolean produce(UnitType unitType);
	
	public int getProductionCapacity(UnitType type);
}
