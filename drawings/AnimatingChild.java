package drawings;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

/**
 * AnimatingChild is an object that supports animation
 * 
 * @author Nick Walther
 */
abstract public class AnimatingChild
{
    protected double x, y;
    protected Color color;
    protected Color borderColor = null;
    protected double direction = 0;
    protected double speed = 0;
    protected boolean active = false;
    protected boolean visible = true;

    /**
     * Animates the object by changing very small details,
     * (such as size, position, or color), that affect the 
     * drawing of the object
     */
    abstract public void animate();

    /**
     * Draws the object using the given Grapics2D object.
     * @param g2 The Graphics2D object used to draw upon
     */
    abstract public void draw(Graphics2D g2);

    abstract public Rectangle2D.Double getBounds();

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

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    public Color getBorderColor() {
        if (borderColor == null)
            return color;
        return borderColor;
    }

    public void setVelocity(double direction, double speed) {
        this.direction = direction;
        this.speed = speed;
    }

    public double getDirection() {
        return direction;
    }

    public double getSpeed() {
        return speed;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean intersects(AnimatingChild other) {
        if (!visible || !other.visible)
            return false;

        Rectangle2D.Double t = getBounds();
        Rectangle2D.Double o = other.getBounds();

        return t.intersects(o.x, o.y, o.width, o.height);
    }

    public boolean contains(Point p) {
        return getBounds().contains(p.x, p.y);
    }
}
