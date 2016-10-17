package sk.hackcraft.bwu;

import jnibwapi.JNIBWAPI;
import jnibwapi.Map;
import jnibwapi.Position;
import jnibwapi.Unit;
import jnibwapi.util.BWColor;

/**
 * Class represeting all operations available on StarCraft graphics to draw
 * something. This instance works as a state machine with 2 states being remembered:
 * 
 * Screen Coordinates / Game Coordinates - Whether the coordinates provided when drawing
 * something should be considered as a coordinates of a certain pixel on a screen or a coordinates
 * on a game plane. Default state is Screen Coordinates.
 * 
 * Color - <code>BWColor</code> instance of current paint that should be used when drawing something.
 * Default color is <code>BWColor.White</code>.
 * 
 * @author nixone
 * 
 */
public class Graphics
{
	final protected JNIBWAPI jnibwapi;

	private BWColor color = BWColor.White;
	private boolean screen = true;

	protected Graphics(JNIBWAPI jnibwapi)
	{
		this.jnibwapi = jnibwapi;
	}

	private Graphics(Graphics graphics)
	{
		this(graphics.jnibwapi);

		this.color = graphics.color;
		this.screen = graphics.screen;
	}

	void setCanvas(boolean screen)
	{
		this.screen = screen;
	}
	
	/**
	 * Creates new graphics to create new drawing context for colors and setting
	 * of coordinates.
	 * 
	 * @return
	 */
	public Graphics createSubGraphics()
	{
		return new Graphics(this);
	}

	/**
	 * Draws an outline for a box that is defined by its top left and bottom
	 * right corners.
	 * 
	 * @param topLeftCorner
	 * @param bottomRightCorner
	 */
	public void drawBox(Vector2D topLeftCorner, Vector2D bottomRightCorner)
	{
		jnibwapi.drawBox(new Position(Math.round(topLeftCorner.x), Math.round(topLeftCorner.y)), new Position(Math.round(bottomRightCorner.x), Math.round(bottomRightCorner.y)), color, false, screen);
	}

	/**
	 * Fills the box defined by its top left and bottom right corners.
	 * 
	 * @param topLeftCorner
	 * @param bottomRightCorner
	 */
	public void fillBox(Vector2D topLeftCorner, Vector2D bottomRightCorner)
	{
		jnibwapi.drawBox(new Position(Math.round(topLeftCorner.x), Math.round(topLeftCorner.y)), new Position(Math.round(bottomRightCorner.x), Math.round(bottomRightCorner.y)), color, true, screen);
	}

	/**
	 * Draws the outline for a circle around a specific unit. 
	 * Coordinate system state is not taken into account.
	 * 
	 * @param unit specific unit
	 * @param radius radius of a circle in a game pixels.
	 */
	public void drawCircle(Unit unit, int radius)
	{
		boolean savedScreen = isScreenCoordinates();
		setGameCoordinates(true);

		drawCircle(unit.getPositionVector(), radius);

		setScreenCoordinates(savedScreen);
	}

	/**
	 * Draws the outline of a circle around a certain position with a certain
	 * radius.
	 * 
	 * @param position
	 *            center of a circle
	 * @param radius
	 *            in pixels
	 */
	public void drawCircle(Vector2D position, int radius)
	{
		jnibwapi.drawCircle(new Position(Math.round(position.x), Math.round(position.y)), radius, color, false, screen);
	}

	/**
	 * Fills the circle around a unit with a certain specified radius. 
	 * Coordinate system state is not taken into account.
	 * 
	 * @param unit
	 * @param radius
	 */
	public void fillCircle(Unit unit, int radius)
	{
		boolean savedScreen = isScreenCoordinates();
		setGameCoordinates(true);

		fillCircle(unit.getPositionVector(), radius);

		setScreenCoordinates(savedScreen);
	}

	/**
	 * Fills the circle around a certain position with a certain radius.
	 * 
	 * @param position
	 * @param radius
	 */
	public void fillCircle(Vector2D position, int radius)
	{
		jnibwapi.drawCircle(new Position(Math.round(position.x), Math.round(position.y)), radius, color, true, screen);
	}

	/**
	 * Draws a single point at a certain position.
	 * 
	 * @param position
	 */
	public void drawDot(Vector2D position)
	{
		jnibwapi.drawDot(new Position(Math.round(position.x), Math.round(position.y)), color, screen);
	}

	/**
	 * Draws a single point at a certain unit. Coordinate
	 * system state is not taken into account.
	 * 
	 * @param unit
	 */
	public void drawDot(Unit unit)
	{
		boolean savedScreen = isScreenCoordinates();
		setGameCoordinates(true);

		drawDot(unit.getPositionVector());

		setScreenCoordinates(savedScreen);
	}

	/**
	 * Draws a line from a certain position to a certain position.
	 * 
	 * @param from start position of line
	 * @param to end position of line
	 */
	public void drawLine(Vector2D from, Vector2D to)
	{
		jnibwapi.drawLine(new Position(Math.round(from.x), Math.round(from.y)), new Position(Math.round(to.x), Math.round(to.y)), color, screen);
	}

	/**
	 * Draws a text at a certain position. Coordinates are of a left edge of the text
	 * (text is left-aligned).
	 * 
	 * @param position position of left edge of the text
	 * @param text text to be drawn, <code>Object.toString()</code> is used.
	 */
	public void drawText(Vector2D position, Object text)
	{
		jnibwapi.drawText(new Position(Math.round(position.x), Math.round(position.y)), text.toString(), screen);
	}

	/**
	 * Draws a text at a certain unit. Text is left-aligned so it spans to the right of a unit.
	 * Coordinate system state is not taken into account.
	 * 
	 * @param unit 
	 * 			to draw text upon
	 * @param text 
	 * 			text to be drawn, <code>Object.toString()</code> is used
	 * @param offset
	 * 			offset in game coordinates of text relative to unit
	 * 
	 */
	public void drawText(Unit unit, Object text, Vector2D offset)
	{
		boolean savedScreen = isScreenCoordinates();
		setGameCoordinates(true);

		drawText(offset.add(unit.getPositionVector()), text);

		setScreenCoordinates(savedScreen);
	}

	/**
	 * Draws a text at a certain unit. Text is left-aligned so it spans to the right of a unit.
	 * Coordinate system state is not taken into account.
	 * 
	 * @param unit 
	 * 			to draw text upon
	 * @param text 
	 * 			text to be drawn, <code>Object.toString()</code> is used
	 * 
	 */
	public void drawText(Unit unit, Object text)
	{
		drawText(unit, text, Vector2D.ZERO);
	}

	/**
	 * Sets the color state of a graphics object. Next <code>draw*</code> or <code>fill*</code>
	 * calls will be painted with this color until next <code>setColor()</code> with another
	 * color is called.
	 * 
	 * @param color 
	 * 			to paint
	 */
	public void setColor(BWColor color)
	{
		this.color = color;
	}

	/**
	 * Sets whether the coordinates should be screen or game coordinates
	 * 
	 * @param screen
	 * 			whether we are using screen coordinates
	 */
	public void setScreenCoordinates(boolean screen)
	{
		this.screen = screen;
	}

	/**
	 * Sets the coordinates to be screen coordinates
	 */
	public void setScreenCoordinates()
	{
		setScreenCoordinates(true);
	}

	/**
	 * Tells a state of coordinate system.
	 * 
	 * @return whether we are using screen coordinates at this moment
	 */
	public boolean isScreenCoordinates()
	{
		return this.screen;
	}

	/**
	 * Sets whether the coordinates should be screen or game coordinates
	 * 
	 * @param game
	 * 			whether we are using game coordinates
	 */
	public void setGameCoordinates(boolean game)
	{
		this.screen = !game;
	}

	/**
	 * Sets the coordinates to be game coordinates
	 */
	public void setGameCoordinates()
	{
		setGameCoordinates(true);
	}

	/**
	 * Tells a state of coordinate system.
	 * 
	 * @return whether we are using game coordinates at this moment
	 */
	public boolean isGameCoordinates()
	{
		return !screen;
	}

	/**
	 * Creates a minimap with usage of this graphics.
	 * 
	 * @param map 
	 * 			map to create a minimap on
	 * @param position 
	 * 			position of top left corner of the minimap to be displayed in
	 * @param size 
	 * 			(width, height) vector of the minimap size in pixels on screen
	 * @return created minimap
	 */
	public Minimap createMinimap(Map map, Vector2D position, Vector2D size)
	{
		return new Minimap(this, map, position, size);
	}
}
