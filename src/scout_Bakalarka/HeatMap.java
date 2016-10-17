package scout_Bakalarka;

import jnibwapi.Position;
import jnibwapi.util.BWColor;
import sk.hackcraft.bwu.Game;

/**
 * Layer above the game map - grid structure consisting of rectangular potential fields.
 * Provides the information about the heat in certain zones of the map
 */
public class HeatMap {

    public static boolean DEBUG=false;

    /**
     * Two dimensional array of Potential fields in heat map
     */
    private PotentialField[][] fieldMap;

    private int rows;

    private int columns;


    /* ------------------- Constructors ------------------- */

    /**
     * Creates the instance of HeatMp with given Game instance
     *
     * @param game
     */
    public HeatMap(Game game) {

    }


    /* ------------------- Initialization methods ------------------- */

    public void initializeHeatMap(int pRectangleSidePX, Game pGame) {
        rows=pGame.getMap().getSize().getPY()/pRectangleSidePX;
        columns=pGame.getMap().getSize().getPX()/pRectangleSidePX;
        fieldMap=new PotentialField[rows][columns];

        if(HeatMap.DEBUG) {
            System.out.println("--:: HeatMap initialization ::--");
            System.out.println("     - Rectangle size = "+pRectangleSidePX);
            System.out.println("     - Map PX = "+pGame.getMap().getSize().getPX()+" ,Grid rows = "+rows);
            System.out.println("     - Map PY = "+pGame.getMap().getSize().getPY()+" ,Grid cols = "+columns);
        }

        for(int i=0;i<rows;i++) {
            for(int j=0;j<columns;j++) {
                //i a j su prehodene preto, lebo Block(x,y) - pre x zodpoveda hodnota column
                fieldMap[i][j]=new PotentialField(pGame,new Position((pRectangleSidePX/2)+pRectangleSidePX*j,(pRectangleSidePX/2)+pRectangleSidePX*i),pRectangleSidePX,pRectangleSidePX,i,j);
            }
        }
//
//        if(GridMap.DEBUG) {
//            System.out.println("BlockMap size = "+getBlockMapSize());
//        }
    }


    /* ------------------- Main functonality methods ------------------- */

    public PotentialField getHeatBlock(int pRow, int pColumn) {
        return fieldMap[pRow][pColumn];
    }

    /**
     * Retruns the exact potential field block, where the given position is.
     *
     * @param position
     * @return PotentialField
     */
    public PotentialField getHeatBlockContainingPosition(Position position) {
        int posX=position.getX(Position.PosType.PIXEL);
        int posY=position.getY(Position.PosType.PIXEL);

        float upperBoxX;
        float lowerBoxX;
        float upperBoxY;
        float lowerBoxY;

        for(int j=0;j<columns;j++) {
            upperBoxX=fieldMap[0][j].getLeftUpperCornerBoxVector().toPosition().getPX();
            lowerBoxX=fieldMap[0][j].getRightLowerCornerBoxVector().toPosition().getPX();
            if(position.getPX()>=upperBoxX&&position.getPX()<=lowerBoxX) {
                for(int i=0;i<rows;i++) {
                    upperBoxY=fieldMap[i][j].getLeftUpperCornerBoxVector().toPosition().getPY();
                    lowerBoxY=fieldMap[i][j].getRightLowerCornerBoxVector().toPosition().getPY();
                    if(position.getPY()>=upperBoxY&&position.getPY()<=lowerBoxY) {
                        /*CONSOLE LOG */
                        System.out.println("Position found in potential field with center coordinates ["+i+","+j+"] :"+fieldMap[i][j].getCenterVector().toPosition().toString());
                        /*END MESSAGE*/
                        return fieldMap[i][j];
                    }
                }
            }
        }
        return null;
    }


    /* ------------------- Real-Time management methods ------------------- */

    public void heatManagement(Game pGame) {
        for(int i=0;i<rows;i++) {
            for(int j=0;j<columns;j++) {
                if(pGame.getJNIBWAPI().isVisible(fieldMap[i][j].getPosition())) {
                    fieldMap[i][j].setHeat(0);
                } else {
                    fieldMap[i][j].increaseHeat();
                }
            }
        }
    }


    /* ------------------- Drawing functions ------------------- */

    public void drawHeatMap(Game pGame) {
        for(int i=0;i<rows;i++) {
            for(int j=0;j<columns;j++) {
                fieldMap[i][j].showGraphicsRectangular(pGame,BWColor.Grey);
            }
        }
    }


    /* ------------------- Getters and setters ------------------- */

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }
}
