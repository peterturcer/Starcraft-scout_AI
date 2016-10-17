package scout_Bakalarka;

import jnibwapi.util.BWColor;
import sk.hackcraft.bwu.Game;
import sk.hackcraft.bwu.Vector2D;

import java.util.ArrayList;

/**
 * ScoutingArea represents area that consists of heat blocks provided by heat map. Each given block is added to array.
 * ScoutingArea provides GEO methods like "nearest block from area to given unit"
 */
public class ScoutingArea {

    public static int BASEAREA=1;
    public static int EXPANDAREA=2;
    public static int ARMYAREA=3;
    public static int PATHAREA=4;

    private int ID;

    private ArrayList<PotentialField> fieldArray;


    /* ------------------- Constructors ------------------- */

    public ScoutingArea() {
        fieldArray=new ArrayList<>();
    }

    public ScoutingArea(int pID) {
        fieldArray=new ArrayList<>();
        ID=pID;
    }

    public ScoutingArea(ScoutingArea pScoutingArea) {
        this.ID=pScoutingArea.getID();
        this.fieldArray=(ArrayList)pScoutingArea.getFieldArray().clone();
    }


    /* ------------------- Data structure operation methods ------------------- */

    public void insert(PotentialField pField) {
        fieldArray.add(pField);
    }

    public void remove(PotentialField pField) {
        fieldArray.remove(pField);
    }


    /* ------------------- Main functionality methods ------------------- */

    public PotentialField getNearestField(ScoutingUnit pScoutingUnit, Game pGame) {
        int difX=0;
        int difY=0;
        int value=0;
        int fieldIndex=0;
        if(!fieldArray.isEmpty()) {
            difX=Math.abs(pScoutingUnit.getUnit().getPosition().getPX()-fieldArray.get(0).getPosition().getPX());
            difY=Math.abs(pScoutingUnit.getUnit().getPosition().getPY()-fieldArray.get(0).getPosition().getPY());
            value=difX+difY;
            if(pGame.getMap().getGroundHeight(pScoutingUnit.getUnit().getPosition())!=pGame.getMap().getGroundHeight(fieldArray.get(0).getPosition())) {
                value+=500;
            }
            fieldIndex=0;
            for(int i=1;i<fieldArray.size();i++) {
                difX=Math.abs(pScoutingUnit.getUnit().getPosition().getPX()-fieldArray.get(i).getPosition().getPX());
                difY=Math.abs(pScoutingUnit.getUnit().getPosition().getPY()-fieldArray.get(i).getPosition().getPY());
                if(pGame.getMap().getGroundHeight(pScoutingUnit.getUnit().getPosition())!=pGame.getMap().getGroundHeight(fieldArray.get(i).getPosition())) {
                    difX+=500;
                }
                if(difX+difY<=value) {
                    fieldIndex=i;
                    value=difX+difY;
                }
            }
            //System.out.println("Unit level = "+pGame.getMap().getGroundHeight(pScoutingUnit.getUnit().getPosition()));
            //System.out.println("Field level = "+pGame.getMap().getGroundHeight(fieldArray.get(fieldIndex).getPosition()));
            return fieldArray.get(fieldIndex);
        }
        return null;
    }


    /* ------------------- Drawing methods ------------------- */

    public void drawScoutingArea(GraphicsExtended pGraphicsEx) {
        pGraphicsEx.setColor(BWColor.Orange);
        for(PotentialField pf:fieldArray) {
            pGraphicsEx.drawBoxALTERNATIVE(pf.getLeftUpperCornerBoxVector().add(new Vector2D(10,10)),pf.getRightLowerCornerBoxVector().sub(new Vector2D(10, 10)));
        }
    }


    /* ------------------- Getters and setters ------------------- */

    public int size() {
        return fieldArray.size();
    }

    public ArrayList<PotentialField> getFieldArray() {
        return fieldArray;
    }

    public void setFieldArray(ArrayList<PotentialField> fieldArray) {
        this.fieldArray = fieldArray;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
