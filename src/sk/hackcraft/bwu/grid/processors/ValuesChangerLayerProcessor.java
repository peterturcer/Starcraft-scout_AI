package sk.hackcraft.bwu.grid.processors;

import java.util.Map;

import sk.hackcraft.bwu.grid.Grid;
import sk.hackcraft.bwu.grid.GridIterator;
import sk.hackcraft.bwu.grid.GridPoint;
import sk.hackcraft.bwu.grid.grids.MatrixGrid;

public class ValuesChangerLayerProcessor
{
	private final Map<Integer, Integer> changeMap;

	public ValuesChangerLayerProcessor(Map<Integer, Integer> changeMap)
	{
		this.changeMap = changeMap;
	}

	public Grid process(Grid layer)
	{
		final MatrixGrid newLayer = new MatrixGrid(layer.getDimension());
		
		layer.createLayerIterator(new GridIterator.IterateListener()
		{
			
			@Override
			public void nextCell(GridPoint cellCoordinates, int cellValue)
			{
				if (changeMap.containsKey(cellValue))
				{
					int newValue = changeMap.get(cellValue);
					newLayer.set(cellCoordinates, newValue);
				}
				else
				{
					newLayer.set(cellCoordinates, cellValue);
				}
			}
		}).iterateAll();
		
		return newLayer;
	}

}
