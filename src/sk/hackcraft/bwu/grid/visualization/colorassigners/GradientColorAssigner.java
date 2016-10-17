package sk.hackcraft.bwu.grid.visualization.colorassigners;

import sk.hackcraft.bwu.grid.visualization.AbstractColorAssigner;

public class GradientColorAssigner<C> extends AbstractColorAssigner<C>
{
	private int from, to;
	
	public GradientColorAssigner(ColorFactory<C> colorCreator, int from, int to)
	{
		super(colorCreator);
		
		this.from = from;
		this.to = to;
	}

	@Override
	public C assignColor(int value)
	{
		int lightness;
		if (value < from)
		{
			lightness = 0;
		}
		else if (value > to)
		{
			lightness = 255;
		}
		else
		{ 
			int range = to - from;
			int rangeValue = value - from;
			
			int percentage = rangeValue * 100 / range;
			lightness = 255 * percentage / 100;
		}
		
		return getColorCreator().create(lightness, lightness, lightness, 255);
	}
}
