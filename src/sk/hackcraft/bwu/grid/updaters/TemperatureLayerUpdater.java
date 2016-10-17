package sk.hackcraft.bwu.grid.updaters;

import sk.hackcraft.bwu.grid.Grid;
import sk.hackcraft.bwu.grid.GridIterator;
import sk.hackcraft.bwu.grid.GridPoint;
import sk.hackcraft.bwu.grid.GridUpdater;

public class TemperatureLayerUpdater implements GridUpdater
{
	private final Grid layer;
	private final int temperatureDelta, frameSkip;
	private final int temperatureMin, temperatureMax;
	
	private int frame;
	
	public TemperatureLayerUpdater(Grid layer, int temperatureDelta, int frameSkip, int temperatureMin, int temperatureMax)
	{
		this.layer = layer;
		this.temperatureDelta = temperatureDelta;
		this.frameSkip = frameSkip;
		
		this.temperatureMin = temperatureMin;
		this.temperatureMax = temperatureMax;
	}
	
	@Override
	public void update()
	{
		if (frame % frameSkip == 0)
		{
			layer.createLayerIterator(new GridIterator.IterateListener()
			{
				@Override
				public void nextCell(GridPoint cellCoordinates, int cellValue)
				{
					int newValue = cellValue += temperatureDelta;
					
					if (newValue < temperatureMin)
					{
						newValue = temperatureMin;
					}
					
					if (newValue > temperatureMax)
					{
						newValue = temperatureMax;
					}
					
					layer.set(cellCoordinates, newValue);
				}
			}).iterateAll();
		}
		
		frame++;
	}
}
