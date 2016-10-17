package sk.hackcraft.bwu.grid;

/**
 * Definition of grid dimension.
 */
public class GridDimension
{
	private final int width, height;

	/**
	 * Constructs new dimension with specified width and height.
	 * @param width width of dimension
	 * @param height height of dimension
	 */
	public GridDimension(int width, int height)
	{
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Returns width of this dimension
	 * @return width of this dimension
	 */
	public int getWidth()
	{
		return width;
	}

	/**
	 * Returns height of this dimension
	 * @return height of this dimension
	 */
	public int getHeight()
	{
		return height;
	}
	
	/**
	 * Check equality of dimensions. Dimensions are equals if they are of same type and their width and height are equal.
	 * @return <code>true</code> if dimensions are equal, <code>false</code> otherwise
	 */
	@Override
	public boolean equals(Object object)
	{
		if (!(object instanceof GridDimension))
		{
			return false;
		}
		
		GridDimension dimension = (GridDimension)object;
		
		return dimension.width == width && dimension.height == height;
	}
	
	@Override
	public int hashCode()
	{
		return width * 31 + height;
	}
	
	@Override
	public String toString()
	{
		return String.format("Dimension %dx%d", width, height);
	}
}
