package sk.hackcraft.bwu;

import jnibwapi.BWAPIEventListener;
import jnibwapi.JNIBWAPI;
import jnibwapi.Player;
import jnibwapi.Position;
import jnibwapi.Unit;

public abstract class BWU
{
	private final JNIBWAPI jnibwapi;
	
	private Bot bot;
	
	private Game game;

	private Graphics graphics;

	private boolean graphicsEnabled = true;

	public BWU()
	{
		this.jnibwapi = new JNIBWAPI(listener, true);

		graphics = new Graphics(jnibwapi);
	}
	
	public void start()
	{
		jnibwapi.start();
	}
	
	private void connected()
	{
	}
	
	protected abstract Bot createBot(Game game);
	
	private BWAPIEventListener listener = new BWAPIEventListener()
	{
		@Override
		public void connected()
		{
			BWU.this.connected();
		}

		@Override
		public void matchStart()
		{
			game = new Game(jnibwapi);
			
			bot = createBot(game);
			bot.gameStarted();
		}

		@Override
		public void matchFrame()
		{
			bot.gameUpdated();

			if (graphicsEnabled)
			{				
				bot.draw(graphics);
			}
		}

		@Override
		public void keyPressed(int keyCode)
		{
			bot.keyPressed(keyCode);
		}

		@Override
		public void matchEnd(boolean winner)
		{
			bot.gameEnded(winner);
		}

		@Override
		public void playerLeft(Player player)
		{
			bot.playerLeft(player);
		}

		@Override
		public void nukeDetect(Position p)
		{
			if(p.isValid()) {
				bot.nukeDetected(new Vector2D(p.getPX(), p.getPY()));
			} else {
				bot.nukeDetected(null);
			}
		}

		@Override
		public void nukeDetect()
		{
			nukeDetect(null);
		}

		@Override
		public void sendText(String text)
		{
			bot.messageSent(text);
		}

		@Override
		public void receiveText(String text)
		{
			bot.messageReceived(text);
		}

		@Override
		public void unitDiscover(Unit unit)
		{
			bot.unitDiscovered(unit);
		}

		@Override
		public void unitEvade(Unit unit)
		{
			bot.unitEvaded(unit);
		}

		@Override
		public void unitShow(Unit unit)
		{
			bot.unitShowed(unit);
		}

		@Override
		public void unitHide(Unit unit)
		{
			bot.unitHid(unit);
		}

		@Override
		public void unitCreate(Unit unit)
		{
			bot.unitCreated(unit);
		}

		@Override
		public void unitDestroy(Unit unit)
		{
			bot.unitDestroyed(unit);
		}

		@Override
		public void unitMorph(Unit unit)
		{
			bot.unitMorphed(unit);
		}

		@Override
		public void unitRenegade(Unit unit)
		{
			bot.unitRenegaded(unit);
		}

		@Override
		public void saveGame(String gameName)
		{
			bot.gameSaved(gameName);
		}

		@Override
		public void unitComplete(Unit unit)
		{
			bot.unitCompleted(unit);
		}

		@Override
		public void playerDropped(Player player)
		{
			bot.playerDropped(player);
		}
	};
}
