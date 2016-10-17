package sk.hackcraft.bwu.grid.processors;

import sk.hackcraft.bwu.grid.Grid;
import sk.hackcraft.bwu.grid.GridDimension;
import sk.hackcraft.bwu.grid.GridPoint;
import sk.hackcraft.bwu.grid.grids.MatrixGrid;

public class BorderLayerProcessor
{
	public static Grid createBorder(Grid layer, int newValue, int sourceValue, Grid output)
	{
		GridDimension dimension = layer.getDimension();
		
		GridPoint[] directions = {
			new GridPoint( 1,  0),
			new GridPoint( 0,  1),
			new GridPoint(-1,  0),
			new GridPoint( 0, -1),
		};

		for (int x = 0; x < dimension.getWidth(); x++)
		{
			for (int y = 0; y < dimension.getHeight(); y++)
			{
				GridPoint cellPosition = new GridPoint(x, y);
				
				if (layer.get(cellPosition) != sourceValue)
				{
					continue;
				}
				
				boolean border = false;
				
				for (GridPoint direction : directions)
				{
					GridPoint point = cellPosition.add(direction);
					
					if (!layer.isValid(point))
					{
						continue;
					}
					
					if (layer.get(point) != sourceValue)
					{
						border = true;
						break;
					}
				}
				
				if (border)
				{
					output.set(cellPosition, newValue);
				}
			}
		}
		
		return output;
	}
}
