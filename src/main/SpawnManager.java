package main;

import java.awt.Color;

/**
 * @author Nicholas Contreras
 */

public class SpawnManager {

	private int difficulty;

	private Color[] colors;

	private long lastSpawn;
	private Color lastColor;
	private int minTimeBetweenSpawns;
	private int maxTimeBetweenSpawns;
	
	private double pressureFactor;

	public void setup(int difficulty, Color[] colors) {
		this.difficulty = difficulty;

		this.colors = colors;

		lastSpawn = 0;
		lastColor = Color.BLACK;
		
		pressureFactor = 0;

		switch (difficulty) {
		case 0:
			minTimeBetweenSpawns = 5000;
			maxTimeBetweenSpawns = 8000;
			break;
		case 1:
			minTimeBetweenSpawns = 4500;
			maxTimeBetweenSpawns = 7500;
			break;
		case 2:
			minTimeBetweenSpawns = 4000;
			maxTimeBetweenSpawns = 7000;
			break;
		case 3:
			minTimeBetweenSpawns = 3500;
			maxTimeBetweenSpawns = 6500;
			break;
		case 4:
			minTimeBetweenSpawns = 3000;
			maxTimeBetweenSpawns = 6000;
			break;
		}
	}

	public ColorDot update() {

		double scaledMin = minTimeBetweenSpawns * (1 - pressureFactor);
		double scaledMax = maxTimeBetweenSpawns * (1 - pressureFactor);
		
		long timeSinceLastSpawn = System.currentTimeMillis() - lastSpawn;
		
		if (timeSinceLastSpawn < scaledMax) {
			return null;
		}

		double minMaxRange = scaledMax - scaledMin;

		double ratioIntoRange = timeSinceLastSpawn / minMaxRange;

		if (Math.random() < ratioIntoRange) {
			Color c = colors[(int) (Math.random() * colors.length)];
			if (c.equals(lastColor)) {
				c = colors[(int) (Math.random() * colors.length)];
			}
			
			if (Math.random() < (difficulty + 1) / 10.0) {
				pressureFactor += (difficulty + 1) / 50.0;
				System.out.println("pressure factor " + pressureFactor);
				if (pressureFactor > 0.9) {
					pressureFactor = 0.9;
				}
			}
			

			lastColor = c;
			lastSpawn = System.currentTimeMillis();

			return new ColorDot(c);
		} else {
			return null;
		}
	}

}
