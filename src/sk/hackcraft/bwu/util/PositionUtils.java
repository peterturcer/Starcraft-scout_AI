package sk.hackcraft.bwu.util;

import java.util.Collection;

import jnibwapi.Position;

public class PositionUtils
{
	public static Position getNearest(Collection<Position> positions, Position from)
	{
		Position nearestPosition = null;
		double nearestDistance = Double.POSITIVE_INFINITY;
		
		for (Position p : positions)
		{
			double distance = p.getApproxPDistance(from);
			
			if (distance < nearestDistance)
			{
				nearestPosition = p;
				nearestDistance = distance;
			}
		}
		
		return nearestPosition;
	}
}
