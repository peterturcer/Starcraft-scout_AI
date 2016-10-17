package custom_AI.aStar;

import custom_AI.GraphicsExtended;
import jnibwapi.Position;
import jnibwapi.Unit;
import jnibwapi.util.BWColor;
import sk.hackcraft.bwu.Game;
import sk.hackcraft.bwu.selection.UnitSet;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Top-level class used to execute path builder (AStarPathCalculator) with given grid map
 *
 * Created by Misho on 27.1.2016.
 */
public class AStarModule {

    private GridMap gridMap;

    /* ------------------- Constructors ------------------- */

    public AStarModule() {

    }

    public AStarModule(GridMap pGridMap) {
        gridMap=pGridMap;
    }


    /* ------------------- main methods ------------------- */

    /**
     * Executes path calculator
     *
     * @param pStartPosition
     * @param pDestination
     * @param pStartingHealth
     * @param pLevelOfSafety
     * @param pAirPath
     * @param game
     * @param pColor
     * @return
     */
    public AStarPathCalculator buildPath(Position pStartPosition, Position pDestination,int pStartingHealth, int pLevelOfSafety, boolean pAirPath, Game game,BWColor pColor) {
        AStarPathCalculator p=new AStarPathCalculator(pStartPosition,pDestination,pStartingHealth,pLevelOfSafety,pAirPath,gridMap,game,pColor);
        p.start();
        return p;
    }

    /* ------------------- initialising methods ------------------- */

    public void initializeAll(Game game) {}

    /* ------------------- real-time management methods ------------------- */


    /* ------------------- data structure operation methods ------------------- */


    /* ------------------- other methods ------------------- */


    /* ------------------- Drawing functions ------------------- */

    public void drawAll(GraphicsExtended graphicsEx,UnitSet pUnits) {
        drawGridMap(graphicsEx,BWColor.Blue);
        drawBlockUnderUnits(pUnits,graphicsEx);
    }

    public void drawGridMap(GraphicsExtended graphicsEx,BWColor color) {
        gridMap.drawGridMap(graphicsEx, color);
    }

    public void drawBlockUnderUnits(UnitSet pUnits, GraphicsExtended graphicsEx) {
        Block b;
        for(Unit u:pUnits) {
            b=gridMap.getBlockByPosition_blockMap(u.getPosition());
            b.drawBlock(graphicsEx, BWColor.Orange);
            //graphicsEx.drawText(u,"R:"+b.getRow()+",C:"+b.getColumn());
        }
    }


    /* ------------------- Getters and Setters ------------------- */

    public GridMap getGridMap() {
        return gridMap;
    }

}
