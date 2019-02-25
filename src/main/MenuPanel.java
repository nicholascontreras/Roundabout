package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * @author Nicholas Contreras
 */

@SuppressWarnings("serial")
public class MenuPanel extends JPanel {

	private BufferedImage background;

	private int textAlpha = 0, deltaAlpha = 0;

	public MenuPanel() {
		try {
			background = ImageIO.read(getClass().getResourceAsStream("/menu.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		g2d.drawImage(background.getScaledInstance(getWidth(), getHeight(), Image.SCALE_DEFAULT), 0, 0, null);

		String text = "Press any button to begin";

		g2d.setColor(new Color(255, 255, 255, textAlpha));

		g2d.setFont(new Font("SansSerif", Font.BOLD, 36));

		int width = g2d.getFontMetrics().stringWidth(text);

		g2d.drawString(text, getWidth() / 2 - width / 2, getHeight() / 2);

		textAlpha += deltaAlpha;

		if (textAlpha >= 255) {
			deltaAlpha = -15;
			textAlpha = 255;
		}

		if (textAlpha <= 0) {
			deltaAlpha = 15;
			textAlpha = 0;
		}
	}

}
