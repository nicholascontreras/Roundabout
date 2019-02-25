package main;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * @author Nicholas Contreras
 */

@SuppressWarnings("serial")
public class GamePanel extends JPanel implements Runnable, WindowListener, KeyListener {

	private JFrame frame;

	private JPanel outerPanel;

	private JPanel menuPanel;

	private BufferedImage background;
	private WheelModel wheelModel;
	private ArrayList<ColorDot> colorDots;

	private boolean rotateClockwise, rotateCounterclockwise;

	private boolean pause;

	private SpawnManager spawnManager;

	private int score;

	private boolean isSplashScreen;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new GamePanel());
	}

	@Override
	public void run() {
		frame = new JFrame("Roundabout");

		outerPanel = new JPanel(new CardLayout());

		this.setPreferredSize(new Dimension(Util.GAME_SIZE, Util.GAME_SIZE));

		menuPanel = new MenuPanel();

		outerPanel.add(menuPanel, "menu");
		outerPanel.add(this, "game");

		frame.addKeyListener(this);

		frame.addWindowListener(this);

		frame.add(outerPanel);

		frame.pack();
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);

		Util.GAME_SIZE = getWidth();

		try {
			background = ImageIO.read(getClass().getResourceAsStream("/background.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			frame.setIconImage(ImageIO.read(getClass().getResourceAsStream("/icon.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}

		colorDots = new ArrayList<ColorDot>();

		pause = true;

		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				if (!pause) {
					update();
				}
				frame.repaint();
			}
		}, 0, 15);

		spawnManager = new SpawnManager();

		isSplashScreen = true;

		switchMode("menu");
	}

	private void update() {

		if (rotateClockwise) {
			wheelModel.rotate(-1);
		} else if (rotateCounterclockwise) {
			wheelModel.rotate(1);
		}

		for (int i = 0; i < colorDots.size(); i++) {
			ColorDot curDot = colorDots.get(i);
			curDot.update();

			if (curDot.getDistFromCenter() < wheelModel.getSize() / 2 + curDot.getSize() / 2) {

				Color c = wheelModel.getColorAtAngle(curDot.getAngle());

				if (c != null && c.equals(curDot.getColor())) {
					if (curDot.isMega()) {
						score += 3;
					} else {
						score++;
					}
					playSound("/good.wav");
				} else {
					if (curDot.isMega()) {
						score -= 3;
					} else {
						score--;
					}
					playSound("/bad.wav");
				}

				colorDots.remove(i);
				i--;
			}
		}

		ColorDot x = spawnManager.update();
		if (x != null) {
			colorDots.add(x);
		}

		if (score < 0) {
			gameOver();
		}
	}

	private void setupGame() {

		int difficulty = -1;

		while (difficulty == -1) {
			difficulty = JOptionPane.showOptionDialog(frame, "Select a game difficulty:", "New Game",
					JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
					new String[] { "Very Easy", "Easy", "Medium", "Hard", "Very Hard" }, null);
		}

		Util.colors = new Color[difficulty + 2];

		for (int i = 0; i < Util.colors.length; i++) {
			Util.colors[i] = Util.colorBank[i];
		}

		wheelModel = new WheelModel(Util.colors, Util.GAME_SIZE / 4);
		colorDots.clear();
		score = 0;
		spawnManager.setup(difficulty, Util.colors);
		pause = false;

		switchMode("game");
	}

	private void gameOver() {
		pause = true;

		int response = JOptionPane.showOptionDialog(frame, "Would you like to play again?", "Game Over",
				JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null,
				new String[] { "Yes, play again", "No, quit game" }, null);

		if (response == 0) {
			setupGame();
		} else {
			System.exit(0);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {

		Graphics2D g2d = (Graphics2D) g;

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2d.drawImage(background.getScaledInstance(getWidth(), getHeight(), Image.SCALE_DEFAULT), 0, 0, null);

		if (!pause) {

			g2d.translate(getWidth() / 2, getHeight() / 2);

			wheelModel.drawWheel(g2d);

			g2d.translate(-getWidth() / 2, -getHeight() / 2);

			for (int i = 0; i < colorDots.size(); i++) {
				colorDots.get(i).draw(g2d);
			}

			g2d.setColor(Color.BLACK);
			String scoreString = score + "";
			g2d.setFont(new Font("SansSerif", Font.BOLD, 32));
			int stringWidth = g2d.getFontMetrics().stringWidth(scoreString);
			int stringHeight = g2d.getFontMetrics().getAscent();
			g2d.drawString(scoreString, getWidth() / 2 - stringWidth / 2, getHeight() / 2 + stringHeight / 2);
		}
	}

	public void switchMode(String mode) {
		((CardLayout) outerPanel.getLayout()).show(outerPanel, mode);
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		pause = true;
		int result = JOptionPane.showConfirmDialog(frame, "Are you sure you want to quit?", "Exit Game",
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null);
		if (result == JOptionPane.YES_OPTION) {
			System.exit(0);
		}
		pause = false;
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (isSplashScreen) {
			isSplashScreen = false;
			setupGame();
		} else {
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				rotateClockwise = true;
				rotateCounterclockwise = false;
			} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				rotateCounterclockwise = true;
				rotateClockwise = false;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			rotateClockwise = false;
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			rotateCounterclockwise = false;
		}
	}

	private void playSound(String sound) {
		new Thread(new Runnable() {
			public void run() {
				try {
					Clip clip = AudioSystem.getClip();
					AudioInputStream inputStream = AudioSystem
							.getAudioInputStream(getClass().getResourceAsStream(sound));
					clip.open(inputStream);
					clip.start();
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
		}).start();
	}
}
