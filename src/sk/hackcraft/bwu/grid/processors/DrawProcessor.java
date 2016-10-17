package sk.hackcraft.bwu.grid.processors;

import java.util.Set;

import sk.hackcraft.bwu.grid.Grid;
import sk.hackcraft.bwu.grid.GridPoint;

public class DrawProcessor
{
	public static void putValue(Grid layer, Set<GridPoint> points, int value)
	{
		for (GridPoint point : points)
		{
			layer.set(point, value);
		}
	}
}
