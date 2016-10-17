package sk.hackcraft.bwu.selection.aggregators;

import sk.hackcraft.bwu.selection.UnitSet;

public interface Aggregator<T>
{
	public T aggregate(UnitSet units);
}
