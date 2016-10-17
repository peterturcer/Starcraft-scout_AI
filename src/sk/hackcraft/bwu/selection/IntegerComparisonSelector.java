package sk.hackcraft.bwu.selection;

import jnibwapi.Unit;
import sk.hackcraft.bwu.Comparison;
import sk.hackcraft.bwu.selection.UnitSelector.BooleanSelector;
import sk.hackcraft.bwu.selection.UnitSelector.IntegerSelector;

public class IntegerComparisonSelector implements BooleanSelector
{
	private IntegerSelector selector;
	private int value;
	private Comparison comparison;

	public IntegerComparisonSelector(IntegerSelector selector, int value, Comparison comparison)
	{
		if (selector == null)
		{
			throw new NullPointerException("Selector cannot be null");
		}
		if (comparison == null)
		{
			throw new NullPointerException("Comparison cannot be null");
		}

		this.selector = selector;
		this.value = value;
		this.comparison = comparison;
	}

	@Override
	public boolean isTrueFor(Unit unit)
	{
		return comparison.compare(selector.getValue(unit), value);
	}
}
