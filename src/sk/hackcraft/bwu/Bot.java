package sk.hackcraft.bwu;

import jnibwapi.Player;
import jnibwapi.Unit;

/**
 * <p>
 * Interface providing callbacks received from BWAPI inteface.
 * </p>
 * 
 * <p>
 * For player view, game can be run in two modes: complete or incomplete map
 * informaton. When complete map information mode is active, all enemy units and
 * theirs data are available to player. Callbacks are also influenced, for
 * example callbacks dealing with unit visibility are called right when unit is
 * created or destroyed, as fog of war is non-existing in this mode. In
 * incomplete mode, non-player units are accessible only when they are visible
 * to the player, and even only som informations about them are available, as
 * hit points or resources count (others, as training units or energy are still
 * unavailable).
 * </p>
 */
public interface Bot
{
	/**
	 * Called when game starts. During this callback all game informations
	 * regaded to actual match are accessible and valid.
	 */
	void gameStarted();

	/**
	 * Called when game ends. This callback can be used for saving data gathered
	 * during the match.
	 * 
	 * @param <code>true</code> if bot is winner, <code>false</code> otherwise
	 */
	void gameEnded(boolean isWinner);

	/**
	 * Called every logical frame. This is used for updating bot state.
	 */
	void gameUpdated();

	/**
	 * Called when key is pressed.
	 * 
	 * @param keyCode
	 *            code identification of pressed key
	 */
	void keyPressed(int keyCode);

	/**
	 * Called when player lefts the game.
	 * 
	 * @param player
	 *            player which left
	 */
	void playerLeft(Player player);

	/**
	 * Called when player was dropped from the game.
	 * 
	 * @param player
	 *            player which was dropped
	 */
	void playerDropped(Player player);

	/**
	 * <p>
	 * Called when nuclear strike is detected. If it is visible for player, its
	 * position will also be provided.
	 * </p>
	 * 
	 * <p>
	 * BWAPI calls this when a nuclear launch has been detected.
	 * </p>
	 * 
	 * @param position
	 *            position of nuclear strike, or <code>null</code> if strike
	 *            information is unavailable for player
	 */
	void nukeDetected(Vector2D target);

	/**
	 * Called when a unit becomes accessible. This is called after
	 * {@link #unitShowed(Unit)} or {@link #unitCreated(Unit)} callbacks.
	 * 
	 * @param unit
	 *            unit that was discovered
	 */
	void unitDiscovered(Unit unit);

	/**
	 * <p>
	 * Called when unit is destroyed or removed from the game.
	 * </p>
	 * 
	 * <p>
	 * If unit information was available to the player at the time of
	 * destruction, {@link #unitHid(Unit)} will also be called.
	 * </p>
	 * 
	 * <p>
	 * <b>Note:</b> Some operations which looks like unit destruction/removing
	 * are actually not removing unit; examples are morphing unitss, when unit
	 * is actually changed to another unit. Exception is morphing of zerg extractor
	 * building, when actually drone is destroyed and vespene geyser is changed
	 * to extractor.
	 * </p>
	 * 
	 * @param unit
	 *            unit that was destroyed
	 */
	void unitDestroyed(Unit unit);

	/**
	 * Called when unit becomes inaccessible. This is called after
	 * {@link #unitHid(Unit)} or {@link #unitDestroyed(Unit)} callbacks.
	 * 
	 * @param unit
	 *            unit that was evaded
	 */
	void unitEvaded(Unit unit);

	/**
	 * <p>
	 * Called when accessible unit is created.
	 * </p>
	 * 
	 * <p>
	 * <b>Note:</b> this is not called when unit is changing type, as when zerg
	 * unit is morphing, or vespene geyser is changed to gas extracting
	 * building.
	 * </p>
	 * 
	 * @param unit
	 *            unit that was created
	 * 
	 * @see #unitShowed(Unit)
	 */
	void unitCreated(Unit unit);

	/**
	 * Called when unit construction or training is completed. This doesn't count morphed units.
	 * 
	 * @param unit
	 *            unit that was completed
	 */
	void unitCompleted(Unit unit);

	/**
	 * Called when unit is morphed (changed it's type) to another unit type.
	 * 
	 * @param unit
	 *            unit that was morphed
	 */
	void unitMorphed(Unit unit);

	/**
	 * Called when unit becomes visible to player.
	 * 
	 * @param unit
	 *            unit that was shown
	 */
	void unitShowed(Unit unit);

	/**
	 * Called when unit becomes invisible to player.
	 * 
	 * @param unit
	 *            unit that was hidden
	 */
	void unitHid(Unit unit);

	/**
	 * Called when unit changes its ownership.
	 * 
	 * @param unit
	 *            unit that renegaded
	 */
	void unitRenegaded(Unit unit);

	/**
	 * Called after each {@link #gameUpdated()} call, if drawing is enabled.
	 * 
	 * @param graphics
	 *            state machine for drawing
	 */
	void draw(Graphics graphics);

	/**
	 * If <code>Game.enableUserInput()</code> is enabled, BWAPI will call this
	 * each time a user enters a message into the chat. If you want the message
	 * to actually show up in chat, you can use <code>Game.sendMessage()</code>
	 * to send the message to other players (if the game is multiplayer), or use
	 * <code>Bot.getPrintStream()</code> if you want the message to just show up
	 * locally.
	 * 
	 * @param message
	 *            message to send
	 */
	void messageSent(String message);

	/**
	 * BWAPI calls this each time it receives a message from another player in
	 * the chat.
	 * 
	 * @param message
	 *            message that was sent
	 */
	void messageReceived(String message);

	/**
	 * BWAPI calls this when the user saves the match. The gameName will be the
	 * name that the player entered in the save game screen.
	 * 
	 * @param gameName
	 */
	void gameSaved(String gameName);
}
