package sk.hackcraft.bwu;

import jnibwapi.JNIBWAPI;
import jnibwapi.Map;
import jnibwapi.Player;
import sk.hackcraft.bwu.selection.UnitSet;

/**
 * Class representing a single instance of a game match. It is created when the
 * match is started and should be thrown away as the game ends (
 * <code>Bot.onGameEnded()</code>)
 * 
 * @author nixone
 * 
 */
public class Game
{
	static public final int TILE_SIZE = 32;

	private final JNIBWAPI jnibwapi;

	public Game(JNIBWAPI jnibwapi)
	{
		this.jnibwapi = jnibwapi;
	}
	
	public JNIBWAPI getJNIBWAPI()
	{
		return jnibwapi;
	}

	/**
	 * Returns the self player in this game
	 * 
	 * @return
	 */
	public Player getSelf()
	{
		return jnibwapi.getSelf();
	}

	/**
	 * Returns <code>UnitSet</code> containing all current accessible ally
	 * units.
	 * 
	 * @return
	 */
	public UnitSet getAlliedUnits()
	{
		return new UnitSet(jnibwapi.getAlliedUnits());
	}

	/**
	 * Returns <code>UnitSet</code> containing all current accessible my units.
	 * 
	 * @return
	 */
	public UnitSet getMyUnits()
	{
		return new UnitSet(jnibwapi.getMyUnits());
	}

	/**
	 * Returns <code>UnitSet</code> containing all current accessible enemy
	 * units.
	 * 
	 * @return
	 */
	public UnitSet getEnemyUnits()
	{
		return new UnitSet(jnibwapi.getEnemyUnits());
	}

	/**
	 * Returns <code>UnitSet</code> containing all current accessible neutral
	 * units.
	 * 
	 * @return
	 */
	public UnitSet getNeutralUnits()
	{
		return new UnitSet(jnibwapi.getNeutralUnits());
	}
	
	/**
	 * Returns {@link UnitSet} containing static neutral units.
	 * @return set of static neutral units
	 */
	public UnitSet getStaticNeutralUnits()
	{
		return new UnitSet(jnibwapi.getStaticNeutralUnits());
	}
	
	/**
	 * returns {@link UnitSet} containing all units.
	 * @return set of all units
	 */
	public UnitSet getAllUnits()
	{
		return new UnitSet(jnibwapi.getAllUnits());
	}

	/**
	 * Get map info for this game
	 * 
	 * @return
	 */
	public Map getMap()
	{
		return jnibwapi.getMap();
	}

	/**
	 * Sets game speed, as a delay between game updates, therefore 0 means
	 * fastest game. Normal values varies between 10-30 speed.
	 * 
	 * @param delay delay between game updates
	 */
	public void setSpeed(int delay)
	{
		jnibwapi.setGameSpeed(delay);
	}
	
	/**
	 * Enables user to use standard StarCraft User Interface to change the game state, send
	 * messages etc.
	 */
	public void enableUserInput()
	{
		jnibwapi.enableUserInput();
	}

	/**
	 * Enables Complete Map Information
	 */
	public void enablePerfectInformation()
	{
		jnibwapi.enablePerfectInformation();
	}

	/**
	 * Returns the number of frames elapsed so far (number of <code>Bot.onGameUpdate()</code> calls).
	 * @return
	 */
	public int getFrameCount()
	{
		return jnibwapi.getFrameCount();
	}
	
	/**
	 * Sends a message to the standard StarCraft chat.
	 * 
	 * @param message message contents
	 */
	public void sendMessage(String message)
	{
		jnibwapi.sendText(message);
	}
}
