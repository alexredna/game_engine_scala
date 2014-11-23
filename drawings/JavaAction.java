package drawings;

import java.awt.*;

public class JavaAction {
	private String name;

	private boolean isColor_p;
	private Color color_p;
	private boolean isVelocity_p;
	private int direction_p;
	private int speed_p;

	private boolean isColor_r;
	private Color color_r;
	private boolean isVelocity_r;
	private int direction_r;
	private int speed_r;

	public JavaAction(String name) {
		this.name = name;
	}

	public void activateColor(Color color, boolean isOnPress) {
		if (isOnPress) {
			isColor_p = true;
			color_p = color;
		} else {
			isColor_r = true;
			color_r = color;
		}
	}

	public void activateVelocity(int direction, int speed, boolean isOnPress) {
		if (isOnPress) {
			isVelocity_p = true;
			direction_p = direction;
			speed_p = speed;
		} else {
			isVelocity_r = true;
			direction_r = direction;
			speed_r = speed;
		}
	}

	public void performPress(AnimatingChild child) {
		if (isColor_p)
			child.setColor(color_p);
		if (isVelocity_p)
			child.setVelocity(direction_p, speed_p);
	}

	public void performRelease(AnimatingChild child) {
		if (isColor_r)
			child.setColor(color_r);
		if (isVelocity_r)
			child.setVelocity(direction_r, speed_r);
	}

	public String toString() {
		return name;
	}
}