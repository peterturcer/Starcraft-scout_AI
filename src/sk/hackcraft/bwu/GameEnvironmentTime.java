package sk.hackcraft.bwu;

public class GameEnvironmentTime implements EnvironmentTime
{
	private final Game game;
	
	public GameEnvironmentTime(Game game)
	{
		this.game = game;
	}
	
	@Override
	public int getFrame()
	{
		return game.getFrameCount();
	}
}
