package sk.hackcraft.bwu.grid.visualization;

import java.util.HashMap;

import jnibwapi.util.BWColor;
import sk.hackcraft.bwu.Graphics;
import sk.hackcraft.bwu.Vector2D;
import sk.hackcraft.bwu.grid.Grid;
import sk.hackcraft.bwu.grid.GridDimension;
import sk.hackcraft.bwu.grid.GridPoint;
import sk.hackcraft.bwu.grid.visualization.colorassigners.MapExactColorAssigner;

public class LayerColorDrawable extends LayerDrawable
{
	public static LayerColorDrawable createBoolean(Grid layer, int cellDimension)
	{
		HashMap<Integer, BWColor> colors = new HashMap<>();
		colors.put(0, BWColor.Black);
		colors.put(0, BWColor.White);
		ColorAssigner<BWColor> colorAssigner = new MapExactColorAssigner<>(colors);
		
		return new LayerColorDrawable(layer, cellDimension, colorAssigner);
	}
	
	private final ColorAssigner<BWColor> colorAssigner;

	public LayerColorDrawable(Grid layer, int cellDimension, ColorAssigner<BWColor> colorAssigner)
	{
		super(layer, cellDimension);
		
		this.colorAssigner = colorAssigner;
	}

	@Override
	protected void drawCell(Graphics graphics, GridPoint cellPosition, GridDimension cellDimension, int value)
	{
		Vector2D topLeftCorner = new Vector2D(cellPosition.getX() + 1, cellPosition.getY() + 1);
		Vector2D bottomRightCorner = new Vector2D(cellPosition.getX() + cellDimension.getWidth() - 1, cellPosition.getY() + cellDimension.getHeight() - 1);
		
		BWColor color = colorAssigner.assignColor(value);
		
		if (color == null)
		{
			return;
		}
		
		graphics.setColor(color);
		graphics.drawBox(topLeftCorner, bottomRightCorner);
	}
}
