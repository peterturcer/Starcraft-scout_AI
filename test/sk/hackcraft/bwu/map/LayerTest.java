package sk.hackcraft.bwu.map;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import sk.hackcraft.bwu.grid.GridDimension;
import sk.hackcraft.bwu.grid.GridUtil;
import sk.hackcraft.bwu.grid.grids.MatrixGrid;

public class LayerTest
{
	@Test
	public void testCreateBlank()
	{
		MatrixGrid layer = new MatrixGrid(new GridDimension(10, 5));
		
		assertEquals(10, layer.getDimension().getWidth());
		assertEquals(5, layer.getDimension().getHeight());
	}
	
	@Test
	public void testCreateCopy()
	{
		MatrixGrid layer = new MatrixGrid(new GridDimension(3, 2));
		
		MatrixGrid layer2 = new MatrixGrid(layer.getDimension());
		GridUtil.copy(layer, layer2);
		
		assertEquals(3, layer2.getDimension().getWidth());
		assertEquals(2, layer2.getDimension().getHeight());
	}
	
	@Test
	public void testCreatePartialCopy()
	{
		MatrixGrid layer = new MatrixGrid(new GridDimension(3, 4));
		layer.set(0,0, 1);
		layer.set(1,1, 2);
		layer.set(2,2, 3);
		layer.set(2, 3, 4);
		
		MatrixGrid layer2 = new MatrixGrid(new GridDimension(2, 2));
		GridUtil.copy(layer, layer2, 1, 2, 1, 2);
		
		assertEquals(2, layer2.getDimension().getWidth());
		assertEquals(2, layer2.getDimension().getHeight());
		
		assertEquals(2, layer2.get(0, 0));
		assertEquals(3, layer2.get(1, 1));
	}
}
