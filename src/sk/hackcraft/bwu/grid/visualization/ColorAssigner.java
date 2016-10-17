package sk.hackcraft.bwu.grid.visualization;

public interface ColorAssigner<C>
{
	C assignColor(int value);

	@FunctionalInterface
	public interface ColorFactory<C>
	{
		C create(int r, int g, int b, int a);

		default C create(int r, int g, int b)
		{
			return create(r, g, b, 0);
		}
	}
}
