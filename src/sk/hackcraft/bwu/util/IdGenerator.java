package sk.hackcraft.bwu.util;

public class IdGenerator
{
	private int nextId = Integer.MIN_VALUE;
	
	public int generateId()
	{
		if (nextId == Integer.MAX_VALUE)
		{
			throw new IndexOutOfBoundsException("Id's are exhausted.");
		}
		
		return nextId++;
	}
}
