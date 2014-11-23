package drawings;

import java.awt.*;
import java.awt.geom.*;

/**
 * AnimatingChild is an object that supports animation
 * 
 * @author Nick Walther
 */
abstract public class AnimatingChild
{
	protected double x, y;
	protected Color color;
    protected int direction = 0;
    protected int speed = 0;
    protected boolean active = false;
    /**
     * Animates the object by changing very small details, (such as size, position, or color), that affect the drawing of the object
     */
    abstract public void animate();

    /**
     * Draws the object using the given Grapics2D object.
     * @param g2 The Graphics2D object used to draw upon
     */
    abstract public void draw(Graphics2D g2);

    abstract public boolean intersects(AnimatingChild ac);

    abstract protected Rectangle2D.Double getBounds();

    public void setLocation(double x, double y) {
    	this.x = x;
    	this.y = y;
    }

    public Point2D.Double getLocation() {
        return new Point2D.Double(x, y);
    }

    public void setColor(Color color) {
    	this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void setVelocity(int direction, int speed) {
        this.direction = direction;
        this.speed = speed;
    }

    public int getDirection() {
        return direction;
    }

    public int getSpeed() {
        return speed;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
