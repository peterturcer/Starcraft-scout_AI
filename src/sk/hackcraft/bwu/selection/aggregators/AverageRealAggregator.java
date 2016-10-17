package sk.hackcraft.bwu.selection.aggregators;

import jnibwapi.Unit;
import sk.hackcraft.bwu.selection.UnitSelector.RealSelector;
import sk.hackcraft.bwu.selection.UnitSet;

public class AverageRealAggregator implements Aggregator<Double>
{
	private RealSelector selector;
	
	public AverageRealAggregator(RealSelector selector)
	{
		this.selector = selector;
	}
	
	public Double aggregate(UnitSet units)
	{
		double accumulated = 0;
		
		for(Unit unit : units)
		{
			accumulated += selector.getValue(unit);
		}
		
		if(units.size() > 0)
		{
			accumulated /= units.size();
		}
		
		return accumulated;
	}
}
