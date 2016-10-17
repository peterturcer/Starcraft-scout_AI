package Projekt_ScoutAI;

import jnibwapi.Unit;
import sk.hackcraft.bwu.Game;
import sk.hackcraft.bwu.Vector2D;

import java.util.Random;

/**
 * Created by Silent1 on 10.10.2016.
 */
public class Scout {

    public void helloWorld(Game pGame) {
        for(Unit u:pGame.getAllUnits()) {
            if(!u.isMoving()) {
                u.move(u.getPositionVector().add(new Vector2D(-500+new Random().nextInt(1000), -500+new Random().nextInt(1000))).toPosition(), false);
            }
        }
    }

    public void detection(Game pGame, Unit pUnit) {
        if(pUnit.getPlayer().isEnemy()) {
            pGame.sendMessage(pUnit.toString() + " detected!");
        }
    }
}
