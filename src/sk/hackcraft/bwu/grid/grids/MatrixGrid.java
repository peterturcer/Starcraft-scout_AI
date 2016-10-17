package sk.hackcraft.bwu.grid.grids;

import java.util.Arrays;

import sk.hackcraft.bwu.grid.AbstractLayer;
import sk.hackcraft.bwu.grid.Grid;
import sk.hackcraft.bwu.grid.GridDimension;
import sk.hackcraft.bwu.grid.GridIterator;
import sk.hackcraft.bwu.grid.GridIterator.IterateListener;

public class MatrixGrid extends AbstractLayer
{
	private final int[] data;
	
	public MatrixGrid(GridDimension dimension)
	{
		super(dimension);
		
		this.data = new int[dimension.getWidth() * dimension.getHeight()];
	}
	
	private MatrixGrid(GridDimension dimension, int[] data)
	{
		super(dimension);
		
		this.data = Arrays.copyOf(data, data.length);
	}
	
	@Override
	public Grid copy()
	{
		return new MatrixGrid(dimension, data);
	}
	
	@Override
	public int get(int x, int y)
	{
		int i = positionToIndex(x, y);
		return data[i];
	}
	
	@Override
	public void set(int x, int y, int value)
	{
		int i = positionToIndex(x, y);
		data[i] = value;
	}
	
	private int positionToIndex(int x, int y)
	{
		int width = dimension.getWidth();
		return  y * width + x;
	}
	
	@Override
	public String toString()
	{
		GridDimension dimension = getDimension();
		return String.format("Layer %dx%d", dimension.getWidth(), dimension.getHeight());
	}
	
	@Override
	public GridIterator createLayerIterator(IterateListener listener)
	{
		return new GridIterator(this, listener)
		{
			@Override
			public void iterateFeature()
			{
				iterateAll();
			}
		};
	}
	
	public static void main(String[] args)
	{
		new MatrixGrid(new GridDimension(10, 5)).combine((v1, v2) -> v1 + v2, null);
	}
}
