package airplane;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import airplane.Enemy.Tank;
import airplane.Plane.Bomb;

/**
 * The main Airplane applet class. Initializes, starts and runs the game.
 * 
 * @author Alex Pomirko
 * @author Stas Erema
 */
@SuppressWarnings("serial")
public class Game extends Applet implements Runnable {

	/**
	 * Applet dimensions (in px).
	 */
	public static final int WIDTH = 800, HEIGHT = 450;
	/**
	 * Number of milliseconds between each frame of the game.
	 */
	public static final int FRAMERATE = 1000 / 24;

	/**
	 * Background.
	 */
	private Background bg;
	/**
	 * Plane.
	 */
	private Plane plane;
	/**
	 * Enemy (Tank queue).
	 */
	private Enemy enemy;
	/**
	 * Lives left and current score.
	 */
	private GUI gui;


	/*
	 * Technical attributes: applet's Graphics Object and Thread Object.
	 */
	private Graphics2D g;
	private Thread thread;

	@Override
	/**
	 * Applet's Initialization method:<br>
	 * <ol>
	 *  <li>Sets applet dimensions and default background color;</li>
	 *  <li>Initializes attributes;</li>
	 *  <li>Adds mouse listeners.</li>
	 * </ol>
	 */
	public void init() {

		/*
		 * 	NOTE:
		 * Please don't resize the window. The Game wont work as expected.
		 */
		
		setSize(new Dimension(WIDTH, HEIGHT));
		setBackground(Color.GRAY);

		setFocusable(true);

		g = (Graphics2D) getGraphics();
		bg = new Background();
		enemy = new Enemy();
		plane = new Plane();
		gui = new GUI();

		g.setColor(Color.ORANGE);
		g.setFont(new Font("Arial", Font.PLAIN, 20));

		addMouseMotionListener(plane);
		addMouseListener(plane);

	}

	@Override
	/**
	 * Applet's start method: starts the game thread.
	 */
	public void start() {

		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}

	@Override
	/**
	 * Applet's run method: runs the main game loop.
	 */
	public void run() {
		
		
		/* 
		 * The main game loop. Updates and draws game,
		 * Checks if the player lost,
		 * Waits for 1/24 seconds.
		 */
		while (true) {
			update(g);
			paint(g);

			if(gui.checkLose())
				break;
			
			try {
				Thread.sleep(Game.FRAMERATE);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		/*
		 * Prints out "you lost" text,
		 * Throws an exception to stop and wait for the player to close the game.
		 * (not a right thing to do, but works as a quick solution)
		 */
		gui.drawLost(g);
		new Exception("YOU LOST!!!").printStackTrace();
	}

	/**
	 * repaints graphics of Background, Enemy (all tanks), Plane (and bombs) and GUI (score and lives icons).
	 */
	@Override
	public void paint(Graphics g) {
		bg.draw(g);
		enemy.draw(g);
		plane.draw(g);
		gui.draw(g);
	}

	/**
	 * Updates all moving elements, checks for hit and missed enemies.
	 */
	@Override
	public void update(Graphics g) {
		bg.update();
		enemy.update();
		plane.update();

		checkExplosion();
		checkMiss();
	}

	/**
	 * Checks to see if the bomb is exploding and if any enemies are hit, adds score.
	 */
	private void checkExplosion() {
		Bomb explosion = plane.getExplosion();

		if (explosion != null) {
			for (Tank t : enemy)
				if (!t.isHit()
						&& t.distTo(explosion.getX(), explosion.getY()) <= explosion
						.getRadius() * 1.25) {
					t.hit();
					gui.addScore();
				}
		}
	}

	/**
	 * Checks for any missed tanks, subtracts lives.
	 */
	private void checkMiss() {
		if (enemy.missed()) {
			gui.addMissed();
		}
	}
}
