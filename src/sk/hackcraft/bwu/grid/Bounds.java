package sk.hackcraft.bwu.grid;

/**
 * Definition of rectangle bounds relative to grid. Bounds position is used relative to grid origin point.
 */
public class Bounds
{
	private final GridPoint position;
	private final GridDimension dimension;
	
	/**
	 * Constructs new bounds with specified position and dimension.
	 * @param position bounds position
	 * @param dimension bounds dimension
	 */
	public Bounds(GridPoint position, GridDimension dimension)
	{
		this.position = position;
		this.dimension = dimension;
	}
	
	/**
	 * Constructs new bounds with specified position and dimension.
	 * @param x x coordinate of bounds position
	 * @param y y coordinate of bounds position
	 * @param width bounds width
	 * @param height bounds height
	 */
	public Bounds(int x, int y, int width, int height)
	{
		this(new GridPoint(x, y), new GridDimension(width, height));
	}
	
	/**
	 * Returns bounds position.
	 * @return bounds position
	 */
	public GridPoint getPosition()
	{
		return position;
	}
	
	/**
	 * Returns bounds dimension.
	 * @return bounds dimension
	 */
	public GridDimension getDimension()
	{
		return dimension;
	}
	
	/**
	 * Tests if specified point is inside bounds.
	 * @param point point to test
	 * @return <code>true</code> if point is inside bounds, <code>false</code> otherwise
	 */
	public boolean isInside(GridPoint point)
	{
		int x = point.getX();
		int y = point.getY();
		
		if (x < position.getX() || x >= position.getX() + dimension.getWidth())
		{
			return false;
		}
		
		if (y < position.getY() || y >= position.getY() + dimension.getHeight())
		{
			return false;
		}
		
		return true;
	}
}
