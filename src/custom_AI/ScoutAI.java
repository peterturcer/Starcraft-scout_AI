package custom_AI;

import custom_AI.aStar.AStarPathCalculator;
import custom_AI.aStar.GridMap;
import javafx.scene.input.KeyCode;
import jnibwapi.Unit;
import jnibwapi.types.UnitType;
import jnibwapi.util.BWColor;
import sk.hackcraft.bwu.Game;
import sk.hackcraft.bwu.Graphics;
import sk.hackcraft.bwu.Vector2D;

import java.awt.*;

/**
 * Top level class of scouting module - connects and manages all modules.
 */
public class ScoutAI {

    public static boolean DEBUG=true;

    public static final int SAFETY_LEVEL=2;

    public static final int MAP_REFRESH_FRAME_COUNT=30;

    public static final int UNIT_DANGERCHECK_FRAME_COUNT=35;

    public boolean manageTasks=false;

    /**
     * Instance of map manager
     */
    private MapManager mapManager;

    /**
     * Instance of action handler
     */
    private ActionManager actionManager;

    /**
     * Instance of unit manager
     */
    private UnitManager unitManager;

    /**
     * Instance of task manager
     */
    private TaskManager taskManager;

    /**
     * Instance of the game
     */
    private Game GAME;


    /* ------------------- Constructors ------------------- */

    /**
     * Create instance of scout
     *
     * @param game
     */
    public ScoutAI(Game game) {
        GAME=game;
        mapManager=new MapManager(game);
        actionManager =new ActionManager();
        unitManager=new UnitManager(game);
        taskManager=new TaskManager();


    }


    /* ------------------- initialising methods ------------------- */

    /**
     * Initialization method for all modules
     */
    public void initializeAll() {
        initializeMapManager();
        initializeDebug();
    }

    public void initializeDebug() {
        AStarPathCalculator.DEBUG=true;
        GridMap.DEBUG=false;
    }

    public void initializeTasks() {
        Task t2=new Task(5,50,ActionManager.SCOUT_EXP_1,true);
        taskManager.addTask(t2);

        Task t1=new Task(40,110,ActionManager.SCOUT_BO,true);
        taskManager.addTask(t1);

//        Task t3=new Task(10,30,ActionManager.SCOUT_EXP_2,true);
//        taskManager.addTask(t3);

//        Task t4=new Task(8,19,ActionManager.SCOUT_ARMY,false);
//        taskManager.addTask(t4);
    }

    public void initializeScoutingUnits() {
        unitManager.initializeScoutingUnits(GAME, UnitType.UnitTypes.Terran_Wraith, 2);
        unitManager.initializeScoutingUnits(GAME, UnitType.UnitTypes.Terran_SCV,2);
//        unitManager.initializeScoutingUnits(GAME, UnitType.UnitTypes.Terran_Vulture,1);
//        unitManager.initializeScoutingUnits(GAME, UnitType.UnitTypes.Terran_Siege_Tank_Tank_Mode, 3);
        unitManager.initializeScoutingUnits(GAME, UnitType.UnitTypes.Terran_Marine, 3);
    }

    /**
     * Initialization of MapManager
     */
    public void initializeMapManager() {
        mapManager.initializeAll(GAME);
    }



    /* ------------------- real-time management methods ------------------- */

    /**
     * Management method for all modules
     */
    public void manageAll() {
        manageMapManager();
        manageUnitManager();
        manageTaskManager();
        if(manageTasks) {
            manageAUTOTEST();
        }
    }

    public void manageAUTOTEST() {

        if (GAME.getFrameCount()==50) {
            initializeScoutingUnits();
        } else if(GAME.getFrameCount()==80) {
            initializeTasks();
        } else if(mapManager.getDangerFields().size()>0&&GAME.getFrameCount()==2000) {
            Task t4=new Task(11,25,ActionManager.SCOUT_ARMY,false);
            taskManager.addTask(t4);
        }

        if(mapManager.getDangerFields().size()>0&&GAME.getFrameCount()==5000) {
            Task t4=new Task(11,25,ActionManager.SCOUT_ARMY,false);
            taskManager.addTask(t4);
        }

        taskManager.manageTaskAssigning(unitManager,mapManager);



    }

    public void manageMapManager() {
        mapManager.manageAll(GAME);
        mapManager.manageAStarModule(GAME);
    }

    public void manageUnitManager() {
        unitManager.manageAll(mapManager,GAME);
    }

    public void unitDetectionManagement(Unit pUnit) {
        unitManager.manageDetection(pUnit,mapManager,GAME);
    }

    public void manageTaskManager() {
        taskManager.manageAll(actionManager, mapManager, GAME);
    }


    /* ------------------- data structure operation methods ------------------- */


    /* ------------------- other methods ------------------- */

    /**
     * In-game commands used for test and demonstration
     *
     * @param pMessage
     */
    public void messageHandler(String pMessage) {
        switch (pMessage) {
            case "scoutBO": TEST_scoutBuildOrder();
                break;
            case "home": TEST_backHome();
                break;
            case "add":
                for(Unit u:GAME.getMyUnits()) {
                    if(u.isSelected()) {
                        unitManager.addScoutingUnit(u);
                    }
                }
                break;
            case "remove":
                for(Unit u:GAME.getMyUnits()) {
                    if(u.isSelected()) {
                        unitManager.removeScoutingUnit(u.getID());
                    }
                }
                break;
            case "refresh":
                mapManager.refreshDangerField(GAME);
                break;
            case "bounds":
                for(ScoutingUnit u:unitManager.getGroundScoutingUnits()) {
                    if(u.getUnit().isSelected()) {
                        u.localDangerCheck(mapManager,GAME);
                    }
                }
                break;
            case "position":
                for(ScoutingUnit u:unitManager.getGroundScoutingUnits()) {
                    if(u.getUnit().isSelected()) {
                        mapManager.getHeatMap().getHeatBlockContainingPosition(u.getUnit().getPosition());
                    }
                }
                break;
            case "area":
                for(ScoutingUnit u:unitManager.getGroundScoutingUnits()) {
                    System.out.println("Unit heatField = ["+mapManager.getHeatMap().getHeatBlockContainingPosition(u.getUnit().getPosition()).getRow()+";"+mapManager.getHeatMap().getHeatBlockContainingPosition(u.getUnit().getPosition()).getColumn()+"]");
                    u.setScoutingArea(mapManager.getScoutingArea(ScoutingArea.BASEAREA).get(0));
                }
                break;
            case "task": initializeTasks();
                break;
            case "assign":
                taskManager.assignUnitToTask(taskManager.getTasks().get(0),unitManager.getGroundScoutingUnits().get(0));
                break;
            case "manage":
                taskManager.manageTaskAssigning(unitManager,mapManager);
                break;
            case "army":
                for(ScoutingUnit u:unitManager.getAllScoutingUnits()) {
                    if(u.getUnit().isSelected()) {
                        u.setScoutingArea(mapManager.getLeastVisitedEnemyArmyArea());
                    }
                }
                break;
            case "init":initializeScoutingUnits();
                initializeTasks();
                break;
        }
    }


    /* ------------------- Test methods ------------------- */

    public void TEST_backHome() {
        for(ScoutingUnit u:unitManager.getAllScoutingUnits()) {
            if(!u.hasOrder()) {
                if(u.getUnit().isSelected()) {
                    u.scout(mapManager.buildPath(u.getUnit(), mapManager.getMyBasePosition(),1, u.getUnit().getType().isFlyer(), GAME,BWColor.Green),false);
                    u.setHasOrder(true);
                    System.out.println("HitPoints = "+u.getUnit().getHitPoints());
                }
            }
        }
    }

    public void TEST_scoutBuildOrder() {
        /*CONSOLE LOG*/
        //System.out.println("Ordered to scout B.O.");
        /*END CONSOLE LOG*/
        for(ScoutingUnit u:unitManager.getAllScoutingUnits()) {
            if(!u.hasOrder()) {
                if(u.getUnit().isSelected()) {
                    u.scout(mapManager.buildPath(u.getUnit(), mapManager.getEnemyBasePositions().get(0).getPosition(), 1, u.getUnit().getType().isFlyer(), GAME, BWColor.Green),false);
                }
            }
        }
    }

    /* ------------------- Drawing functions ------------------- */

    /**
     * Drawing method for all modules
     * @param graphics
     */
    public void drawAll(Graphics graphics) {
        drawMapManager();
        drawUnitManager();
        drawStats(graphics);
    }

    /**
     * Drawning method for stats
     *
     * @param graphics
     */
    public void drawStats(Graphics graphics) {
        graphics.drawText(new Vector2D(20, 20), "--- Scout stats and actions ---");
        graphics.drawText(new Vector2D(20, 40), unitManager.toString() +
                        mapManager.toString()
        );
        taskManager.drawTaskList(graphics);
    }

    public void drawMapManager() {
        mapManager.drawAll(GAME);
    }

    public void drawUnitManager() {
        unitManager.drawAll();
    }

    /* ------------------- Getters and Setters ------------------- */


}
