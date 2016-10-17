package scout_Bakalarka;

import jnibwapi.JNIBWAPI;
import jnibwapi.Unit;
import sk.hackcraft.bwu.Graphics;
import sk.hackcraft.bwu.Vector2D;

/**
 * Extension of framework graphics methods
 */
public class GraphicsExtended extends Graphics {

    public GraphicsExtended(JNIBWAPI jnibwapi) {
        super(jnibwapi);
    }

    /**
     * Draws a line from a certain position to certain position.
     * Coordinate system state is not taken into account.
     *
     * created by Misho
     *
     * @param from
     * @param to
     */
    public void drawLineALTERNATIVE(Vector2D from, Vector2D to) {
        boolean savedScreen = isScreenCoordinates();
        setGameCoordinates(true);
        drawLine(from,to);
        setScreenCoordinates(savedScreen);
    }

    /**
     * Draws a line from a certain unit to certain unit.
     * Coordinate system state is not taken into account.
     *
     * created by Misho
     *
     * @param from
     * @param to
     */
    public void drawLine(Unit from, Unit to) {
        boolean savedScreen = isScreenCoordinates();
        setGameCoordinates(true);
        drawLine(from.getPositionVector(), to.getPositionVector());
        setScreenCoordinates(savedScreen);
    }

    /**
     * Draws the outline for a circle around a specific position.
     * Coordinate system state is not taken into account.
     *
     * created by Misho
     *
     * @param position specific position
     * @param radius radius of a circle in a game pixels.
     */
    public void drawCircleALTERNATIVE(Vector2D position, int radius)
    {
        boolean savedScreen = isScreenCoordinates();
        setGameCoordinates(true);
        drawCircle(position, radius);
        setScreenCoordinates(savedScreen);
    }

    /**
     * Draws a single point at a certain unit. Coordinate
     * system state is not taken into account.
     *
     * created by Misho
     *
     * @param position
     */
    public void drawDotALTERNATIVE(Vector2D position)
    {
        boolean savedScreen = isScreenCoordinates();
        setGameCoordinates(true);
        drawDot(position);
        setScreenCoordinates(savedScreen);
    }

    /**
     * Draws a text at a certain position. Coordinate
     * system state is not taken into account.
     *
     * created by Misho
     *
     * @param position
     */
    public void drawTextALTERNATIVE(Vector2D position, Object text)
    {
        boolean savedScreen = isScreenCoordinates();
        setGameCoordinates(true);
        drawText(position, text);
        setScreenCoordinates(savedScreen);
    }

    public void drawBoxALTERNATIVE(Vector2D topLeftCorner, Vector2D bottomRightCorner) {
        boolean savedScreen = isScreenCoordinates();
        setGameCoordinates(true);
        drawBox(topLeftCorner,bottomRightCorner);
        setScreenCoordinates(savedScreen);
    }

}
