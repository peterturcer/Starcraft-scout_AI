package jnibwapi;

public class DefaultModelFactory implements ModelFactory {
	@Override
	public Unit createUnit(int id, JNIBWAPI jnibwapi) {
		return new Unit(id, jnibwapi);
	}

	@Override
	public Player createPlayer(int[] data, int index, String name) {
		return new Player(data, index, name);
	}
}
