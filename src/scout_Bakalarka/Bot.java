package scout_Bakalarka;

import jnibwapi.Player;
import jnibwapi.Unit;
import sk.hackcraft.bwu.*;
import sk.hackcraft.bwu.Graphics;

import java.awt.*;

/**
 * Top class of AI bot with callback methods
 */
public class Bot extends AbstractBot {

    ScoutAI scout;

    Robot r; //camera
    public boolean show=false;
    int aKeyCode=0;

    public static void main(String[] args) {
        BWU bwu=new BWU() {
            @Override
            protected sk.hackcraft.bwu.Bot createBot(Game game) {
                sk.hackcraft.bwu.Bot customBot=new Bot(game);
                return customBot;
            }
        };
        bwu.start();
    }

    public Bot(Game game) {
        super(game);
    }

    @Override
    public void gameStarted() {
        //game.sendMessage("black sheep wall");  //fog
        game.sendMessage("Bot successfully started!");
        game.getJNIBWAPI().setGameSpeed(20); //15
        game.enableUserInput();

        scout=new ScoutAI(game);
        scout.initializeAll();

        try {
            r = new Robot();

        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void gameEnded(boolean isWinner) {

    }

    @Override
    public void gameUpdated() {
        scout.manageAll();

        if(aKeyCode==49&&show) {
            r.keyPress(49);
        }
        if(aKeyCode==50&&show) {
            r.keyPress(50);
        }
        if(aKeyCode==51&&show) {
            r.keyPress(51);
        }
        if(aKeyCode==52&&show) {
            r.keyPress(52);
        }
        if(aKeyCode==53&&show) {
            r.keyPress(53);
        }
        if(aKeyCode==54&&show) {
            r.keyPress(54);
        }
        if(aKeyCode==55&&show) {
            r.keyPress(55);
        }
    }

    @Override
    public void keyPressed(int keyCode) {
        if(keyCode==107) {
            scout.manageTasks=true;
        }
        if(keyCode==106) {
            show=!show;
        }
        if(keyCode==49) {
            aKeyCode=49;
        }
        if(keyCode==50) {
            aKeyCode=50;
        }
        if(keyCode==51) {
            aKeyCode=51;
        }

    }

    @Override
    public void playerLeft(Player player) {

    }

    @Override
    public void playerDropped(Player player) {

    }

    @Override
    public void nukeDetected(Vector2D target) {

    }

    @Override
    public void unitDiscovered(Unit unit) {
        scout.unitDetectionManagement(unit);
    }

    @Override
    public void unitDestroyed(Unit unit) {

    }

    @Override
    public void unitEvaded(Unit unit) {

    }

    @Override
    public void unitCreated(Unit unit) {

    }

    @Override
    public void unitCompleted(Unit unit) {

    }

    @Override
    public void unitMorphed(Unit unit) {

    }

    @Override
    public void unitShowed(Unit unit) {

    }

    @Override
    public void unitHid(Unit unit) {

    }

    @Override
    public void unitRenegaded(Unit unit) {

    }

    @Override
    public void draw(Graphics graphics) {
        scout.drawAll(graphics);
    }

    @Override
    public void messageSent(String message) {
        scout.messageHandler(message);
    }

    @Override
    public void messageReceived(String message) {

    }

    @Override
    public void gameSaved(String gameName) {

    }
}
