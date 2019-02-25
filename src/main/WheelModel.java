package main;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * @author Nicholas Contreras
 */

public class WheelModel {

	private static final int zoneSpacingSize = 30;

	private Color[] colors;
	
	private int size;

	private int rotation;
	private int zoneSize;
	private int lapseSize;

	public WheelModel(Color[] colors, int size) {
		this.colors = colors;
		this.size = size;
		rotation = 0;
		zoneSize = (360 - (colors.length * zoneSpacingSize)) / colors.length;
		lapseSize = 360 / colors.length;
	}
	
	public int getSize() {
		return size;
	}
	
	public void rotate(int angle) {
		rotation += angle;
		
		if (rotation >= 360) {
			rotation -= 360;
		}
		
		if (rotation < 0) {
			rotation += 360;
		}
	}

	public void drawWheel(Graphics2D g2d) {
		g2d.setColor(Color.GRAY);
		g2d.fillOval(-size / 2, -size / 2, size, size);
		

		int curDrawAngle = rotation - zoneSpacingSize / 2;

		for (Color curColor : colors) {
			g2d.setColor(curColor);
			g2d.fillArc(-size / 2, -size / 2, size, size, curDrawAngle, -zoneSize);
			curDrawAngle -= lapseSize;
		}

		g2d.setColor(Color.GRAY);
		g2d.fillOval(-size * 3 / 8, -size * 3 / 8, size * 3 / 4, size * 3 / 4);
	}
	
	public Color getColorAtAngle(int angle) {
		
		angle += rotation;
		
		int lapseArea = (angle / lapseSize) % colors.length;
		
		int posInLapse = angle % lapseSize;
		
		if (posInLapse < zoneSpacingSize / 2) {
			return null;
		}
		if (posInLapse + zoneSpacingSize / 2 > lapseSize) {
			return null;
		}
		
		return colors[lapseArea];
	}
}
