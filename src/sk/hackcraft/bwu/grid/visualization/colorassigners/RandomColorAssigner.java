package sk.hackcraft.bwu.grid.visualization.colorassigners;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import sk.hackcraft.bwu.grid.visualization.AbstractColorAssigner;

public class RandomColorAssigner<C> extends AbstractColorAssigner<C>
{
	private final Random random;
	private final Map<Integer, C> usedColors;
	
	public RandomColorAssigner(Random random, ColorFactory<C> colorCreator)
	{
		super(colorCreator);
		
		this.random = random;

		usedColors = new HashMap<>();
	}
	
	@Override
	public C assignColor(int value)
	{
		if (!usedColors.containsKey(value))
		{
			int r = random.nextInt(256);
			int g = random.nextInt(256);
			int b = random.nextInt(256);
			C color = getColorCreator().create(r, g, b, 0);
			
			usedColors.put(value, color);
		}
		
		return usedColors.get(value);
	}
}
