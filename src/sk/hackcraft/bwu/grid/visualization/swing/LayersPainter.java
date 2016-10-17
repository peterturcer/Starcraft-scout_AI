package sk.hackcraft.bwu.grid.visualization.swing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sk.hackcraft.bwu.Updateable;
import sk.hackcraft.bwu.grid.Grid;
import sk.hackcraft.bwu.grid.GridDimension;
import sk.hackcraft.bwu.grid.GridIterator;
import sk.hackcraft.bwu.grid.GridPoint;
import sk.hackcraft.bwu.grid.visualization.ColorAssigner;

public class LayersPainter implements Updateable
{
	private final List<Grid> layers;
	private final Map<Grid, ColorAssigner<Color>> colorAssigners;
	
	private BufferedImage backBuffer;

	public LayersPainter(GridDimension mapDimension)
	{
		this.layers = new ArrayList<Grid>();
		this.colorAssigners = new HashMap<Grid, ColorAssigner<Color>>();

		backBuffer = new BufferedImage(mapDimension.getWidth(), mapDimension.getHeight(), BufferedImage.TYPE_INT_ARGB);
	}
	
	public synchronized void addLayer(Grid layer, ColorAssigner<Color> colorAssigner)
	{
		layers.add(layer);
		colorAssigners.put(layer, colorAssigner);
	}
	
	public synchronized void addLayer(Grid layer, ColorAssigner<Color> colorAssigner, int index)
	{
		layers.add(index, layer);
		colorAssigners.put(layer, colorAssigner);
	}
	
	public synchronized void removeLayer(Grid layer)
	{
		layers.remove(layer);
		colorAssigners.remove(layer);
	}
	
	@Override
	public synchronized void update()
	{
		final Graphics2D g2d = (Graphics2D)backBuffer.getGraphics();
		
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, backBuffer.getWidth(), backBuffer.getHeight());
		
		for (final Grid layer : layers)
		{
			layer.createLayerIterator(new GridIterator.IterateListener()
			{
				@Override
				public void nextCell(GridPoint cellCoordinates, int cellValue)
				{
					int x = cellCoordinates.getX();
					int y = cellCoordinates.getY();
					
					ColorAssigner<Color> colorAssigner = colorAssigners.get(layer);
					Color color = colorAssigner.assignColor(cellValue);
					
					g2d.setColor(color);
					g2d.fillRect(x, y, 1, 1);
				}
			}).iterateFeature();
		}
	}
	
	public synchronized void drawTo(Graphics2D graphics, java.awt.Dimension dimension)
	{
		int dx1 = 0;
		int dy1 = 0;
		int dx2 = (int)dimension.getWidth();
		int dy2 = (int)dimension.getHeight();
		int sx1 = 0;
		int sy1 = 0;
		int sx2 = backBuffer.getWidth();
		int sy2 = backBuffer.getHeight();

		graphics.drawImage(backBuffer, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
	}
}
