package sk.hackcraft.bwu.selection;

import java.util.Set;

import jnibwapi.Unit;

public class Pickers
{
	public static Picker FIRST = new Picker()
	{
		@Override
		public Unit pickFrom(Set<Unit> units)
		{
			if (units.isEmpty())
			{
				return null;
			}
			else
			{
				return units.iterator().next();
			}
		}
	};
}
