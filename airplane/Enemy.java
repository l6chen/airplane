package airplane;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import airplane.Enemy.Tank;

/**
 * The class representing the enemy, a queue of Tanks.
 */
@SuppressWarnings("serial")
public class Enemy extends LinkedList<Tank> {

	/**
	 * Class representing a single tank.
	 */
	class Tank {
		/**
		 * Tanks physical characteristics.
		 */
		private int x, y, width, height;
		/**
		 * Shows if a tank was hit with a bomb.
		 */
		private boolean hit;

		/**
		 * Default constructor. Creates a tank on the horizon at the center ±
		 * 40px with the size of 30x20px.
		 */
		private Tank() {
			x = (int) ((Math.random() - 0.5) * 80);
			y = 20;

			width = 30;
			height = 20;

			hit = false;

			// System.out.println("new enemy at " + x + ", " + y);
		}

		public int getX() {
			return x + Game.WIDTH / 2;
		}

		public int getY() {
			return y + Game.HEIGHT / 3 + height / 2;
		}

		public boolean isHit() {
			return hit;
		}

		/**
		 * Is called when a tank is hit with a bomb.
		 */
		public void hit() {
			hit = true;
			System.out.println("enemy hit");
		}

		/**
		 * Calculates the distance (in px) from the center of the tank to a
		 * specific point.
		 * 
		 * @param x
		 *            - X coord of the Point
		 * @param y
		 *            - Y coord of the Point
		 * @return distance to the Point
		 */
		public double distTo(double x, double y) {

			double w = (getX() - x);
			double h = (getY() - y);

			return (int) Math.sqrt(w * w + h * h);
		}
	}

	/**
	 * 'Alive' and 'hit' images of the tank.
	 */
	private Image img, img_h;

	/**
	 * Timeout till the next enemy can be spawned.
	 */
	private int timeout;

	/**
	 * Default constructor. Sets the <b>timeout</b> to a random time between 5
	 * and 8 sec.
	 */
	public Enemy() {
		setImgs();

		timeout = (int) (Math.random() * 3 * 1000 / Game.FRAMERATE) + 5 * 1000
				/ Game.FRAMERATE;
	}

	/**
	 * Sets the <b>timeout</b> to a specified number of seconds.
	 * 
	 * @param num
	 *            - number of seconds
	 */
	public Enemy(int num) {
		setImgs();

		timeout = num * 1000 / Game.FRAMERATE;
	}

	public void setImgs() {
		try {
			img = ImageIO.read(new File("./data/enm/live.png"));
			img_h = ImageIO.read(new File("./data/enm/hit.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Tries to spawn a new enemy. If successful, sets timeout to [1, 3] sec.
	 */
	private void trySpawn() {
		if (timeout == 0) {
			add(new Tank());
			timeout = (int) (Math.random() * 1000 / Game.FRAMERATE) + 2 * 1000
					/ Game.FRAMERATE;
		}
	}

	/**
	 * Moves all tanks closer.
	 */
	private void move() {
		for (Tank t : this) {
			t.x *= 1.04;
			t.y *= 1.05;

			t.width *= 1.05;
			t.height *= 1.05;
		}
	}

	/**
	 * Checks if the first tank is was missed by the plane, i.e. if the tank is
	 * off the screen and not hit.
	 * 
	 * @return <b>true</b> if a tank was missed,<br>
	 *         <b>false</b> otherwise.
	 */
	public boolean missed() {
		if (!isEmpty() && peek().y > Game.HEIGHT) {
			if (poll().isHit())
				return false;
			else
				System.err.println("enemy missed");
			return true;
		}
		return false;
	}

	/**
	 * Counts down until next enemy can spawn.
	 */
	private void timeout() {
		if (timeout > 0)
			timeout--;
	}

	/**
	 * Updates the enemies:
	 * <ol>
	 * <li>Moves all tanks closer;</li>
	 * <li>Tries to spawn new enemies.</li>
	 * </ol>
	 */
	public void update() {
		move();
		trySpawn();

		timeout();
	}

	/**
	 * Draws all the tanks in the appropriate positions and in the appropriate
	 * state.
	 * 
	 * @param g
	 *            - Graphics Object to be drawn to
	 */
	public void draw(Graphics g) {
		for (Tank t : this){
			if (t.hit) {
				g.drawImage(img_h, t.x - t.width / 2 + Game.WIDTH / 2, t.y
						+ Game.HEIGHT / 3, t.width, t.height, null);
			} else {
				g.drawImage(img, t.x - t.width / 2 + Game.WIDTH / 2, t.y
						+ Game.HEIGHT / 3, t.width, t.height, null);
			}
			
			//g.fillOval(t.getX(), t.getY(), 1, 1);
		}
	}
}
