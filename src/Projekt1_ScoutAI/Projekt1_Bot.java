package Projekt1_ScoutAI;

import jnibwapi.Player;
import jnibwapi.Unit;
import sk.hackcraft.bwu.*;

/**
 * Created by Silent1 on 10.10.2016.
 */
public class Projekt1_Bot extends AbstractBot {

    private Scout scout_bot;


    public static void main(String[] args) {
        BWU bwu=new BWU() {
            @Override
            protected sk.hackcraft.bwu.Bot createBot(Game game) {
                sk.hackcraft.bwu.Bot customBot=new Projekt1_Bot(game);
                return customBot;
            }
        };
        bwu.start();
    }

    public Projekt1_Bot(Game game) {
        super(game);
        scout_bot=new Scout();
    }

    @Override
    public void gameStarted() {
        game.sendMessage("Projekt1_Bot successfully started!");
        game.getJNIBWAPI().setGameSpeed(20); //15
        game.enableUserInput();
    }

    @Override
    public void gameEnded(boolean isWinner) {

    }

    @Override
    public void gameUpdated() {
        scout_bot.helloWorld(game);

    }

    @Override
    public void keyPressed(int keyCode) {

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
        scout_bot.detection(game,unit);
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

    }

    @Override
    public void messageSent(String message) {

    }

    @Override
    public void messageReceived(String message) {

    }

    @Override
    public void gameSaved(String gameName) {

    }
}
