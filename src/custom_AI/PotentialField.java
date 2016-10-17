package custom_AI;

import jnibwapi.Position;
import jnibwapi.Unit;
import jnibwapi.types.UnitType;
import jnibwapi.util.BWColor;
import sk.hackcraft.bwu.Game;
import sk.hackcraft.bwu.Vector2D;
import sk.hackcraft.bwu.selection.UnitSet;

/**
 * Represents certain area on the map and provides information about this area
 */
public class PotentialField {

    private int id;

    private UnitType unitType;

    private double priority;

    private int row;
    private int column;

    /**
     * Center coordinate X
     */
    private int X;

    /**
     * Center coordinate Y
     */
    private int Y;

    /**
     * Radius of the potential field
     */
    private double radius;

    /**
     * Heat value
     */
    private double heat;

    /**
     * Custom graphics functions for drawing
     */
    private GraphicsExtended graphics;


    /* ------------------- Constructors ------------------- */

    /**
     * Initialization of PF with given game instance, graphics instance, center X and Y coordiantes and given radius
     *
     * @param game
     * @param X
     * @param Y
     * @param radius
     */
    public PotentialField(Game game, int X, int Y, int radius) {
        this.priority=0;
        if(game!=null) {
            this.graphics=new GraphicsExtended(game.getJNIBWAPI());
        }
        this.X=X;
        this.Y=Y;
        this.radius=radius;
        this.heat=0;
        this.id=-1;
        this.row=-1;
        this.column=-1;
    }

    public PotentialField(Game game,Position position,int radius) {
        this.priority=0;
        this.graphics=new GraphicsExtended(game.getJNIBWAPI());
        this.X=position.getPX();
        this.Y=position.getPY();
        this.radius=radius;
        this.heat=0;
        this.id=-1;
        this.row=-1;
        this.column=-1;
    }

    public PotentialField(Game pGame, Unit pUnit) {
        this.priority=0;
        this.graphics=new GraphicsExtended(pGame.getJNIBWAPI());
        this.X=pUnit.getPosition().getPX();
        this.Y=pUnit.getPosition().getPY();
        this.radius=pUnit.getType().getSightRange();
        this.heat=0;
        this.id=pUnit.getID();
        this.unitType=pUnit.getType();
        this.row=-1;
        this.column=-1;
        //System.out.println("PF radius = "+radius);
    }

    public PotentialField(Game game, int X, int Y, int radius, double priority) {
        this.priority=priority;
        this.graphics=new GraphicsExtended(game.getJNIBWAPI());
        this.X=X;
        this.Y=Y;
        this.radius=radius;
        this.heat=0;
        this.id=-1;
        this.row=-1;
        this.column=-1;
    }

    public PotentialField(Game game,Position position,int radius, int priority) {
        this.priority=priority;
        this.graphics=new GraphicsExtended(game.getJNIBWAPI());
        this.X=position.getPX();
        this.Y=position.getPY();
        this.radius=radius;
        this.heat=0;
        this.id=-1;
        this.row=-1;
        this.column=-1;
    }

    public PotentialField(Game game,Position position,int radius, int priority, int pRow, int pColumn) {
        this.priority=priority;
        this.graphics=new GraphicsExtended(game.getJNIBWAPI());
        this.X=position.getPX();
        this.Y=position.getPY();
        this.radius=radius;
        this.heat=0;
        this.id=-1;
        this.row=pRow;
        this.column=pColumn;
    }


    /* ------------------- Main functonality methods ------------------- */

    /**
     * Returns upper left corner vector with coordinates
     *
     * @return Vector2D
     */
    public Vector2D getLeftUpperCornerBoxVector() {
        return new Vector2D((float)(X-(radius/2)),(float)(Y-(radius/2)));
    }

    /**
     * Returns lower right corner vector with coordinates
     *
     * @return Vector2D
     */
    public Vector2D getRightLowerCornerBoxVector() {
        return new Vector2D((float)(X+(radius/2)),(float)(Y+(radius/2)));
    }

    /**
     * Returns center vector with coordinates
     *
     * @return Vector2D
     */
    public Vector2D getCenterVector() {
        return new Vector2D(X,Y);
    }

    /**
     * If field is visible to allied units
     *
     * @return
     */
    public boolean isVisible(UnitSet units) {
        for(Unit u : units) {
            if(u.isAt(new Vector2D(X,Y),u.getType().getSightRange())) {
                return true;
            }
        }
        return false;
    }

    public boolean isVisible(Unit unit) {
        if(unit.isAt(new Vector2D(X,Y),unit.getType().getSightRange())) {
            return true;
        }
        return false;
    }

    public boolean isUnitInRange(Unit pUnit) {
        Position pos=new Position(X,Y);
        if(pos.getPDistance(pUnit.getPosition())<=radius&&pUnit.getID()==id) {
            return true;
        }
        return false;
    }

    public boolean isPositionInRange(Position pPosition) {
        Position pos=new Position(X,Y);
        if(pos.getPDistance(pPosition)<=radius) {
            return true;
        }
        return false;
    }

    public double getRangeLengthInPercent(Position pPosition) {
        double distance=pPosition.getPDistance(new Position(X,Y));
        double percent=100-(100/radius)*distance;
        return percent/100;
    }


    /* ------------------- Real-time management methods ------------------- */

    /**
     * Increases heat by +0,01
     */
    public void increaseHeat() {
        this.heat+=1/(double)100;
    }


    /* ------------------- Drawing functions ------------------- */

    /**
     * Visually draws circular field on screen
     */
    public void showGraphicsCircular(Game game,BWColor color) {
        graphics.setColor(color);
        graphics.drawDotALTERNATIVE(new Vector2D(X, Y));
        graphics.drawCircleALTERNATIVE(new Vector2D(X, Y), (int) radius);
        graphics.drawTextALTERNATIVE(new Vector2D(X - 10, Y - 20), String.format("%.3g%n", heat));
        graphics.drawTextALTERNATIVE(new Vector2D(X - 10, Y - 40), isVisible(game.getMyUnits()));
    }

    /**
     * Visually draws rectangular potential field on screen
     */
    public void showGraphicsRectangular(Game game,BWColor color) {
        graphics.setColor(color);
        graphics.drawDotALTERNATIVE(new Vector2D(X,Y));
        graphics.drawBoxALTERNATIVE(new Vector2D((float) (X - (radius / 2)), (float) (Y - (radius / 2))), new Vector2D((float) (X + (radius / 2)), (float) (Y + (radius / 2))));
        graphics.drawTextALTERNATIVE(new Vector2D(X - 10, Y - 20), String.format("%.3g%n", heat));
        graphics.drawTextALTERNATIVE(new Vector2D(X - 10, Y - 40), isVisible(game.getMyUnits()));

        /*TESTING ADDED INFO
        graphics.drawTextALTERNATIVE(new Vector2D(X - 10, Y - 40), new Vector2D(X,Y).toPosition().getPX()+";"+new Vector2D(X,Y).toPosition().getPY());
        /*END TESTING*/
    }

    /* ------------------- Getters and Setters ------------------- */

    public Position getPosition() {
        return new Position(X,Y);
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public UnitType getUnitType() {
        return unitType;
    }

    public void setUnitType(UnitType unitType) {
        this.unitType = unitType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPriority() {
        return priority;
    }

    public void setPriority(double priority) {
        this.priority = priority;
    }

    public void setHeat(int heat) {
        this.heat=heat/(double)100;
    }

    public double getHeat() {
        return heat;
    }

    public int getX() {
        return X;
    }

    public void setX(int x) {
        X = x;
    }

    public int getY() {
        return Y;
    }

    public void setY(int y) {
        Y = y;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
