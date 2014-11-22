package drawings;

import java.awt.*;

/**
 * AnimatingChild is an object that supports animation
 * 
 * @author Nick Walther
 */
abstract public class AnimatingChild
{
	protected double x, y;
	protected Color color;

    /**
     * Animates the object by changing very small details, (such as size, position, or color), that affect the drawing of the object
     */
    abstract public void animate();

    /**
     * Draws the object using the given Grapics2D object.
     * @param g2 The Graphics2D object used to draw upon
     */
    abstract public void draw(Graphics2D g2);

    public void setLocation(double x, double y) {
    	this.x = x;
    	this.y = y;
    }

    public void setColor(Color color) {
    	this.color = color;
    }
}
