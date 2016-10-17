package custom_AI;

import custom_AI.aStar.*;
import jnibwapi.BaseLocation;
import jnibwapi.ChokePoint;
import jnibwapi.Position;
import jnibwapi.Unit;
import jnibwapi.types.UnitType;
import jnibwapi.util.BWColor;
import sk.hackcraft.bwu.Game;
import sk.hackcraft.bwu.Vector2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the GEO data, positions and fields
 */
public class MapManager {

    /**
     * Size of the edge of grid block
     */
    public static final int GRIDEDGESIZE=18; //18

    private AStarModule aStarModule;

    private HeatMap heatMap;

    /**
     * Postiton of my base
     */
    private Position myBasePosition;

    /**
     * List of enemy base positons
     */
    private ArrayList<PotentialField> enemyBasePositions;

    /**
     * List of expansion positons
     */
    private ArrayList<PotentialField> expansionPositions;

    /**
     * List of danger fields
     */
    private ArrayList<PotentialField> dangerFields;

    /**
     * List of retreat fields
     */
    private ArrayList<PotentialField> retreatFields;

    private List<ChokePoint> chokePoints;

    private ArrayList<ScoutingArea> scoutingAreas;

    private ArrayList<ScoutingArea> armyArea;

    private AStarPathCalculator staticPathCalculator;

    private GraphicsExtended graphicsEx;


    /* ------------------- Constructors ------------------- */

    /**
     * Creates the instance of MapManager and initializes variables
     */
    public MapManager(Game game) {
        heatMap=new HeatMap(game);
        enemyBasePositions=new ArrayList<>();
        expansionPositions=new ArrayList<>();
        dangerFields=new ArrayList<>();
        retreatFields=new ArrayList<>();
        scoutingAreas=new ArrayList<>();
        graphicsEx=new GraphicsExtended(game.getJNIBWAPI());
        aStarModule=new AStarModule(new GridMap(MapManager.GRIDEDGESIZE,game));
    }

    public MapManager(Game game, AStarModule pAStarModule, HeatMap pHeatMap) {
        enemyBasePositions=new ArrayList<>();
        expansionPositions=new ArrayList<>();
        dangerFields=new ArrayList<>();
        retreatFields=new ArrayList<>();
        scoutingAreas=new ArrayList<>();
        graphicsEx=new GraphicsExtended(game.getJNIBWAPI());
        aStarModule=pAStarModule;
        heatMap=pHeatMap;
    }

    /**
     * Creates the instance of MapManager with given lists of enemy base positions and expansion positions
     *
     * @param pEnemyBasePositions
     * @param pExpansionPositions
     */
    public MapManager(ArrayList pEnemyBasePositions, ArrayList pExpansionPositions,Game game) {
        enemyBasePositions=pEnemyBasePositions;
        expansionPositions=pExpansionPositions;
        dangerFields=new ArrayList<>();
        retreatFields=new ArrayList<>();
        graphicsEx=new GraphicsExtended(game.getJNIBWAPI());
    }


    /* ------------------- initialising methods ------------------- */

    public void initializeAll(Game game) {
        initializeHeatMap(game);
        initializeChokePoints(game);
        initializeEnemyBaseLocations(game);
        initializeAStarModule(game);
        initializeScoutingAreas(game);
    }

    public void initializeScoutingAreas(Game pGame) {
        /* Initialize enemy base area */
        Position eBasePosition=enemyBasePositions.get(0).getPosition();
        PotentialField centerBlock=heatMap.getHeatBlockContainingPosition(eBasePosition);
        int cRow=centerBlock.getRow();
        int cCol=centerBlock.getColumn();
        ScoutingArea eBaseArea=new ScoutingArea();

        for(int i=cRow-1;i<=cRow+1;i++) {
            if(i>=0&&i<heatMap.getColumns()) {
                for(int j=cCol-1;j<=cCol+1;j++) {
                    if(j>=0&&j<heatMap.getColumns()) {
                        eBaseArea.insert(heatMap.getHeatBlock(i,j));
                    }
                }
            }
        }
        System.out.println("eBase area size = " + eBaseArea.size() + " block");
        for(PotentialField pf:eBaseArea.getFieldArray()) {
            System.out.println("Field ["+pf.getRow()+";"+pf.getColumn()+"]");
        }
        eBaseArea.setID(ScoutingArea.BASEAREA);
        scoutingAreas.add(eBaseArea);

        if(staticPathCalculator==null) {
            staticPathCalculator = buildPath(pGame.getMyUnits().firstOf(UnitType.UnitTypes.Terran_SCV), enemyBasePositions.get(0).getPosition(), 1, false, pGame, BWColor.Green);
        }

        //ToDo: initialization of other paths
    }

    public ScoutingArea getEnemyArmyArea() {
        ScoutingArea area=new ScoutingArea();
        PotentialField centerBlock;
        int cRow;
        int cCol;
        for(PotentialField pf:dangerFields) {
            centerBlock=heatMap.getHeatBlockContainingPosition(pf.getPosition());
            cRow=centerBlock.getRow();
            cCol=centerBlock.getColumn();
            for(int i=cRow-1;i<=cRow+1;i++) {
                if(i>=0&&i<heatMap.getColumns()) {
                    for(int j=cCol-1;j<=cCol+1;j++) {
                        if(j>=0&&j<heatMap.getColumns()) {
                            if(!area.getFieldArray().contains(heatMap.getHeatBlock(i,j))) {
                                area.insert(heatMap.getHeatBlock(i,j));
                            }
                        }
                    }
                }
            }
        }
        if(area.getFieldArray().size()>0) {
            return area;
        }
        return null;
    }

    public ScoutingArea getLeastVisitedEnemyArmyArea()  {
        ScoutingArea area=new ScoutingArea();
        PotentialField centerBlock;
        int cRow;
        int cCol;
        double heatLvl=Double.MIN_VALUE;
        int index=-1;
        for(int i=0;i<dangerFields.size();i++) {
            if(dangerFields.get(i).getHeat()>heatLvl) {
                heatLvl=dangerFields.get(i).getHeat();
                index=i;
            }

        }
        if(index!=-1) {
            centerBlock = heatMap.getHeatBlockContainingPosition(dangerFields.get(index).getPosition());
            cRow=centerBlock.getRow();
            cCol=centerBlock.getColumn();
            for(int i=cRow-1;i<=cRow+1;i++) {
                if(i>=0&&i<heatMap.getColumns()) {
                    for(int j=cCol-1;j<=cCol+1;j++) {
                        if(j>=0&&j<heatMap.getColumns()) {
                            if(!area.getFieldArray().contains(heatMap.getHeatBlock(i,j))) {
                                area.insert(heatMap.getHeatBlock(i,j));
                            }
                        }
                    }
                }
            }
        }
        if(area.getFieldArray().size()>0) {
            return area;
        }
        return null;
    }

    /**
     * Initializes chokepoints from the map
     *
     * @param game
     */
    public void initializeChokePoints(Game game) {
        chokePoints=game.getMap().getChokePoints();

        for(ChokePoint choke:chokePoints) {
            /*CONSOLE LOG*/
            System.out.println("Added chokepoint at position :"+choke.getCenter().toString());
            /*END CONSOLE LOG*/
        }

    }

    public void initializeEnemyBaseLocations(Game pGame) {
        /*CONSOLE LOG*/
        System.out.println("Initializing base locations...");
        /*END CONSOLE LOG*/
        for(BaseLocation b:pGame.getMap().getBaseLocations()) {
            if(!pGame.getJNIBWAPI().isVisible(b.getPosition())) {
                if(b.isStartLocation()) {
                    addEnemyBasePosition(pGame,b.getPosition());
                    /*CONSOLE LOG*/
                    System.out.println("Added base location at "+b.getPosition().toString());
                    /*END CONSOLE LOG*/
                } else {
                    addExpansionPosition(pGame,b.getPosition());
                    /*CONSOLE LOG*/
                    System.out.println("Added expansion position at " + b.getPosition().toString());
                    /*END CONSOLE LOG*/
                }
            } else {
                if(b.isStartLocation()) {
                    myBasePosition=b.getPosition();
                    /*CONSOLE LOG*/
                    System.out.println("Added home base location at "+b.getPosition().toString());
                    /*END CONSOLE LOG*/
                }

            }
        }
    }

    public void initializeHeatMap(Game game) {
        heatMap.initializeHeatMap(500, game);
    }

    public void initializeAStarModule(Game game) {
        aStarModule.initializeAll(game);
    }


    /* ------------------- real-time management methods ------------------- */

    public void manageAll(Game game) {
        manageHeatMap(game);
        refreshDangerField(game);
        manageInitializationBaseArea(game);
        manageDangerFields(game);
    }

    public void refreshDangerField(Game pGame) {
        if(pGame.getFrameCount()%ScoutAI.MAP_REFRESH_FRAME_COUNT==0) {
            refreshMap(pGame);
        }
    }

    public void refreshMap(Game pGame) {
        for(Unit pUnit:pGame.getEnemyUnits()) {
            if (pUnit.getPlayer().isEnemy() && pUnit.getType().isAttackCapable()&&!pUnit.getType().isWorker()) {
                PotentialField p = getDangerFieldByID(pUnit.getID());
                if (p != null) {
                    if(p.getX()!=pUnit.getPosition().getPX()&&p.getY()!=pUnit.getPosition().getPY()) {
                        aStarModule.getGridMap().refreshGridMap(p);
                        p.setX(pUnit.getPosition().getPX());
                        p.setY(pUnit.getPosition().getPY());
                        aStarModule.getGridMap().updateGridMap(p);
                    }
                } else {
                    PotentialField pf = new PotentialField(pGame, pUnit);
                    dangerFields.add(pf);
                    aStarModule.getGridMap().updateGridMap(pf);
                }
            }
        }


        for(PotentialField pf:dangerFields) {
            if(pf.isVisible(pGame.getMyUnits())) {
                if(pGame.getEnemyUnits().size()<1||pGame.getEnemyUnits().where(unit -> unit.getID()==pf.getId()).isEmpty()) {
                    aStarModule.getGridMap().refreshGridMap(pf);
                    dangerFields.remove(pf);
                }
            }
        }
    }

    public void manageInitializationBaseArea(Game pGame) {
        /* Initialize base-to-base area */
        if(staticPathCalculator!=null&&staticPathCalculator.finished) {
            ArrayList<Block> path=staticPathCalculator.getBlockPathArray();
            ScoutingArea basePathArea=new ScoutingArea();

            for(Block b:path) {
                PotentialField pf=heatMap.getHeatBlockContainingPosition(b.getPosition());
                if(!basePathArea.getFieldArray().contains(pf)) {
                    basePathArea.insert(pf);
                }
            }
            basePathArea.setID(ScoutingArea.PATHAREA);
            scoutingAreas.add(basePathArea);
            staticPathCalculator=null;
        }
    }

    public void manageHeatMap(Game game) {
        heatMap.heatManagement(game);
    }

    public void manageDangerFields(Game pGame) {
        for(PotentialField pf:dangerFields) {
            if(pGame.getJNIBWAPI().isVisible(pf.getPosition())) {
                pf.setHeat(0);
            } else {
                pf.increaseHeat();
            }
        }
    }

    public void manageAStarModule(Game game) {

    }

    /* ------------------- data structure operation methods ------------------- */

    /**
     * Adds expansion position to the list
     *
     * @param expansionPosition
     */
    public void addExpansionPosition(Game game, Position expansionPosition) {
        PotentialField expansionPF=new PotentialField(game,expansionPosition,UnitType.UnitTypes.Terran_Command_Center.getSightRange());
        if(!expansionPositions.contains(expansionPF)) {
            expansionPositions.add(expansionPF);
        }
    }

    /**
     * Removes expansion position from the list
     *
     * @param expansionPosition
     */
    public void removeExpansionPosition(Game game, Position expansionPosition) {
        PotentialField pf=new PotentialField(game,expansionPosition,UnitType.UnitTypes.Terran_Command_Center.getSightRange());
        if(expansionPositions.contains(pf)) {
            expansionPositions.remove(pf);
        }
    }

    /**
     * Adds enemy base position to the list
     *
     * @param basePosition
     */
    public void addEnemyBasePosition(Game game, Position basePosition) {
        PotentialField basePF=new PotentialField(game, basePosition, UnitType.UnitTypes.Terran_Command_Center.getSightRange());
        if(!enemyBasePositions.contains(basePF)) {
            enemyBasePositions.add(basePF);
        }
    }

    /**
     * Removes base position from the list
     *
     * @param basePosition
     */
    public void removeBasePosition(Game game, Position basePosition) {
        PotentialField basePF=new PotentialField(game, basePosition, UnitType.UnitTypes.Terran_Command_Center.getSightRange());
        if(enemyBasePositions.contains(basePF)) {
            enemyBasePositions.remove(basePF);
        }
    }

    /**
     * Adds danger field to the list
     *
     * @param pDangerField
     */
    public void addDangerField(PotentialField pDangerField) {
        dangerFields.add(pDangerField);
    }

    /**
     * Removes danger field from the list
     *
     * @param pDangerField
     */
    public void removeDangerField(PotentialField pDangerField) {
        if(dangerFields.contains(pDangerField)) {
            dangerFields.remove(pDangerField);
        }
    }

    /**
     * Adds retreat field to the list
     *
     * @param pRetreatField
     */
    public void addRetreatField(PotentialField pRetreatField) {
        retreatFields.add(pRetreatField);
    }

    /**
     * Removes retreat field from the list
     *
     * @param pRetreatField
     */
    public void removeRetreatField(PotentialField pRetreatField) {
        if(retreatFields.contains(pRetreatField)) {
            retreatFields.remove(pRetreatField);
        }
    }

    public PotentialField getDangerFieldByID(int pID) {
        for(PotentialField pf:dangerFields) {
            if(pf.getId()==pID) {
                return pf;
            }
        }
        return null;
    }


    /* ------------------- other methods ------------------- */

    public boolean containsPotentialFieldWithID(int pID) {
        for(PotentialField pf:dangerFields) {
            if(pf.getId()==pID) {
                return true;
            }
        }
        return false;
    }

    public AStarPathCalculator buildPath(Unit pUnit, Position pDestination, int pLevelOfSafety, boolean pAirPath, Game game, BWColor pColor) {
        return aStarModule.buildPath(pUnit.getPosition(), pDestination, pUnit.getHitPoints(), pLevelOfSafety, pAirPath, game,pColor);
    }

    public AStarPathCalculator buildPath(Unit pUnit,Position pStart, Position pDestination, int pLevelOfSafety, boolean pAirPath, Game game, BWColor pColor) {
        return aStarModule.buildPath(pStart, pDestination, pUnit.getHitPoints(), pLevelOfSafety, pAirPath, game,pColor);
    }

    /**
     * Return count of the enemy bases
     *
     * @return
     */
    public int getEnemyBaseCount() {
        return enemyBasePositions.size();
    }

    /**
     * Returns count of the expansions
     *
     * @return
     */
    public int getExpanzionCount() {
        return expansionPositions.size();
    }

    /**
     * Returns count of the danger fields
     *
     * @return
     */
    public int getDangerFieldsCount() {
        return dangerFields.size();
    }

    /**
     * Returns count of the retreat fields
     *
     * @return
     */
    public int getRetreatFieldsCount() {
        return retreatFields.size();
    }

    /**
     * Returns string information about map
     *
     * @return
     */
    public String toString() {
        return "\nEnemy bases  = "+getEnemyBaseCount()+
               "\nExpansion locations = "+getExpanzionCount()+
               "\nDanger locations = "+getDangerFieldsCount()+
               "\nRetreat locations = "+getRetreatFieldsCount();
    }


    /* ------------------- Drawing functions ------------------- */

    public void drawAll(Game game) {
        drawBasePositions(game);
        drawExpansionPositions(game);
        drawChokePoints();
        drawHeatMap(game);
        drawDangerFields(game);
        drawDangerGrid(game);
    }

    /**
     * Draws choke points on the map
     */
    public void drawChokePoints() {
        graphicsEx.setColor(BWColor.Purple);
        for(ChokePoint ch:chokePoints) {
            graphicsEx.drawCircleALTERNATIVE(new Vector2D(ch.getCenter().getPX(),ch.getCenter().getPY()),(int)ch.getRadius());
        }
    }

    /**
     * Draws base positions on the map
     */
    public void drawBasePositions(Game game) {
        for(PotentialField pf : enemyBasePositions) {
            pf.showGraphicsCircular(game, BWColor.Red);
        }
    }

    /**
     * Draws expansion positions on the map
     */
    public void drawExpansionPositions(Game game) {
        for(PotentialField pf:expansionPositions) {
            pf.showGraphicsCircular(game, BWColor.Yellow);
        }
    }

    public void drawHeatMap(Game game) {
        heatMap.drawHeatMap(game);
    }

    public void drawDangerFields(Game pGame) {
        for(PotentialField pf:dangerFields) {
            pf.showGraphicsCircular(pGame,BWColor.Orange);
        }
    }

    public void drawDangerGrid(Game game) {
        aStarModule.getGridMap().drawDangerGrid(new GraphicsExtended(game.getJNIBWAPI()),BWColor.Green);
    }

    /* ------------------- Getters and Setters ------------------- */

    public ArrayList<ScoutingArea> getScoutingArea(int pID) {
        ArrayList<ScoutingArea> arSc=new ArrayList<>();
        for(ScoutingArea sc:scoutingAreas) {
            if(sc.getID()==pID) {
                arSc.add(new ScoutingArea(sc));
            }
        }
        return arSc;
    }

    public AStarModule getaStarModule() {
        return aStarModule;
    }

    public void setaStarModule(AStarModule aStarModule) {
        this.aStarModule = aStarModule;
    }

    public HeatMap getHeatMap() {
        return heatMap;
    }

    public void setHeatMap(HeatMap heatMap) {
        this.heatMap = heatMap;
    }

    public Position getMyBasePosition() {
        return myBasePosition;
    }

    public void setMyBasePosition(Position myBasePosition) {
        this.myBasePosition = myBasePosition;
    }

    public ArrayList<PotentialField> getEnemyBasePositions() {
        return enemyBasePositions;
    }

    public void setEnemyBasePositions(ArrayList<PotentialField> enemyBasePositions) {
        this.enemyBasePositions = enemyBasePositions;
    }

    public ArrayList<PotentialField> getExpansionPositions() {
        return expansionPositions;
    }

    public void setExpansionPositions(ArrayList<PotentialField> expansionPositions) {
        this.expansionPositions = expansionPositions;
    }

    public ArrayList<PotentialField> getDangerFields() {
        return dangerFields;
    }

    public void setDangerFields(ArrayList<PotentialField> dangerFields) {
        this.dangerFields = dangerFields;
    }

    public ArrayList<PotentialField> getRetreatFields() {
        return retreatFields;
    }

    public void setRetreatFields(ArrayList<PotentialField> retreatFields) {
        this.retreatFields = retreatFields;
    }

    public List<ChokePoint> getChokePoints() {
        return chokePoints;
    }

    public void setChokePoints(List<ChokePoint> chokePoints) {
        this.chokePoints = chokePoints;
    }

    public GraphicsExtended getGraphicsEx() {
        return graphicsEx;
    }

    public void setGraphicsEx(GraphicsExtended graphicsEx) {
        this.graphicsEx = graphicsEx;
    }
}
