package sk.hackcraft.bwu.selection;

import java.util.Set;

import jnibwapi.Unit;

public interface Picker
{
	Unit pickFrom(Set<Unit> units);
}
