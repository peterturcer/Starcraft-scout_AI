package sk.hackcraft.bwu;

import java.io.PrintStream;

import jnibwapi.JNIBWAPI;

/**
 * Class representing a programmed bot able to play many successive games. Bot
 * reacts to certain in-game events through abstract callbacks that are called from
 * BWAPI client that is connected to StarCraft immediately when corresponding event happens.
 * These callbacks are the main way to asynchronously (you don't need to actively 
 * check for changed state) receive information about the game world.
 * 
 * @author nixone
 *
 */
abstract public class AbstractBot implements Bot
{
	protected final JNIBWAPI bwapi;
	protected final Game game;

	private final GraphicsOutputStream graphicsOutputStream = new GraphicsOutputStream();
	protected final PrintStream printStream = new PrintStream(graphicsOutputStream);

	/**
	 * Initializes BWAPI, this doesn't actually connect to BWAPI yet.
	 * In order to start your bot, call <code>start()</code>. This constructor
	 * doesn't enable terrain analyzer by default.
	 */
	public AbstractBot(Game game)
	{
		this.bwapi = game.getJNIBWAPI();
		this.game = game;
	}

	/**
	 * Returns the printing stream which output is directed onto screen through
	 * <code>Graphics</code> when enabled.
	 * 
	 * @return stream for printing any output
	 */
	public PrintStream getPrintStream()
	{
		return printStream;
	}
}