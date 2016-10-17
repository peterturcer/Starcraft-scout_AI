package sk.hackcraft.bwu;

import jnibwapi.Unit;

public abstract class UnitOwning
{
	public void addUnit(Unit unit)
	{
		unitAdded(unit);
	}
	
	public void removeUnit(Unit unit)
	{
		unitRemoved(unit);
	}
	
	public abstract boolean owns(Unit unit);
	
	public abstract void unitAdded(Unit unit);
	public abstract void unitRemoved(Unit unit);
}
