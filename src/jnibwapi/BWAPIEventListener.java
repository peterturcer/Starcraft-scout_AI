package jnibwapi;

/**
 * Interface for BWAPI callback methods;
 * 
 * For BWAPI specific events see: http://code.google.com/p/bwapi/wiki/AIModule
 */
public interface BWAPIEventListener {
	
	/** connection to BWAPI established */
	public void connected();
	
	/** game has just started, game settings can be turned on here */
	public void matchStart();
	
	/** perform AI logic here */
	public void matchFrame();
	
	/** game has just terminated */
	public void matchEnd(boolean winner);
	
	/** keyPressed from within StarCraft */
	public void keyPressed(int keyCode);
	
	// BWAPI callbacks
	public void sendText(String text);
	public void receiveText(String text);
	public void playerLeft(Player player);
	public void nukeDetect(Position p);
	public void nukeDetect();
	public void unitDiscover(Unit unit);
	public void unitEvade(Unit unit);
	public void unitShow(Unit unit);
	public void unitHide(Unit unit);
	public void unitCreate(Unit unit);
	public void unitDestroy(Unit unit);
	public void unitMorph(Unit unit);
	public void unitRenegade(Unit unit);
	public void saveGame(String gameName);
	public void unitComplete(Unit unit);
	public void playerDropped(Player player);
}
