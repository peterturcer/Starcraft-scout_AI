package custom_AI;

import javafx.geometry.Pos;
import jnibwapi.BaseLocation;
import jnibwapi.ChokePoint;
import jnibwapi.Position;
import jnibwapi.Unit;
import jnibwapi.util.BWColor;
import sk.hackcraft.bwu.Game;
import sk.hackcraft.bwu.selection.UnitSet;

import java.util.ArrayList;

/**
 * Handles all actions of the scout bot
 */
public class ActionManager {

    public static final int SCOUT_BO=0;
    public static final int SCOUT_EXP_1=1;
    public static final int SCOUT_EXP_2=2;
    public static final int SCOUT_EXP_3=3;
    public static final int SCOUT_EXP_4=4;
    public static final int SCOUT_EXP_5=5;
    public static final int SCOUT_EXP_6=6;
    public static final int SCOUT_ARMY=7;
    public static final int RETURN_HOME=8;

    /**
     * Creates instance of the ActionManager
     */
    public ActionManager() {}


    /* ------------------- Top level methods ------------------- */

    /**
     * Starts action for unit from Task depending on ActionID from Task
     *
     * @param pTask
     * @param pMapManager
     * @param pGame
     */
    public void startAction(Task pTask,MapManager pMapManager,Game pGame) {
        switch (pTask.getActionID()) {
            case SCOUT_BO : scoutBuildOrder(pTask,pMapManager,pGame);
                break;
            case RETURN_HOME : returnHome(pTask.getScoutingUnit(),pMapManager,pGame);
                break;
            case SCOUT_ARMY: scoutArmy(pTask,pMapManager);
                break;
            case SCOUT_EXP_6:
            case SCOUT_EXP_5:
            case SCOUT_EXP_4:
            case SCOUT_EXP_3:
            case SCOUT_EXP_2:
            case SCOUT_EXP_1: scoutExpand(pTask,pMapManager,pGame);
                break;
        }
    }

    public void scoutBuildOrder(Task pTask, MapManager pMapManager, Game pGame) {
        Position base=pMapManager.getEnemyBasePositions().get(0).getPosition();
        int safetyLevel=pTask.getSafetyLevel();

        pTask.getScoutingUnit().scout(pMapManager.buildPath(pTask.getScoutingUnit().getUnit(), base, safetyLevel, pTask.getScoutingUnit().getUnit().getType().isFlyer(),pGame, BWColor.Green),true);
    }

    public void scoutExpand(Task pTask, MapManager pMapManager, Game pGame) {
        if(pTask.getActionID()>=1&&pTask.getActionID()<=7) {
            if(pTask.getActionID()<pMapManager.getExpanzionCount()) {
                int safetyLevel=pTask.getSafetyLevel();
                pTask.getScoutingUnit().scout(pMapManager.buildPath(pTask.getScoutingUnit().getUnit(), pMapManager.getExpansionPositions().get(pTask.getActionID()).getPosition(), safetyLevel, pTask.getScoutingUnit().getUnit().getType().isFlyer(),pGame, BWColor.Green),true);
            }
        }
    }

    public void scoutArmy(Task pTask, MapManager pMapManager) {
        pTask.getScoutingUnit().setScoutingArea(pMapManager.getLeastVisitedEnemyArmyArea());
    }

    public void returnHome(ScoutingUnit pScoutingUnit, MapManager pMapManager, Game pGame) {
        Position base=pMapManager.getMyBasePosition();

        pScoutingUnit.scout(pMapManager.buildPath(pScoutingUnit.getUnit(),base,ScoutAI.SAFETY_LEVEL,pScoutingUnit.getUnit().getType().isFlyer(),pGame, BWColor.Blue),false);
    }

    public void scoutExpansion(UnitSet scoutingUnits) {

    }

    public void scoutChokePoints(UnitSet scoutingUnits) {

    }

    public void scoutEnemyArmyPositions(UnitSet scoutingUnits) {

    }

    public void scoutDrop(UnitSet scoutingUnits) {

    }


    /* ------------------- Other methods ------------------- */

    /**
     * Sends all units in given UnitSet to the given position with queued[true]/non-queued[false] command
     *
     * @param units
     * @param position
     * @param queued
     */
    public void goToPosition(UnitSet units, Position position,boolean queued) {
        for (Unit unit : units) {
            unit.move(position,queued);
        }
    }

    /**
     * Sends given unit to the given position with queued[true]/non-queued[false] command
     *
     * @param unit
     * @param position
     * @param queued
     */
    public void goToPosition(Unit unit, Position position, double percentageOfDistance, boolean queued) {

        boolean searching=true;

        int startingUnitPX=unit.getPosition().getPX();
        int startingUnitPY=unit.getPosition().getPY();

        int destinationPX=position.getPX();
        int destinationPY=position.getPY();

        int normalizedPX=Math.abs(startingUnitPX - destinationPX);
        int normalizedPY=Math.abs(startingUnitPY-destinationPY);

        System.out.println("searching...");
        unit.move(new Position((int)(normalizedPX*(percentageOfDistance/10)+destinationPX),(int)(normalizedPY*(percentageOfDistance/10)+destinationPY)),false);
    }

    /**
     * Sends given unit to the given BaseLocation with queued[true]/non-queued[false] command
     *
     * @param unit
     * @param baseLocation
     * @param queued
     */
    public void goToPosition(Unit unit, BaseLocation baseLocation,boolean queued) {
        unit.move(baseLocation.getPosition(),queued);
    }
}
