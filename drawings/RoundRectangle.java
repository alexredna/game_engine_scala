package drawings;

import java.awt.*;
import java.awt.geom.*;

public class RoundRectangle extends AnimatingChild
{
    private double width, height, arcwidth, archeight;

    public RoundRectangle()
    {
        x = 10;
        y = 10;
        width = 50;
        height = 50;
		arcwidth = 15;
		archeight = 15;
        color = Color.BLACK;
    }

	public void setWidth(double width) {
		this.width = width;
	}

	public double getWidth() {
		return this.width;
	}

	public void setHeight(double height) {
		this.height = height;
	}
	
	public double getHeight() {
		return height;
	}

	public void setArcWidth(double arcwidth) {
		this.arcwidth = arcwidth;
	}

	public double getArcWidth() {
		return this.arcwidth;
	}

	public void setArcHeight(double archeight) {
		this.archeight = archeight;
	}
	
	public double getArcHeight() {
		return this.archeight;
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
		if(visible) {
	        RoundRectangle2D.Double rect = new RoundRectangle2D.Double(x, y, width, height, arcwidth, archeight);
    	    g2.setColor(color);
        	g2.fill(rect);
		}
    }

    public boolean intersects(AnimatingChild ac) {
        return ac.getBounds().intersects(x, y, width, height);
    }

    protected Rectangle2D.Double getBounds() {
        return new Rectangle2D.Double(x, y, width, height);
    }

    public String toString() {
        return "RoundRectangle(x=" + x + ", y=" + y +
            ", w=" + width + ", h=" + height + ", arcw=" + arcwidth + ", arch=" + archeight + ")";
    }
}
