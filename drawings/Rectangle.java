package drawings;

import java.awt.*;
import java.awt.geom.*;

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
        if (active) {
            x += speed * Math.cos(Math.toRadians(direction));
            y -= speed * Math.sin(Math.toRadians(direction));
        }
    }

    public void draw(Graphics2D g2)
    {
        Rectangle2D.Double rect = new Rectangle2D.Double(x, y, width, height);
        g2.setColor(color);
        g2.fill(rect);
    }

    public boolean intersects(AnimatingChild ac) {
        return ac.getBounds().intersects(x, y, width, height);
    }

    protected Rectangle2D.Double getBounds() {
        return new Rectangle2D.Double(x, y, width, height);
    }

    public String toString() {
        return "Rectangle(x=" + x + ", y=" + y +
            ", w=" + width + ", h=" + height + ")";
    }
}
