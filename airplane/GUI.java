package airplane;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GUI {

	/**
	 * Number of lives of the plane.
	 */
	public static final int MAX_HEALTH = 10;

	/**
	 * Statistic counter.
	 */
	private int missed, score;
	/**
	 * GUI icons.
	 */
	private Image iconScore, iconLives;

	/**
	 * Default constructor.
	 */
	public GUI() {
		missed = 0;
		score = 0;

		try {
			iconScore = ImageIO.read(new File("./data/icon/score.png"));
			iconLives = ImageIO.read(new File("./data/icon/lives.png"));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Increments missed lives.
	 */
	public void addMissed() {
		++missed;
	}

	/**
	 * Increments score.
	 */
	public void addScore() {
		++score;
	}

	/**
	 * Checks if number of lost lives is greater than MAX.
	 * @return <b>true</b>, if player lost, <b>false</b> otherwise.
	 */
	public boolean checkLose() {
		return missed > MAX_HEALTH;
	}

	/**
	 * Draws lives left and score icons.
	 * @param g - Graphics Object to be drawn to.
	 */
	public void draw(Graphics g) {

		for (int i = 1; i <= MAX_HEALTH - missed; i++)
			g.drawImage(iconLives, Game.WIDTH - i * 10 - i * 50, 10, 50, 50, null);

		g.drawImage(iconScore, 10, Game.HEIGHT - 60, 50, 50, null);
		g.drawString(":" + score, 65, Game.HEIGHT - 25);
	}
	
	/**
	 * Draws "YOU LOST" string in the middle of the screen.
	 * @param g - Graphics Object to be drawn to.
	 */
	public void drawLost(Graphics g) {
		g.setFont(new Font("Arial", Font.BOLD, 50));
		g.drawString("YOU LOST!!!", (int) (Game.WIDTH/3.5), (int) (Game.HEIGHT/1.5));
	}
}
