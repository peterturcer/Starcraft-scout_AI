package sk.hackcraft.bwu;

import jnibwapi.Unit;

// TODO
public interface BaseListener
{
	void unitCreated(Unit unit);
	void unitDestroyed(Unit unit);
	
	void enemyEnteredPerimeter(Unit unit);
	void enemyLeftPerimeter(Unit unit);
	
	void unitUnderAttack(Unit unit, Unit attacker);
}
