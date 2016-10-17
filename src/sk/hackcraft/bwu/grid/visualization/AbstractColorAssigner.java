package sk.hackcraft.bwu.grid.visualization;

public abstract class AbstractColorAssigner<C> implements ColorAssigner<C>
{
	private final ColorFactory<C> colorCreator;

	public AbstractColorAssigner(ColorFactory<C> colorCreator)
	{
		this.colorCreator = colorCreator;
	}
	
	public ColorFactory<C> getColorCreator()
	{
		return colorCreator;
	}
}
