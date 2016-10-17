package sk.hackcraft.bwu.grid.visualization.colorassigners;

import java.util.Map;

import sk.hackcraft.bwu.grid.visualization.ColorAssigner;

public class MapExactColorAssigner<C> implements ColorAssigner<C>
{
	private final Map<Integer, C> colors;
	
	public MapExactColorAssigner(Map<Integer, C> colors)
	{
		this.colors = colors;
	}

	@Override
	public C assignColor(int value)
	{
		return colors.get(value);
	}
}
