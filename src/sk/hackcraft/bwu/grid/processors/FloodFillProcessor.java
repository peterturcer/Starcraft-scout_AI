package sk.hackcraft.bwu.grid.processors;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import sk.hackcraft.bwu.Comparison;
import sk.hackcraft.bwu.grid.Grid;
import sk.hackcraft.bwu.grid.GridPoint;
import sk.hackcraft.bwu.grid.GridUtil;

public class FloodFillProcessor
{
	public static void fillGradient(Grid layer, Map<GridPoint, Integer> startPoints)
	{
		fillGradient(layer, startPoints, -1, Comparison.LESS);
	}
	
	public static void fillGradient(Grid layer, Map<GridPoint, Integer> startPoints, int step, Comparison comparison)
	{
		for (Map.Entry<GridPoint, Integer> entry : startPoints.entrySet())
		{
			GridPoint point = entry.getKey();
			int value = entry.getValue();
			
			layer.set(point, value);
		}
		
		fillGradient(layer, startPoints.keySet(), step, comparison);
	}

	public static void fillGradient(Grid layer, Set<GridPoint> startPoints)
	{
		fillGradient(layer, startPoints, -1, Comparison.LESS);
	}
	
	public static void fillGradient(Grid layer, Set<GridPoint> startPoints, int step, Comparison comparison)
	{
		Queue<GridPoint> continuePoints = new LinkedList<GridPoint>(startPoints);
		
		while (!continuePoints.isEmpty())
		{
			GridPoint point = continuePoints.remove();
			
			int actualValue = layer.get(point);
			
			int newValue = actualValue + step;
			
			for (GridPoint direction : GridUtil.get4WayDirections())
			{
				GridPoint cellPosition = point.add(direction);
				
				if (!layer.isValid(cellPosition))
				{
					continue;
				}
				
				int cellValue = layer.get(cellPosition);
				
				if (comparison.compare(cellValue, newValue))
				{
					layer.set(cellPosition, newValue);
					continuePoints.add(cellPosition);
				}
			}
		}
	}
	
	public static Set<GridPoint> defillGradient(Grid layer, int clearValue, Set<GridPoint> startPoints)
	{
		Set<GridPoint> borderPoints = new HashSet<>();
		Queue<GridPoint> continuePoints = new LinkedList<GridPoint>(startPoints);
		
		while (!continuePoints.isEmpty())
		{
			GridPoint point = continuePoints.remove();
			
			int actualValue = layer.get(point);
			
			for (GridPoint direction : GridUtil.get4WayDirections())
			{
				GridPoint cellPosition = point.add(direction);
				
				if (!layer.isValid(cellPosition))
				{
					continue;
				}
				
				int cellValue = layer.get(cellPosition);
				
				if (cellValue == clearValue)
				{
					continue;
				}
				
				if (cellValue < actualValue)
				{
					continuePoints.add(cellPosition);
				}
				else
				{
					borderPoints.add(cellPosition);
				}
			}
			
			layer.set(point, clearValue);
		}
		
		return borderPoints;
	}
	
	public static void fillValue(Grid layer, Map<GridPoint, Integer> startPoints, int fillableValue)
	{
		Set<GridPoint> startPointsSet = new HashSet<>();
		
		for (Map.Entry<GridPoint, Integer> entry : startPoints.entrySet())
		{
			GridPoint point = entry.getKey();
			int value = entry.getValue();
			
			layer.set(point, value);
			
			startPointsSet.add(point);
		}
		
		fillValue(layer, startPointsSet, fillableValue);
	}
	
	public static void fillValue(Grid layer, Set<GridPoint> startPoints, int fillableValue)
	{
		Queue<GridPoint> continuePoints = new LinkedList<>(startPoints);

		while (!continuePoints.isEmpty())
		{
			GridPoint point = continuePoints.remove();
			
			int value = layer.get(point);
			
			for (GridPoint directon : GridUtil.get4WayDirections())
			{
				GridPoint neighboorPoint = point.add(directon);
			
				if (!layer.isValid(neighboorPoint))
				{
					continue;
				}
				
				int pointValue = layer.get(neighboorPoint);
				if (pointValue == fillableValue)
				{
					layer.set(neighboorPoint, value);
					continuePoints.add(neighboorPoint);
				}
			}
		}
	}
}
