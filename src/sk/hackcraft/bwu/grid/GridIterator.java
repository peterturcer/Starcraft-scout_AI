package sk.hackcraft.bwu.grid;


public abstract class GridIterator
{
	private final Grid layer;
	protected final IterateListener listener;

	public GridIterator(Grid layer, IterateListener listener)
	{
		this.layer = layer;
		this.listener = listener;
	}

	public void iterateAll()
	{
		GridDimension dimension = layer.getDimension();
		for (int x = 0; x < dimension.getWidth(); x++)
		{
			for (int y = 0; y < dimension.getHeight(); y++)
			{
				GridPoint coordinates = new GridPoint(x, y);
				int value = layer.get(coordinates);
				
				listener.nextCell(coordinates, value);
			}
		}
	}
	
	public abstract void iterateFeature();

	public interface IterateListener
	{
		void nextCell(GridPoint cellCoordinates, int cellValue);
	}
}
