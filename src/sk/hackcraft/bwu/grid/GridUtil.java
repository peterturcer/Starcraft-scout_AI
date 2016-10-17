package sk.hackcraft.bwu.grid;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class for grids containing static methods for common operations.
 */
public class GridUtil
{
	private static final GridPoint[] DIRECTIONS = { new GridPoint(1, 0), new GridPoint(0, 1), new GridPoint(-1, 0), new GridPoint(0, -1) };

	/**
	 * Returns array containing 4 way normalized direction vectors. These
	 * directions are right, top, left, bottom.
	 * 
	 * @return array containing 4 way normalized direction vectors
	 */
	public static GridPoint[] get4WayDirections()
	{
		return Arrays.copyOf(DIRECTIONS, DIRECTIONS.length);
	}

	/**
	 * <p>
	 * Copies specified value range from one grid to another. Copied area is
	 * defined as rectangle relative to source grid.
	 * </p>
	 * 
	 * <p>
	 * If source grid is smaller than destination grid, copied data will be
	 * aligned to top left corner of destination grid. If destination grid is
	 * smaller, data outside of the destination will be discarded.
	 * </p>
	 * 
	 * @param source
	 *            source grid
	 * @param destination
	 *            destination grid
	 * @param offsetX
	 *            copy rectangle upper left corner x
	 * @param lengthX
	 *            copy rectangle width
	 * @param offsetY
	 *            copy rectangle upper left corner y
	 * @param lengthY
	 *            copy rectangle height
	 */
	public static void copy(Grid source, Grid destination, int offsetX, int lengthX, int offsetY, int lengthY)
	{
		int thisX = 0, thisY = 0;

		for (int x = 0; x < source.getDimension().getWidth(); x++)
		{
			if (x < offsetX || x >= offsetX + lengthX)
			{
				continue;
			}

			for (int y = 0; y < source.getDimension().getHeight(); y++)
			{
				if (y < offsetY || y >= offsetY + lengthY)
				{
					continue;
				}

				int value = source.get(new GridPoint(x, y));
				destination.set(thisX, thisY, value);

				thisY++;
			}

			thisX++;
			thisY = 0;
		}
	}

	/**
	 * <p>
	 * Copies specified value range from one grid to another. Copied area is
	 * defined as rectangle relative to source grid.
	 * </p>
	 * 
	 * <p>
	 * If source grid is smaller than destination grid, copied data will be
	 * aligned to top left corner of destination grid. If destination grid is
	 * smaller, data outside of the destination will be discarded.
	 * </p>
	 * 
	 * @param source
	 *            source grid
	 * @param destination
	 *            destination grid
	 * @param copyBounds
	 *            copy area boundary
	 */
	public static void copy(Grid source, Grid destination, Bounds copyBounds)
	{
		GridPoint p = copyBounds.getPosition();
		GridDimension d = copyBounds.getDimension();

		copy(source, destination, p.getX(), d.getWidth(), p.getY(), d.getHeight());
	}

	/**
	 * <p>
	 * Copies source grid to destination grid.
	 * </p>
	 * 
	 * <p>
	 * If source grid is smaller than destination grid, copied data will be
	 * aligned to top left corner of destination grid. If destination grid is
	 * smaller, data outside of the destination will be discarded.
	 * </p>
	 * 
	 * @param source
	 *            source grid
	 * @param destination
	 *            destination grid
	 */
	public static void copy(Grid from, Grid to)
	{
		copy(from, to, 0, from.getDimension().getWidth(), 0, from.getDimension().getHeight());
	}

	/**
	 * Fills entire grid with specified value.
	 * 
	 * @param grid grid to fill
	 * @param fillValue filling value
	 */
	public static void fill(final Grid grid, final int fillValue)
	{
		grid.createLayerIterator(new GridIterator.IterateListener()
		{
			@Override
			public void nextCell(GridPoint cellCoordinates, int cellValue)
			{
				grid.set(cellCoordinates, fillValue);
			}
		}).iterateAll();
	}

	/**
	 * Query method to retrieve all points containing specified value.
	 * @param grid searched grid
	 * @param value specified value
	 * @return
	 */
	public static Set<GridPoint> getPointsWithValue(Grid grid, final int value)
	{
		final Set<GridPoint> points = new HashSet<>();

		grid.createLayerIterator(new GridIterator.IterateListener()
		{

			@Override
			public void nextCell(GridPoint cellCoordinates, int cellValue)
			{
				if (cellValue == value)
				{
					points.add(cellCoordinates);
				}
			}
		}).iterateAll();

		return points;
	}
}
