package sk.hackcraft.bwu;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import jnibwapi.Position;
import jnibwapi.Position.PosType;
import sk.hackcraft.bwu.grid.GridDimension;
import sk.hackcraft.bwu.grid.GridPoint;

public class Convert
{
	public static Vector2D toPositionVector(Position p)
	{
		return Vector2DMath.toVector(p, PosType.PIXEL);
	}
	
	public static Position toPosition(Vector2D vector)
	{
		int x = Math.round(vector.getX());
		int y = Math.round(vector.getY());
		
		return new Position(x, y);		
	}
	
	public static GridPoint toGridPoint(Position position)
	{
		int x = position.getBX();
		int y = position.getBY();
		
		return new GridPoint(x, y);
	}

	public static GridPoint toGridPoint(Vector2D vector, float scaleX, float scaleY)
	{
		int x = (int)(vector.getX() * scaleX);
		int y = (int)(vector.getY() * scaleY);
		
		return new GridPoint(x, y);
	}
	
	public static GridPoint toGridPoint(Vector2D vector, Vector2D scale)
	{
		float scaleX = scale.getX();
		float scaleY = scale.getY();

		return toGridPoint(vector, scaleX, scaleY);
	}
	
	public static GridDimension toGridDimension(Position position)
	{
		int width = position.getBX();
		int height = position.getBY();
		
		return new GridDimension(width, height);
	}
	
	private static final Map<Object, Map<Object, Object>> converters = new HashMap<>();
	
	public static <V, T> void addConverter(Class<V> fromType, Class<T> toType, Converter<V, T> converter)
	{
		if (!converters.containsKey(fromType))
		{
			converters.put(fromType, new HashMap<>());
		}
		
		converters.get(fromType).put(toType, converter);
	}
	
	public static void main(String[] args)
	{
		addConverter(Integer.class, String.class, (i) -> i.toString());
		addConverter(String.class, Integer.class, (s) -> Integer.parseInt(s));
		addConverter(Position.class, Vector2D.class, (p) -> { return new Vector2D(p.getPX(), p.getPY());});
		
		Integer i = 3;
		String s = Convert.convert(i, String.class);
		System.out.println(s);
		
		i = Convert.convert(s, Integer.class);
		System.out.println(i);
		
		Position p = new Position(30, 10);
		Vector2D v = Convert.convert(p, Vector2D.class);
		System.out.println(v);
	}
	
	public static <V, T> T convert(V value, Class<T> type)
	{
		Map<Object, Object> typeConverters = converters.get(value.getClass());
		
		if (typeConverters == null)
		{
			throw new NoSuchElementException("No converters from type " + value.getClass());
		}
		
		Converter converter = (Converter)typeConverters.get(type);
		
		if (converter == null)
		{
			throw new NoSuchElementException("No converter to type " + type);
		}
		
		return (T)converter.convert(value);
	}
	
	@FunctionalInterface
	public interface Converter<V, T>
	{
		T convert(V value);
	}
}
