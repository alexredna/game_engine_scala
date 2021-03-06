package drawings;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public class Circle extends AnimatingChild
{
    private double radius;

    public Circle()
    {
        x = 10;
        y = 10;
        radius = 25;
        color = Color.BLACK;
    }

    public Circle(Circle other) {
        this.x = other.x;
        this.y = other.y;
        this.color = new Color(other.color.getRGB());
        if (other.borderColor != null)
            this.borderColor = new Color(other.borderColor.getRGB());
        this.direction = other.direction;
        this.speed = other.speed;
        this.active = other.active;
        this.visible = other.visible;

        this.radius = other.radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getRadius() {
        return this.radius;
    }
    public void animate()
    {
        if (active) {
            x += speed * Math.cos(Math.toRadians(direction));
            y -= speed * Math.sin(Math.toRadians(direction));    
        }
    }

    public void draw(Graphics2D g2)
    {
        if (visible) {
            Ellipse2D.Double shape = new Ellipse2D.Double(x, y, 2*radius, 2*radius);
            g2.setColor(color);
            g2.fill(shape);
            if (borderColor != null) {
                g2.setColor(borderColor);
                g2.draw(shape);
            }
        }
    }

    public Rectangle2D.Double getBounds() {
        return new Rectangle2D.Double(x, y, 2*radius, 2*radius);
    }

    public String toString() {
        return "Circle(x=" + x + ", y=" + y + ", r=" + radius + ")";
    }
}
