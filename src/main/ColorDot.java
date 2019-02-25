package main;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * @author Nicholas Contreras
 */

public class ColorDot {

	private Color color;

	private double xPos, yPos;
	private double xSpeed, ySpeed;

	private int angle;
	
	private int size;
	
	private boolean isMegaDot;

	public ColorDot(Color color) {
		
		isMegaDot = Math.random() < 0.05;
		
		size = isMegaDot ? 40 : 15;
			
		this.color = color;

		angle = (int) (Math.random() * 360);

		xPos = (Math.cos(Math.toRadians(angle)) * Util.GAME_SIZE) + Util.GAME_SIZE / 2;
		yPos = (Math.sin(Math.toRadians(angle)) * Util.GAME_SIZE) + Util.GAME_SIZE / 2;

		int approachAngle = angle + 180;

		xSpeed = Math.cos(Math.toRadians(approachAngle));
		ySpeed = Math.sin(Math.toRadians(approachAngle));
	}
	
	public boolean isMega() {
		return isMegaDot;
	}

	public void update() {
		xPos += xSpeed;
		yPos += ySpeed;
	}

	public Color getColor() {
		return color;
	}

	public int getAngle() {
		return angle;
	}

	public double getDistFromCenter() {
		double distSquared = (xPos - Util.GAME_SIZE / 2) * (xPos - Util.GAME_SIZE / 2)
				+ (yPos - Util.GAME_SIZE / 2) * (yPos - Util.GAME_SIZE / 2);
		return Math.sqrt(distSquared);
	}
	
	public int getSize() {
		return size;
	}

	public void draw(Graphics2D g2d) {
		g2d.setColor(color);
		g2d.fillOval((int) (xPos - size / 2), (int) (yPos - size / 2), size, size);
	}
}
