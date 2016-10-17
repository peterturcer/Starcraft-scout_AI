package sk.hackcraft.bwu.grid;

import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import sk.hackcraft.bwu.grid.GridIterator.IterateListener;

/**
 * Definition of two dimensional grid holding integer values.
 */
public interface Grid
{
	/**
	 * Returns dimension of this grid.
	 * 
	 * @return dimension of this grid
	 */
	GridDimension getDimension();

	/**
	 * Checks if specified point is inside grid dimension and thus valid.
	 * 
	 * @param point
	 *            point to check
	 * @return validness of specified point
	 */
	default boolean isValid(GridPoint point)
	{
		GridDimension dimension = getDimension();

		int x = point.getX();
		if (x < 0 || x >= dimension.getWidth())
		{
			return false;
		}

		int y = point.getY();
		if (y < 0 || y >= dimension.getHeight())
		{
			return false;
		}

		return true;
	}

	/**
	 * Returns value saved at specified x,y coordinates.
	 * 
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @return value saved at specified x,y coordinates
	 */
	int get(int x, int y);

	/**
	 * Returns value saved at specified point.
	 * @param point specified point
	 * @return value saved at specified point
	 */
	default int get(GridPoint point)
	{
		return get(point.getX(), point.getY());
	}

	/**
	 * Sets value at specified x,y coordinates.
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param value value to set
	 */
	void set(int x, int y, int value);

	/**
	 * Sets value at specified point.
	 * @param point specified point
	 * @param value value to set
	 */
	default void set(GridPoint point, int value)
	{
		set(point.getX(), point.getY(), value);
	}

	/**
	 * Creates and returns {@link GridIterator} instance for iterating over this layer.
	 * @param listener iterator listener
	 * @return grid iterator of this grid
	 */
	GridIterator createLayerIterator(IterateListener listener);

	/**
	 * Returns copy of this grid.
	 * @return copy of this grid
	 */
	Grid copy();

	/**
	 * Applies modifier to this grid.
	 * @param operator operator used in modifying grid values
	 * @return this grid, so operations can be chained
	 */
	default public Grid modify(UnaryOperator<Integer> operator)
	{
		GridDimension dimension = getDimension();
		for (int x = 0; x < dimension.getWidth(); x++)
		{
			for (int y = 0; y < dimension.getHeight(); y++)
			{
				int value = operator.apply(get(x, y));
				set(x, y, value);
			}
		}

		return this;
	}

	/**
	 * Combines this grid with another one.
	 * @param operator operator used in combination
	 * @return this grid, so operations can be chained
	 */
	default public Grid combine(BinaryOperator<Integer> operator, Grid grid)
	{
		GridDimension dimension = getDimension();
		for (int x = 0; x < dimension.getWidth(); x++)
		{
			for (int y = 0; y < dimension.getHeight(); y++)
			{
				int value = operator.apply(get(x, y), grid.get(x, y));
				set(x, y, value);
			}
		}

		return this;
	}

	/**
	 * Adds another grid to this grid.
	 * @param grid grid to add
	 * @return this grid, so operations can be chained
	 */
	default public Grid add(Grid grid)
	{
		return combine((v1, v2) -> v1 + v2, grid);
	}

	/**
	 * Subtracts another grid from this grid.
	 * @param grid grid to subtract
	 * @return this grid, so operations can be chained
	 */
	default public Grid sub(Grid grid)
	{
		return combine((v1, v2) -> v1 - v2, grid);
	}

	/**
	 * Multiplies this grid with another grid.
	 * @param grid grid to multiply with
	 * @return this grid, so operations can be chained
	 */
	default public Grid mul(Grid grid)
	{
		return combine((v1, v2) -> v1 * v2, grid);
	}

	/**
	 * Divides this grid by another grid.
	 * @param grid grid to divide with
	 * @return this grid, so operations can be chained
	 */
	default public Grid div(Grid grid)
	{
		return combine((v1, v2) -> v1 / v2, grid);
	}
}
