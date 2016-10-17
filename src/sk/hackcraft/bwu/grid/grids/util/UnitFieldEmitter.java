package sk.hackcraft.bwu.grid.grids.util;

import jnibwapi.Unit;
import sk.hackcraft.bwu.Convert;
import sk.hackcraft.bwu.grid.GridPoint;
import sk.hackcraft.bwu.grid.grids.PotentialFieldMatrixLayer.FieldEmitter;

public class UnitFieldEmitter implements FieldEmitter<Unit>
{
	private final Unit unit;
	
	public UnitFieldEmitter(Unit unit)
	{
		this.unit = unit;
	}
	
	@Override
	public int getValue()
	{
		return unit.getType().getGroundWeapon().getMaxRange() / 32;
	}

	@Override
	public GridPoint getPosition()
	{
		return Convert.toGridPoint(unit.getPosition());
	}

	@Override
	public Unit getSource()
	{
		return unit;
	}
}
