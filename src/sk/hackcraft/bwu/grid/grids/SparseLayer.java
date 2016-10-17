package sk.hackcraft.bwu.grid.grids;

import java.util.HashMap;
import java.util.Map;

import sk.hackcraft.bwu.grid.AbstractLayer;
import sk.hackcraft.bwu.grid.GridDimension;
import sk.hackcraft.bwu.grid.GridIterator;
import sk.hackcraft.bwu.grid.GridPoint;
import sk.hackcraft.bwu.grid.GridIterator.IterateListener;

public class SparseLayer extends AbstractLayer
{
	private Map<GridPoint, Integer> data;
	
	public SparseLayer(GridDimension dimenson)
	{
		super(dimenson);
		
		this.data = new HashMap<GridPoint, Integer>();
	}
	
	private SparseLayer(GridDimension dimension, Map<GridPoint, Integer> data)
	{
		super(dimension);
		
		this.data = new HashMap<>(data);
	}
	
	public Map<GridPoint, Integer> getAsMap()
	{
		return new HashMap<>(data);
	}
	
	@Override
	public SparseLayer copy()
	{
		return new SparseLayer(dimension, data);
	}

	@Override
	public int get(int x, int y)
	{
		GridPoint point = new GridPoint(x, y);
		return get(point);
	}
	
	@Override
	public int get(GridPoint coordinates)
	{
		if (!data.containsKey(coordinates))
		{
			return 0;
		}
		else
		{
			return data.get(coordinates);
		}
	}

	@Override
	public void set(int x, int y, int value)
	{
		GridPoint point = new GridPoint(x, y);
		set(point, value);
	}
	
	@Override
	public void set(GridPoint coordinates, int value)
	{
		if (value == 0)
		{
			data.remove(coordinates);
		}
		else
		{
			data.put(coordinates, value);
		}
	}
	
	@Override
	public GridIterator createLayerIterator(IterateListener listener)
	{
		return new GridIterator(this, listener)
		{
			@Override
			public void iterateFeature()
			{
				for (Map.Entry<GridPoint, Integer> entry : data.entrySet())
				{
					listener.nextCell(entry.getKey(), entry.getValue());
				}
			}
		};
	}
	
	protected void clear()
	{
		data.clear();
	}
}
