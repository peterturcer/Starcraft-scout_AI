package sk.hackcraft.bwu.grid;


public abstract class AbstractLayer implements Grid
{	
	protected final GridDimension dimension;
	
	public AbstractLayer(GridDimension dimension)
	{
		this.dimension = dimension;
	}

	@Override
	public GridDimension getDimension()
	{
		return dimension;
	}
	
	@Override
	public String toString()
	{
		GridDimension dimension = getDimension();
		return String.format("Layer %dx%d", dimension.getWidth(), dimension.getHeight());
	}
}
