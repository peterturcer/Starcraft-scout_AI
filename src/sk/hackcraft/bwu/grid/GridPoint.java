package sk.hackcraft.bwu.grid;

/**
 * Data structure defining position in grid.
 */
public class GridPoint
{
	/**
	 * Origin point pointing to [0,0] coordinates.
	 */
	public static final GridPoint ORIGIN = new GridPoint(0, 0);

	private final int x, y;

	/**
	 * Constructs new point with specified coordiantes.
	 * 
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 */
	public GridPoint(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	/**
	 * Returns x coordinate.
	 * 
	 * @return x coordinate
	 */
	public int getX()
	{
		return x;
	}

	/**
	 * Returns y coordinate.
	 * 
	 * @return y coordinate
	 */
	public int getY()
	{
		return y;
	}

	/**
	 * Adds specified point to this point and returns result.
	 * 
	 * @param point
	 *            point to add
	 * @return sum of two points
	 */
	public GridPoint add(GridPoint point)
	{
		return new GridPoint(x + point.x, y + point.y);
	}

	/**
	 * Subtracts specified point from this point and returns result.
	 * 
	 * @param point
	 *            point to subtract
	 * @return subtraction of two points
	 */
	public GridPoint sub(GridPoint point)
	{
		return new GridPoint(x - point.x, y - point.y);
	}

	/**
	 * Multiplies this point with specified point and returns result.
	 * 
	 * @param point
	 *            point to multiply with
	 * @return multiplication of two points
	 */
	public GridPoint mul(GridPoint point)
	{
		return new GridPoint(x * point.x, y * point.y);
	}

	/**
	 * Divides this point with specified point and returns result.
	 * 
	 * @param point
	 *            point to divide with
	 * @return division of two points
	 */
	public GridPoint div(GridPoint point)
	{
		return new GridPoint(x / point.x, y / point.y);
	}

	/**
	 * Checks if two points are equal. Points are equal if they are of same type
	 * and their X and Y coordinates are equal.
	 * @return <code>true</code> if points are equal, <code>false</code> otherwise
	 */
	@Override
	public boolean equals(Object object)
	{
		if (!(object instanceof GridPoint))
		{
			return false;
		}

		GridPoint point = (GridPoint) object;

		return point.x == x && point.y == y;
	}

	@Override
	public int hashCode()
	{
		return x * 31 + y;
	}

	@Override
	public String toString()
	{
		return String.format("LayerPoint [%d,%d]", x, y);
	}
}
