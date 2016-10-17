package sk.hackcraft.bwu.grid.visualization.colorassigners;

import java.util.Map;
import java.util.TreeMap;

import sk.hackcraft.bwu.grid.visualization.ColorAssigner;

public class MapGradientColorAssignment<C> implements ColorAssigner<C>
{
	private final TreeMap<Integer, C> colors;

	public MapGradientColorAssignment(TreeMap<Integer, C> colors)
	{
		this.colors = colors;
	}
	
	@Override
	public C assignColor(int value)
	{
		Map.Entry<Integer, C> entry = colors.floorEntry(value);
		
		if (entry == null)
		{
			return null;
		}
		else
		{
			return entry.getValue();
		}
	}
}
