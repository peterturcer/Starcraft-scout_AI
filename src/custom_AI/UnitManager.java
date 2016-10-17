package custom_AI;

import jnibwapi.Position;
import jnibwapi.Unit;
import jnibwapi.types.UnitType;
import jnibwapi.util.BWColor;
import sk.hackcraft.bwu.Game;
import sk.hackcraft.bwu.Vector2D;
import sk.hackcraft.bwu.selection.UnitSet;

import java.util.ArrayList;

/**
 * Management of the scouting units.
 */
public class UnitManager {

    private ArrayList<ScoutingUnit> scoutingUnits;

    private GraphicsExtended graphicsEx;


    /* ------------------- Constructors ------------------- */

    /**
     * Creates instance of UnitManager
     */
    public UnitManager(Game game) {
        scoutingUnits=new ArrayList<>();
        graphicsEx=new GraphicsExtended(game.getJNIBWAPI());
    }

    /**
     * Creates instance of UnitManager with given set of units
     *
     * @param pScoutingUnits
     */
    public UnitManager(UnitSet pScoutingUnits,Game game) {
        scoutingUnits=new ArrayList<>();
        for(Unit u:pScoutingUnits) {
            scoutingUnits.add(new ScoutingUnit(u));
        }
        graphicsEx=new GraphicsExtended(game.getJNIBWAPI());
    }


    /* ------------------- Initialization methods ------------------- */

    /**
     * Initializes scouting units of certain type and mass
     *
     * @param pGame
     * @param pType
     * @param pMass
     */
    public void initializeScoutingUnits(Game pGame,UnitType pType,int pMass) {
        int counter=0;
        for(Unit u:pGame.getMyUnits()) {
            if(u.getType()==pType) {
                scoutingUnits.add(new ScoutingUnit(u));
                counter++;
            }
            if(counter==pMass) {
                break;
            }
        }
    }


    /* ------------------- Top-level methods ------------------- */

    public ScoutingUnit getClosestScoutingUnit(Task pTask, MapManager pMapManager, boolean pInterrupt) {
        Position destination=null;
        switch (pTask.getActionID()) {
            case 0: destination=pMapManager.getEnemyBasePositions().get(0).getPosition();
                break;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6: destination=pMapManager.getExpansionPositions().get(pTask.getActionID()).getPosition();
                break;
            case 7: destination=pMapManager.getMyBasePosition();
                break;
        }


        if(scoutingUnits.size()>0&&destination!=null) {
            int index=0;
            boolean found=false;
            double distance=Integer.MAX_VALUE;
            for(int i=0;i<scoutingUnits.size();i++) {
                if(pInterrupt) {
                    if(scoutingUnits.get(i).getUnit().isExists()&&scoutingUnits.get(i).getUnit().getPosition().getPDistance(destination)<distance) {
                        distance=scoutingUnits.get(i).getUnit().getPosition().getPDistance(destination);
                        index=i;
                        found=true;
                    }
                } else if(!scoutingUnits.get(i).hasOrder()||!scoutingUnits.get(i).isHasTask()) {
                    if(scoutingUnits.get(i).getUnit().isExists()&&scoutingUnits.get(i).getUnit().getPosition().getPDistance(destination)<distance) {
                        distance=scoutingUnits.get(i).getUnit().getPosition().getPDistance(destination);
                        index=i;
                        found=true;
                    }
                }

            }
            if(found) {
                return scoutingUnits.get(index);
            }
        }
        return null;
    }

    /**
     * Returns onlz ground scouting units
     *
     * @return
     */
    public ArrayList<ScoutingUnit> getGroundScoutingUnits() {
        ArrayList<ScoutingUnit> groundScoutingUnits=new ArrayList<>();
        for(ScoutingUnit u:scoutingUnits) {
            if(!u.getUnit().getType().isFlyer()) {
                groundScoutingUnits.add(u);
            }
        }

        if(groundScoutingUnits.size()>0) {
            return groundScoutingUnits;
        } else {
            return null;
        }
    }

    /**
     * Return only air scouting units
     *
     * @return
     */
    public ArrayList<ScoutingUnit> getAirScoutingUnits() {
        ArrayList<ScoutingUnit> airScoutingUnits=new ArrayList<>();
        for(ScoutingUnit u:scoutingUnits) {
            if(u.getUnit().getType().isFlyer()) {
                airScoutingUnits.add(u);
            }
        }

        if(airScoutingUnits.size()>0) {
            return airScoutingUnits;
        } else {
            return null;
        }
    }

    /**
     * Returns only cloakable scouting units
     *
     * @return
     */
    public ArrayList<ScoutingUnit> getStealthScoutingUnits() {
        ArrayList<ScoutingUnit> stealthScoutingUnits=new ArrayList<>();
        for(ScoutingUnit u:scoutingUnits) {
            if(u.getUnit().getType().isCloakable()) {
                stealthScoutingUnits.add(u);
            }
        }

        if(stealthScoutingUnits.size()>0) {
            return stealthScoutingUnits;
        } else {
            return null;
        }
    }

    public ArrayList<ScoutingUnit> getAllScoutingUnits() {
        return scoutingUnits;
    }


    /* ------------------- Data structure operations ------------------- */

    /**
     * Adds scouting unit to the group
     *
     * @param unit
     */
    public void addScoutingUnit(Unit unit) {
        scoutingUnits.add(new ScoutingUnit(unit));
    }

    /**
     * Removes unit with certain ID from the group
     *
     * @param ID
     */
    public void removeScoutingUnit(int ID) {
        scoutingUnits.removeIf(scoutingUnit -> scoutingUnit.getUnit().getID() == ID);
    }


    /* ------------------- Real-time management methods ------------------- */

    public void manageAll(MapManager pMapManager, Game pGame) {
        manageUnits(pMapManager, pGame);
    }

    public void manageUnits(MapManager pMapManager, Game pGame) {
        if(scoutingUnits.size()>0) {
            for(ScoutingUnit u:scoutingUnits) {
                if(!u.getUnit().isExists()) {
                    scoutingUnits.remove(u);
                } else {
                    u.manageAll(pMapManager, pGame);
                }
            }
        }
    }

    public void manageDetection(Unit pUnit, MapManager pMapManager, Game pGame) {
        for(ScoutingUnit s: scoutingUnits) {
            if(pUnit.getPlayer().isEnemy()&&pUnit.getType().isAttackCapable()) {
                s.enemyDetected(pUnit, pMapManager, pGame);
            }
        }
    }


    /* ------------------- Drawing functions ------------------- */

    public void drawAll() {
        drawUnitPositions();
        drawUnitPaths();
    }

    public void drawUnitPositions() {
        for(ScoutingUnit u:scoutingUnits) {
            if(u.getUnit().isExists()) {
                graphicsEx.setColor(BWColor.Orange);
                graphicsEx.drawBoxALTERNATIVE(new Vector2D(u.getUnit().getPosition().getPX()-25,u.getUnit().getPosition().getPY()-25),new Vector2D(u.getUnit().getPosition().getPX()+25,u.getUnit().getPosition().getPY()+25));
            }
        }
    }

    public void drawUnitPaths() {
        for(ScoutingUnit u:scoutingUnits) {
            u.drawAll(graphicsEx);
        }
    }

    /**
     * Draws line from each scouting unit to it`s destination
     */
    public void drawUnitDestination() {
        graphicsEx.setColor(BWColor.Cyan);
        for(ScoutingUnit u:scoutingUnits) {
            graphicsEx.drawLineALTERNATIVE(u.getUnit().getPositionVector(),new Vector2D(u.getUnit().getTargetPosition().getPX(),u.getUnit().getTargetPosition().getPY()));
        }
    }

    /**
     * Returns String with information
     *
     * @return
     */
    public String toString() {
        int allUnits=0;
        int groundUnits=0;
        int airUnits=0;
        if(scoutingUnits!=null) {
            allUnits=scoutingUnits.size();
        }
        if(getGroundScoutingUnits()!=null) {
            groundUnits=getGroundScoutingUnits().size();
        }
        if(getAirScoutingUnits()!=null) {
            airUnits=getAirScoutingUnits().size();
        }

        return  "\nScouting units = "+allUnits+
                "\nAir scouting units = "+airUnits+
                "\nGround scouting units = "+groundUnits;
    }


    /* ------------------- Getters and setters ------------------- */

    public ArrayList<ScoutingUnit> getScoutingUnits() {
        return scoutingUnits;
    }

    public void setScoutingUnits(ArrayList<ScoutingUnit> scoutingUnits) {
        this.scoutingUnits = scoutingUnits;
    }
}


