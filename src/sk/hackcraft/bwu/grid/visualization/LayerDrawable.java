package sk.hackcraft.bwu.grid.visualization;

import sk.hackcraft.bwu.Drawable;
import sk.hackcraft.bwu.Graphics;
import sk.hackcraft.bwu.grid.Bounds;
import sk.hackcraft.bwu.grid.Grid;
import sk.hackcraft.bwu.grid.GridDimension;
import sk.hackcraft.bwu.grid.GridPoint;

public abstract class LayerDrawable implements Drawable
{
	private final Grid layer;
	private final GridDimension cellDimension;
	
	private Bounds bounds;
	
	public LayerDrawable(Grid layer, int cellDimension)
	{
		this.layer = layer;
		this.cellDimension = new GridDimension(cellDimension, cellDimension);

		this.bounds = new Bounds(GridPoint.ORIGIN, layer.getDimension());
	}
	
	public void setBounds(Bounds bounds)
	{
		this.bounds = bounds;
	}
	
	@Override
	public void draw(Graphics graphics)
	{
		graphics.setGameCoordinates();
		
		GridDimension dimension = layer.getDimension();
		for (int x = 0; x < dimension.getWidth(); x++)
		{
			for (int y = 0; y < dimension.getHeight(); y++)
			{
				GridPoint cellPosition = new GridPoint(x, y);
				
				if (!bounds.isInside(cellPosition))
				{
					continue;
				}
				
				int value = layer.get(cellPosition);
				
				GridPoint drawPosition = new GridPoint(x * cellDimension.getWidth(), y * cellDimension.getHeight());
				
				drawCell(graphics, drawPosition, cellDimension, value);
			}
		}
	}
	
	protected abstract void drawCell(Graphics graphics, GridPoint cellPosition, GridDimension cellDimension, int value);
}
