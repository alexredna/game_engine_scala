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

    public Rectangle(Rectangle other) {
        this.x = other.x;
        this.y = other.y;
        this.color = new Color(other.color.getRGB());
        if (other.borderColor != null)
            this.borderColor = new Color(other.borderColor.getRGB());
        this.direction = other.direction;
        this.speed = other.speed;
        this.active = other.active;
        this.visible = other.visible;

        this.width = other.width;
        this.height = other.height;
    }

    public void setSize(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public double getWidth() {
        return this.width;
    }
    
    public double getHeight() {
        return height;
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
            Rectangle2D.Double shape = new Rectangle2D.Double(x, y, width, height);
            g2.setColor(color);
            g2.fill(shape);
            if (borderColor != null) {
                g2.setColor(borderColor);
                g2.draw(shape);
            }
        }
    }

    public Rectangle2D.Double getBounds() {
        return new Rectangle2D.Double(x, y, width, height);
    }

    public String toString() {
        return "Rectangle(x=" + x + ", y=" + y +
            ", w=" + width + ", h=" + height + ")";
    }
}
