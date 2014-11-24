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
        radius = 50;
        color = Color.BLACK;
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
        Ellipse2D.Double rect = new Ellipse2D.Double(x, y, radius, radius);
        g2.setColor(color);
        g2.fill(rect);
    }

    public boolean intersects(AnimatingChild ac) {
        return ac.getBounds().intersects(x, y, radius, radius);
    }

    protected Rectangle2D.Double getBounds() {
        return new Rectangle2D.Double(x, y, radius, radius);
    }

    public String toString() {
        return "Circle(x=" + x + ", y=" + y + ", r=" + radius + ")";
    }
}
