package sk.hackcraft.bwu.grid;

import jnibwapi.Map;
import jnibwapi.Position;
import jnibwapi.Position.PosType;
import sk.hackcraft.bwu.grid.grids.MatrixGrid;

public class GameLayerFactory
{
	public static Grid createBuildableLayer(final Map map)
	{
		Position dimension = map.getSize();
		int width = dimension.getBX();
		int height = dimension.getBY();
		
		MapLayerCreator creator = new MapLayerCreator(width, height)
		{
			@Override
			protected boolean evaluate(int x, int y)
			{
				Position position = new Position(x, y, PosType.BUILD);
				return map.isBuildable(position);
			}
		};
		
		return creator.create();
	}
	
	public static Grid createLowResWalkableLayer(final Map map)
	{
		Position dimension = map.getSize();
		int width = dimension.getBX();
		int height = dimension.getBY();
		
		MapLayerCreator creator = new MapLayerCreator(width, height)
		{
			@Override
			protected boolean evaluate(int x, int y)
			{
				Position position = new Position(x, y, PosType.BUILD);
				return map.isLowResWalkable(position);
			}
		};
		
		return creator.create();
	}
	
	public static Grid createWalkableLayer(final Map map)
	{
		Position dimension = map.getSize();
		int width = dimension.getWX();
		int height = dimension.getWY();
		
		MapLayerCreator creator = new MapLayerCreator(width, height)
		{
			@Override
			protected boolean evaluate(int x, int y)
			{
				Position position = new Position(x, y, PosType.WALK);
				return map.isWalkable(position);
			}
		};
		
		return creator.create();
	}
	
	private static abstract class MapLayerCreator
	{
		private final int width, height;
		
		public MapLayerCreator(int width, int height)
		{
			this.width = width;
			this.height = height;
		}
		
		public Grid create()
		{
			final MatrixGrid layer = new MatrixGrid(new GridDimension(width, height));
			
			layer.createLayerIterator(new GridIterator.IterateListener()
			{
				
				@Override
				public void nextCell(GridPoint cellCoordinates, int cellValue)
				{
					int x = cellCoordinates.getX();
					int y = cellCoordinates.getY();

					int newValue = evaluate(x, y) ? 1 : 0;

					layer.set(cellCoordinates, newValue);
				}
			}).iterateAll();
			
			return layer;
		}
		
		/**
		 * Writes 1 (<code>true</code>) or 0 (<code>false</code>) to the layer on specified position on evaluation of that position from external source.
		 * @param x x coodrinate
		 * @param y y coordinate
		 * @return boolean value depending on evaluation of data from external source
		 */
		protected abstract boolean evaluate(int x, int y);
	}
}
