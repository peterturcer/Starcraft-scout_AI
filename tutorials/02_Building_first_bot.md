# Building your first bot

In this tutorial I will guide you from the basics of how a bot works to a fully functional and playing, but not very intelligent bot.

Before we start to write code, you need to understand few things about **BWU-Java**. It's structure is very similar to JNIBWAPI (if you already worked with JNIBWAPI, it will seem very similar but much more comfortable, if you didn't, don't worry, everyhing will be explained later).
 
## Basic classes and their meaning
 
There are few classes that you will meet on daily basis developing with **BWU-Java**.
 
* ``sk.hackcraft.bwu.Bot`` is the class your bot will be extending. You create main function, in which you create your ``SampleBot`` instance (for example). Then you call ``bot.start()`` and everything else is handled inside your ``SampleBot`` object in abstract methods that ``Bot`` provides. It also provides few other funcionality to configure your bot how they behave to the StarCraft environment.
 
* ``sk.hackcraft.bwu.Game`` represents a single game/match that occurs. It contains references about the map you are playing on, players you are against and also methods to retrieve your or enemy units. Most of the things you will need, you will find in this class.
 
* ``sk.hackcraft.bwu.Unit`` represents a single unit in a game. It has many methods for unit manipulation and will be covered in special tutorial. Building are also considered as units in *BWU-Java*.
 
* ``sk.hackcraft.bwu.selection.UnitSet`` represents collection of many units. It is very powerfull class and has a whole framework built around it. For now you just need to know that it implements ``Set<Unit>`` so we can treat it like a traditional Set. Also, the ``Game`` object returns a ``UnitSet`` when you retrieve your or enemy units.


These are the few important classes you will encounter building your sample bot. There are many other utilities, but we will cover them in next tutorials, so hold on.

## Bot
 
Create a new class, for example, ``SampleBot``, that extends ``sk.hackcraft.bwu.Bot``, add unimplemented methods (your IDE should do this for you). Your class should look something like this:
 
```java
public class SampleBot extends Bot {
    public void onConnected() {}
    public void onGameStarted(Game game) {}
        public void onGameEnded() {}
        public void onDisconnected() {}
        public void onGameUpdate() {}
        public void onKeyPressed(int keyCode) {}
        public void onMatchEnded(boolean isWinner) {}
        public void onPlayerLeft(Player player) {}
        public void onNukeDetected(Vector2D position) {}
        public void onUnitDiscovered(Unit unit) {}
        public void onUnitDestroyed(Unit unit) {}
        public void onUnitEvaded(Unit unit) {}
        public void onUnitCreated(Unit unit) {}
        public void onUnitMorphed(Unit unit) {}
        public void onUnitShown(Unit unit) {}
        public void onUnitHidden(Unit unit) {}
        public void onDraw(Graphics graphics) {}
}
```
 
As you can see, there are many empty methods that don't do a thing. Why? There are called *callbacks* (or handlers). The code inside these methods will run, whenever corresponding event occurs in a game. You should never call these methods directly. For example, code inside ``onUnitShown`` is ran whenever some unit appears in a game. The parameter is automatically set to the ``Unit`` object corresponding to the unit that appeared. Because we don't know what we want to do when something happens, every method is empty.
 
However, even if we placed some code to these methods, they would never run, because *we didn't start* this bot yet. But don't worry, it's simple. At the beginning of this class (or any class, but we will do this here) we need to create a ``main`` method that creates a ``SampleBot`` instance and starts it:
 
```java
public class SampleBot extends Bot {
        static public void main(String [] arguments) {
                SampleBot bot = new SampleBot();
                bot.start();
        }
       
        public void onConnected() {}
        public void onGameStarted(Game game) {}
        // ... rest of the empty methods ...
}
```
 
Now your bot is ready. It doesn't do a thing, but the handlers (code inside these empty methods, which will not stay empty as we build our bot) are run every time corresponding events occur in a game.

## Basic flow of the application
 
1. Your ``main`` function is started, you create your ``Bot`` instance and call ``Bot.start()`` on it.
2. **BWU-Java** automatically waits for a StarCraft instance to start, and will connect to it. ``Bot.onConnected()`` is called.
3. You (or ChaosLauncher automatically) select the map, set the game parameters and start the match.
4. ``Bot.onGameStarted(Game game)`` is called. *Be careful, immediately after game is started, there may be no units in a game so far*.
5. Everytime StarCraft updates and re-draws the game, ``Bot.onGameUpdate()`` is called.
6. The match ends (depending on map or game settings). ``Bot.onGameEnded()`` is called.
7. If you start next match in a single StarCraft run, you will go back to number 4. of this list.
8. StarCraft instance is killed, ``Bot.onDisconnected()`` is called.
9. ``Bot.start()`` method ends *(it didn't end until now!)*

## Saving the Game instance for later

When our bot enters the game, we want to have the ability to gather map data or retrieve my or enemy units
for later manipulation. However, we see the ``Game`` instance only in ``Bot.onGameStarted(Game game)`` method.
There are reasons why I chose this way, so you need to save this instance of ``Game`` class for later in your bot:

```java
public class SampleBot extends Bot {
	static public void main(String [] arguments) {
		SampleBot bot = new SampleBot();
		bot.start();
	}
	
	private Game game = null;
	
	public void onConnected() {}
	public void onGameStarted(Game game) {
		this.game = game;		
	}
	public void onGameEnded() {
		this.game = null;
	}
	// ... rest ot the handlers ...
}
```

This way we have a great image whether we are in a game whenever any handler is called (we just test the ``game``
attribute if it is equal to ``null``).
 
## Evaluating the game state
 
There is one method among all of them, which is called more often than any other one. It's ``onGameUpdate()``. This method is called
every single *frame* of the game. *Frame* in the game is the smallest time unit that you can encounter while developing
your bot. In the game, you can see a *frame* as a updating and re-drawing a game state.

In my own experience, I call almost all of my bot logic from this ``onGameUpdate()`` method. There are many other
very useful methods, but if you need to re-evaluate something from the game as often as is possible, you need
to place the evaluation code into this method.
 
```java
public void onGameUpdate() {
        // your game state evaluation code that needs to execute very often
}
```

# Let's dance!

Well, we are through the easy preparation stuff. Now it is time to get our units moving.

It's simple. We want our units to move. For simplification, let's say we want them to move around the map randomly,
attack whatever they meet (just the enemies of course). Sounds easy, right? Let's write it down what we need to do every
game update.

1. We want to **retrieve all our units**. This is easy, we use ``game.getMyUnits()``.
2. We **loop over every our unit**, and for each unit, we do step 3.
3. **Check, if unit is idle or stuck**. This step is really important, so we don't make new orders to every
unit every single frame. If we would do so, our unit would get stuck and look schizophrenic. Most of the times
the unit would not move at all (there should be few */at least 10-20/* frame spaces between orders on a single unit).
4. **Generate random position within a map**. ``game.getMap()`` has two methods, ``.getWidth()`` and ``.getHeight()`` which
returns the width and height of the map in tiles. *Be careful, positions to units are given in pixels. One tile is 32x32 pixels*
5. Give the **attack-move order to unit at a generated position**. This ensures that unit is no longer idle, so next frame
we are clear to loop over this unit again.

Basic skeleton code for looping and checking for every unit should then look like this:

```java
	public void onGameUpdate() {
		UnitSet myUnits = game.getMyUnits();
		
		for(Unit unit : myUnits) {
			if(unit.isIdle() || unit.isStuck()) {
				// generate new position
				// attack move!
			}
		}
	}
```

Now, we want to generate random position within a map where we should send our idle or stuck unit to. There
are many ways to this, but I will use my ``Vector2D`` class to this, because it's really simple that way.

This class contains a static method ``Vector2D.random()`` which will generate a 2D vector with
components in interval ``0`` to ``1``. When we have this "random" vector, we can scale it to fit the whole map.
It is best done by ``Vector2D.scale(double x, dobule y)`` method, where the ``x`` and ``y`` represents, how should the
individual components be scaled. We want the ``x`` component to fit map width, and ``y`` component to fit map height.
We also don't have to remember, how big the tile is in pixels, we just use the ``Game.TILE_SIZE`` constant.

Our code would be something like this:

```java
	public void onGameUpdate() {
		UnitSet myUnits = game.getMyUnits();
		
		for(Unit unit : myUnits) {
			if(unit.isIdle() || unit.isStuck()) {
				// generate new position
				Vector2D position = Vector2D.random().scale(
					game.getMap().getWidth()*Game.TILE_SIZE,
					game.getMap().getHeight()*Game.TILE_SIZE
				);
				// attack move!
				unit.attack(position);
			}
		}
	}
```
# Final source code

Here is the complete version of this sample:

```java
package sk.hackcraft.bwu.sample;

import javabot.model.Player;
import sk.hackcraft.bwu.Bot;
import sk.hackcraft.bwu.Game;
import sk.hackcraft.bwu.Graphics;
import sk.hackcraft.bwu.Unit;
import sk.hackcraft.bwu.Vector2D;
import sk.hackcraft.bwu.selection.UnitSet;

public class SampleBot extends Bot {
	static public void main(String [] arguments) {
		SampleBot bot = new SampleBot();
		bot.start();
	}
	
	private Game game = null;
	
	public void onConnected() {}
	public void onGameStarted(Game game) {
		this.game = game;		
	}
	public void onGameEnded() {
		this.game = null;
	}
	public void onGameUpdate() {
		UnitSet myUnits = game.getMyUnits();
		
		for(Unit unit : myUnits) {
			if(unit.isIdle() || unit.isStuck()) {
				// generate new position
				Vector2D position = Vector2D.random().scale(
					game.getMap().getWidth()*Game.TILE_SIZE,
					game.getMap().getHeight()*Game.TILE_SIZE
				);
				// attack move!
				unit.attack(position);
			}
		}
	}
	
	public void onDisconnected() {}
	public void onKeyPressed(int keyCode) {}
	public void onMatchEnded(boolean isWinner) {}
	public void onPlayerLeft(Player player) {}
	public void onNukeDetected(Vector2D position) {}
	public void onUnitDiscovered(Unit unit) {}
	public void onUnitDestroyed(Unit unit) {}
	public void onUnitEvaded(Unit unit) {}
	public void onUnitCreated(Unit unit) {}
	public void onUnitMorphed(Unit unit) {}
	public void onUnitShown(Unit unit) {}
	public void onUnitHidden(Unit unit) {}
	public void onDraw(Graphics graphics) {}
}
```



