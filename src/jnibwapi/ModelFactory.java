package jnibwapi;

public interface ModelFactory {
	public Unit createUnit(int id, JNIBWAPI jnibwapi);
	public Player createPlayer(int[] data, int index, String name);
}
