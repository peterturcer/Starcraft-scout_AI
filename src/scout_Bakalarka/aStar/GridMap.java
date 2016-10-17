package scout_Bakalarka.aStar;

import scout_Bakalarka.GraphicsExtended;
import scout_Bakalarka.PotentialField;
import jnibwapi.Position;
import jnibwapi.util.BWColor;
import sk.hackcraft.bwu.Game;

import java.util.ArrayList;

/**
 * Grid map consists of array of blocks. Provides GEO information for other classes.
 */
public class GridMap {

    public static boolean DEBUG=false;

    private Block[][] blockMap;

    private int rows;

    private int columns;

    public static final double DAMAGE_MODIFIER=0.3;

    public static final boolean SHOW_GRIDINPOTENTIALFIELD=false;


    /* ------------------- Constructors ------------------- */

    public GridMap(int rectangleSidePX, Game pGame) {
        rows=pGame.getMap().getSize().getPY()/rectangleSidePX;
        columns=pGame.getMap().getSize().getPX()/rectangleSidePX;
        blockMap=new Block[rows][columns];

        if(GridMap.DEBUG) {
            System.out.println("--:: GridMap initialization ::--");
            System.out.println("     - Rectangle size = "+rectangleSidePX);
            System.out.println("     - Map PX = "+pGame.getMap().getSize().getPX()+" ,Grid rows = "+rows);
            System.out.println("     - Map PY = "+pGame.getMap().getSize().getPY()+" ,Grid cols = "+columns);
        }

        for(int i=0;i<rows;i++) {
            for(int j=0;j<columns;j++) {
                                                            //i a j su prehodene preto, lebo Block(x,y) - pre x zodpoveda hodnota column
                blockMap[i][j]=new Block(new Position((rectangleSidePX/2)+rectangleSidePX*j,(rectangleSidePX/2)+rectangleSidePX*i),rectangleSidePX,i,j,pGame);
            }
        }

        if(GridMap.DEBUG) {
            System.out.println("BlockMap size = "+getBlockMapSize());
        }
    }

    public GridMap(GridMap pGridMap, Game game) {
        rows=pGridMap.getRows();
        columns=pGridMap.getColumns();
        blockMap=new Block[rows][columns];
        for(int i=0;i<rows;i++) {
            for(int j=0;j<columns;j++) {
                Block b=new Block(pGridMap.getBlockMap()[i][j].getPosition(),pGridMap.getBlockMap()[i][j].getRadius(),i,j,game);
                b.setDamage(pGridMap.getBlockMap()[i][j].getDamage());
                b.setAirDamage(pGridMap.getBlockMap()[i][j].isAirDamage());
                b.setGroundDamage(pGridMap.getBlockMap()[i][j].isGroundDamage());
                b.setInPotentialField(pGridMap.getBlockMap()[i][j].isInPotentialField());
                blockMap[i][j]=b;
            }
        }
    }

    public GridMap(int pRows, int pColumns) {
        rows=pRows;
        columns=pColumns;
        blockMap=new Block[rows][columns];
    }


    /* ------------------- Initialization methods ------------------- */

    public void initializeBlockMap(Game game) {
        double mapWidth=game.getMap().getSize().getX(Position.PosType.PIXEL);
        double mapHeight=game.getMap().getSize().getY(Position.PosType.PIXEL);
        int radius=(int)(mapWidth/rows);

        for(int j=0;j<rows;j++) {
            for(int i=0;i<columns;i++) {
                Block block=new Block(new Position((radius/2)+(radius*i),(radius/2)+(radius*j)),radius,j,i,game);
                blockMap[j][i]=block;
            }
        }

        if(GridMap.DEBUG) {
            System.out.println("BlockMap size = "+getBlockMapSize());
        }
    }


    /* ------------------- Main functionality methods ------------------- */

    public void refreshGridMap(PotentialField pPotentialField) {
        Block centerBlock=getBlockByPosition_blockMap(pPotentialField.getPosition());
        int columnCounter=centerBlock.getColumn();

        double blockSideX=centerBlock.getRadius();
        int radiusBlockCount=0;
        radiusBlockCount=(int)(pPotentialField.getRadius()/blockSideX);
        /*
        while(pPotentialField.isPositionInRange(blockMap[centerBlock.getRow()][columnCounter].getPosition())) {
            radiusBlockCount++;
            columnCounter++;
        }
        */

        int row=centerBlock.getRow()-radiusBlockCount;
        int col=centerBlock.getColumn()-radiusBlockCount;
        int maxRowRange=centerBlock.getRow()+radiusBlockCount;
        int maxColRange=centerBlock.getColumn()+radiusBlockCount;

        while(row<=maxRowRange) {
            if(row<rows) {
                while(col<=maxColRange) {
                    if(col<columns) {
                        if(pPotentialField.isPositionInRange(blockMap[row][col].getPosition())) {
                            if(pPotentialField.getUnitType().getAirWeapon().getMaxRange()>30||pPotentialField.getUnitType().getAirWeapon().getMaxRange()>30) {
                                blockMap[row][col].setDamage(blockMap[row][col].getDamage() - DAMAGE_MODIFIER * pPotentialField.getUnitType().getGroundWeapon().getDamageAmount());
                            } else {
                                blockMap[row][col].setDamage(blockMap[row][col].getDamage() - DAMAGE_MODIFIER * (pPotentialField.getRangeLengthInPercent(blockMap[row][col].getPosition()) * (pPotentialField.getUnitType().getGroundWeapon().getDamageAmount())));
                            }

                            if(blockMap[row][col].getDamage()<=0.01) {
                                blockMap[row][col].setShowInGame(false);
                                blockMap[row][col].setInPotentialField(false);
                                blockMap[row][col].setAirDamage(false);
                                blockMap[row][col].setGroundDamage(false);
                            }
                        }
                    }
                    col++;
                }
            }
            row++;
            col=centerBlock.getColumn()-radiusBlockCount;
        }
    }

    public void updateGridMap(PotentialField pPotentialField) {
        Block centerBlock=getBlockByPosition_blockMap(pPotentialField.getPosition());
        int columnCounter=centerBlock.getColumn();

        double blockSideX=centerBlock.getRadius();
        int radiusBlockCount=0;
        radiusBlockCount=(int)(pPotentialField.getRadius()/blockSideX);
        /*
        while(pPotentialField.isPositionInRange(blockMap[centerBlock.getRow()][columnCounter].getPosition())) {
            radiusBlockCount++;
            columnCounter++;
        }
        */

        int row=centerBlock.getRow()-radiusBlockCount;
        int col=centerBlock.getColumn()-radiusBlockCount;
        int maxRowRange=centerBlock.getRow()+radiusBlockCount;
        int maxColRange=centerBlock.getColumn()+radiusBlockCount;

        while(row<=maxRowRange) {
            if(row<rows) {
                while(col<=maxColRange) {
                    if(col<columns) {
                        if(pPotentialField.isPositionInRange(blockMap[row][col].getPosition())) {
                            if(SHOW_GRIDINPOTENTIALFIELD) {
                                blockMap[row][col].setShowInGame(true);
                            }
                            blockMap[row][col].setInPotentialField(true);

                            boolean airToAir=pPotentialField.getUnitType().getAirWeapon().isTargetsAir();
                            boolean airToGround=pPotentialField.getUnitType().getAirWeapon().isTargetsGround();
                            boolean groundToGround=pPotentialField.getUnitType().getGroundWeapon().isTargetsGround();
                            boolean groundToAir=pPotentialField.getUnitType().getGroundWeapon().isTargetsAir();

                            if(airToAir||groundToAir) {
                                blockMap[row][col].setAirDamage(true);
                            }
                            if(airToGround||groundToGround) {
                                blockMap[row][col].setGroundDamage(true);
                            }

                            //System.out.println("Air DMG = "+blockMap[row][col].isAirDamage());
                            //System.out.println("Ground DMG = "+blockMap[row][col].isGroundDamage());

                            if(pPotentialField.getUnitType().getGroundWeapon().getMaxRange()>30||pPotentialField.getUnitType().getAirWeapon().getMaxRange()>30) {
                                blockMap[row][col].setDamage(blockMap[row][col].getDamage() + DAMAGE_MODIFIER*pPotentialField.getUnitType().getGroundWeapon().getDamageAmount());
                            } else {
                                blockMap[row][col].setDamage(blockMap[row][col].getDamage() + DAMAGE_MODIFIER * (pPotentialField.getRangeLengthInPercent(blockMap[row][col].getPosition()) * (pPotentialField.getUnitType().getGroundWeapon().getDamageAmount())));
                            }
                        }
                    }
                    col++;
                }
            }
            row++;
            col=centerBlock.getColumn()-radiusBlockCount;
        }
    }

    public Block getBlockByPosition_blockMap(Position position) {
        int posX=position.getPX();
        int posY=position.getPY();

        int blockLeftX=0;
        int blockRightX=0;
        int blockTopY=0;
        int blockBottomY=0;


        for(int j=0;j<columns;j++) {
            blockLeftX=(int)(blockMap[0][j].getPosition().getPX()-(blockMap[0][j].getRadius()/2));
            blockRightX=(int)(blockMap[0][j].getPosition().getPX()+(blockMap[0][j].getRadius()/2));

            if(posX>=blockLeftX&&posX<=blockRightX) {
                for(int i=0;i<rows;i++) {
                    blockTopY=(int)(blockMap[i][j].getPosition().getPY()-(blockMap[i][j].getRadius()/2));
                    blockBottomY=(int)(blockMap[i][j].getPosition().getPY()+(blockMap[i][j].getRadius()/2));

                    if(posY>=blockTopY&&posY<=blockBottomY) {
                        return blockMap[i][j];
                    }
                }
            }
        }
        return null;
    }

    public Block getBlockByRowAndColumn(int pRow, int pColumn) {
        if(pRow>-1&&pRow<=rows) {
            if(pColumn>-1&&pColumn<=columns) {
                return blockMap[pRow][pColumn];
            }
        }
        return null;
    }

    public int getBlockMapSize() {
        int size=0;
        for(int i=0;i<rows;i++) {
            size+=blockMap[i].length;
        }
        return size;
    }

    /**
     * Returns array of neighbour blocks to given block
     *
     * @param pActualBlock
     * @return
     */
    public ArrayList<Block> getNeighbourBlocks(Block pActualBlock) {
        int actualPositionType=getBlockPositionType(pActualBlock);
        switch (actualPositionType) {
            case 1: return getTopLeftNeighbourCoordinates(pActualBlock);
            case 2: return getTopRightNeighbourCoordinates(pActualBlock);
            case 3: return getTopMiddleNeighbourCoordinates(pActualBlock);
            case 4: return getBottomLeftNeighbourCoordinates(pActualBlock);
            case 5: return getBottomRightNeighbourCoordinates(pActualBlock);
            case 6: return getBottomMiddleNeighbourCoordinates(pActualBlock);
            case 7: return getMiddleLeftNeighbourCoordinates(pActualBlock);
            case 8: return getMiddleRightNeighbourCoordinates(pActualBlock);
            case 9: return getMiddleMiddleNeighbourCoordinates(pActualBlock);
        }
        return null;
    }

    public ArrayList<Block> getTopLeftNeighbourCoordinates(Block pActualBlock) {
        int actualRow=pActualBlock.getRow();
        int actualColumn=pActualBlock.getColumn();
        ArrayList<Block> neighbourBlocks=new ArrayList<>();

        for(int row=actualRow;row<=actualRow+1;row++) {
            for(int column=actualColumn;column<=actualColumn+1;column++) {

                if(!(row==actualRow&&column==actualColumn)) {
                    neighbourBlocks.add(blockMap[row][column]);
                }
            }
        }
        return neighbourBlocks;
    }

    public ArrayList<Block> getTopRightNeighbourCoordinates(Block pActualBlock) {
        int actualRow=pActualBlock.getRow();
        int actualColumn=pActualBlock.getColumn();
        ArrayList<Block> neighbourBlocks=new ArrayList<>();

        for(int row=actualRow;row<=actualRow+1;row++) {
            for(int column=actualColumn-1;column<=actualColumn;column++) {

                if(!(row==actualRow&&column==actualColumn)) {
                    neighbourBlocks.add(blockMap[row][column]);
                }
            }
        }
        return neighbourBlocks;
    }

    public ArrayList<Block> getTopMiddleNeighbourCoordinates(Block pActualBlock) {
        int actualRow=pActualBlock.getRow();
        int actualColumn=pActualBlock.getColumn();
        ArrayList<Block> neighbourBlocks=new ArrayList<>();

        for(int row=actualRow;row<=actualRow+1;row++) {
            for(int column=actualColumn-1;column<=actualColumn+1;column++) {

                if(!(row==actualRow&&column==actualColumn)) {
                    neighbourBlocks.add(blockMap[row][column]);
                }
            }
        }
        return neighbourBlocks;
    }

    public ArrayList<Block> getBottomLeftNeighbourCoordinates(Block pActualBlock) {
        int actualRow=pActualBlock.getRow();
        int actualColumn=pActualBlock.getColumn();
        ArrayList<Block> neighbourBlocks=new ArrayList<>();

        for(int row=actualRow-1;row<=actualRow;row++) {
            for(int column=actualColumn;column<=actualColumn+1;column++) {

                if(!(row==actualRow&&column==actualColumn)) {
                    neighbourBlocks.add(blockMap[row][column]);
                }
            }
        }
        return neighbourBlocks;
    }

    public ArrayList<Block> getBottomRightNeighbourCoordinates(Block pActualBlock) {
        int actualRow=pActualBlock.getRow();
        int actualColumn=pActualBlock.getColumn();
        ArrayList<Block> neighbourBlocks=new ArrayList<>();

        for(int row=actualRow-1;row<=actualRow;row++) {
            for(int column=actualColumn-1;column<=actualColumn;column++) {

                if(!(row==actualRow&&column==actualColumn)) {
                    neighbourBlocks.add(blockMap[row][column]);
                }
            }
        }
        return neighbourBlocks;
    }

    public ArrayList<Block> getBottomMiddleNeighbourCoordinates(Block pActualBlock) {
        int actualRow=pActualBlock.getRow();
        int actualColumn=pActualBlock.getColumn();
        ArrayList<Block> neighbourBlocks=new ArrayList<>();

        for(int row=actualRow-1;row<=actualRow;row++) {
            for(int column=actualColumn-1;column<=actualColumn+1;column++) {

                if(!(row==actualRow&&column==actualColumn)) {
                    neighbourBlocks.add(blockMap[row][column]);
                }
            }
        }
        return neighbourBlocks;
    }

    public ArrayList<Block> getMiddleLeftNeighbourCoordinates(Block pActualBlock) {
        int actualRow=pActualBlock.getRow();
        int actualColumn=pActualBlock.getColumn();
        ArrayList<Block> neighbourBlocks=new ArrayList<>();

        for(int row=actualRow-1;row<=actualRow+1;row++) {
            for(int column=actualColumn;column<=actualColumn+1;column++) {

                if(!(row==actualRow&&column==actualColumn)) {
                    neighbourBlocks.add(blockMap[row][column]);
                }
            }
        }
        return neighbourBlocks;
    }

    public ArrayList<Block> getMiddleRightNeighbourCoordinates(Block pActualBlock) {
        int actualRow=pActualBlock.getRow();
        int actualColumn=pActualBlock.getColumn();
        ArrayList<Block> neighbourBlocks=new ArrayList<>();

        for(int row=actualRow-1;row<=actualRow+1;row++) {
            for(int column=actualColumn-1;column<=actualColumn;column++) {

                if(!(row==actualRow&&column==actualColumn)) {
                    neighbourBlocks.add(blockMap[row][column]);
                }
            }
        }
        return neighbourBlocks;
    }

    public ArrayList<Block> getMiddleMiddleNeighbourCoordinates(Block pActualBlock) {
        int actualRow=pActualBlock.getRow();
        int actualColumn=pActualBlock.getColumn();
        ArrayList<Block> neighbourBlocks=new ArrayList<>();

        for(int row=actualRow-1;row<=actualRow+1;row++) {
            for(int column=actualColumn-1;column<=actualColumn+1;column++) {

                if(!(row==actualRow&&column==actualColumn)) {
                    neighbourBlocks.add(blockMap[row][column]);
                }
            }
        }
        return neighbourBlocks;
    }

    public int getBlockPositionType(Block pActualBlock) {
        int actualRow=pActualBlock.getRow();
        int actualColumn=pActualBlock.getColumn();
        int maxRows=rows;
        int maxColumns=columns;

        int actualPositionType=-1;

        if(actualRow-1<0) {                                     //je v nultom riadku
            if(actualColumn-1<0) {                              //je v L.H rohu
                actualPositionType=1;                           //je v L.H. rohu - situacia 1
            } else if(actualColumn+1==maxColumns) {              //je v P.H. rohu
                actualPositionType=2;                           //je v P.H. rohu - situacia 2
            } else {                                            //je na nultom riadku ale nie v rohoch
                actualPositionType=3;                           //je na nultom riadku ale nie v rohoch - situacia 3
            }
        } else if(actualRow+1==maxRows) {                        //je v poslednom riadku
            if(actualColumn-1<0) {                              //je v L.D rohu
                actualPositionType=4;                           //je v L.D. rohu - situacia 4
            } else if(actualColumn+1==maxColumns) {              //je v P.D. rohu
                actualPositionType=5;                           //je v P.D. rohu - situacia 5
            } else {                                            //je na maxumalnom riadku ale nie v rohoch
                actualPositionType=6;                           //je na maximalnomm riadku ale nie v rohoch - situacia 6
            }
        } else {                                                //nie je na nultom ani na poslednom riadku
            if(actualColumn-1<0) {                              //je na lavom kraji
                actualPositionType=7;                           //je na lavom kraji - situacia 7
            } else if(actualColumn+1==maxColumns) {              //je na pravom kraji
                actualPositionType=8;                           //je na pravom kraji - situacia 8
            } else {                                            //nie je na krajoch
                actualPositionType=9;                           //nie je na krajoch - situacia 9
            }
        }

        //System.out.println("Block row="+actualRow+",column="+actualColumn+",position type="+actualPositionType);

        return actualPositionType;
    }

    public boolean isSameBlock(Block pBlock1, Block pBlock2) {
        if(pBlock1.getRow()==pBlock2.getRow()&&pBlock1.getColumn()==pBlock2.getColumn()) {
            return true;
        }
        return false;
    }


    /* ------------------- Drawing methods ------------------- */

    public void drawDangerGrid(GraphicsExtended graphicsEx,BWColor color) {
        for(int i=0;i<rows;i++) {
            for(int j=0;j<columns;j++) {
                if(blockMap[i][j].isShowInGame()) {
                    blockMap[i][j].drawBlock(graphicsEx, color);
                }
            }
        }
    }

    public void drawGridMap(GraphicsExtended graphicsEx,BWColor color) {
        for(int i=0;i<rows;i++) {
            for(int j=0;j<columns;j++) {
                blockMap[i][j].drawBlock(graphicsEx, color);
            }
        }
    }

    /* ------------------- Getters and Setters ------------------- */

    public Block[][] getBlockMap() {
        return blockMap;
    }

    public void setBlockMap(Block[][] blockMap) {
        this.blockMap = blockMap;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }
}
