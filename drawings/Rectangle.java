package drawings;

import java.awt.*;
import java.awt.geom.*;

/**
 * RedResizingRectangle is an AnimatingChild extension visually represented by a red rectangle
 * It changes its width and height by one pixel every time its animate method is called
 * 
 * @author Nick Walther
 */
public class Rectangle extends AnimatingChild
{
    private double width, height;

    public Rectangle()
    {
        x = 10;
        y = 10;
        width = 50;
        height = 50;
        color = Color.BLACK;
    }

    public void animate()
    {
        x += 1;
    }

    public void draw(Graphics2D g2)
    {
        Rectangle2D.Double rect = new Rectangle2D.Double(x,
                y,
                width,
                height);
        g2.setColor(color);
        g2.fill(rect);
    }

    public String toString() {
        return "Rectangle(x=" + x + ", y=" + y +
            ", w=" + width + ", h=" + height + ")";
    }
}
