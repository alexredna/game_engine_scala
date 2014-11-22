package drawings;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

/**
 * GreenMovingCircle is an AnimatingChild extension visually represented by a green circle
 * It changes its y position by one pixel every time its animate method is called
 * 
 * @author Nick Walther
 */
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
        x += 1;
    }

    public void draw(Graphics2D g2)
    {
        Ellipse2D.Double rect = new Ellipse2D.Double(x, y, radius, radius);
        g2.setColor(color);
        g2.fill(rect);
    }

    public String toString() {
        return "Circle(x=" + x + ", y=" + y + ", r=" + radius + ")";
    }
}
