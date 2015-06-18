package airplane;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;

import javax.imageio.ImageIO;

/**
 * Class representing the plane.
 */
public class Plane implements MouseListener, MouseMotionListener {

	/**
	 * Plane image width and height when drawing.
	 */
	public static int WIDTH = 175, HEIGTH = 75;

	/**
	 * plane X position.<br>
	 * Local coordinate (meaning '0' is the center, not left edge).
	 */
	private int pos;
	/**
	 * X coordinate of the mouse cursor.<br>
	 * Global coordinate (meaning '0' is the left edge).
	 */
	private int mousePos;

	/**
	 * Plane direction to be drawn:<br>
	 * <li>'<b>c</b>'enter - headed straight</li> <li>'<b>l</b>'eft - headed
	 * left</li> <li>'<b>r</b>'ight - headed right</li>
	 */
	private char dir;
	/**
	 * Plane and bomb images to be drawn.
	 */
	private Image img, bombImg, holeImg;

	/**
	 * Queue of bombs.
	 */
	private LinkedList<Bomb> bombs;

	/**
	 * Inner class representing the bomb.
	 */
	class Bomb {

		/**
		 * Max explosion radius.
		 */
		public static final int MAX_RADIUS = 40;

		/**
		 * Bomb coordinates.<br>
		 * (Local (x is derived from plane's 'pos', y=0 is at plane height).
		 */
		private double x, y;
		private double width, height;
		private double holeWidth, holeHeight;
		/**
		 * Current explosion radius.
		 */
		private int radius;
		/**
		 * Bomb's current state.
		 */
		private boolean exploding, expanding;

		/**
		 * Bomb default constructor
		 */
		public Bomb() {
			x = pos;
			y = 10;
			width = 10;
			height = 15;
			holeWidth = holeHeight = 30;

			radius = 0;
			exploding = false;
			expanding = false;
		}

		public double getX() {
			return x + Game.WIDTH / 2;
		}

		public double getY() {
			return y + HEIGTH / 2 + Game.HEIGHT / 6 + height;
		}

		public boolean isExploding() {
			return exploding;
		}

		public int getRadius() {
			return radius;
		}

		/**
		 * Defines bomb explosion behavior.<br>
		 * <ol>
		 * <li>When initiated first time sets exploding and expanding to
		 * <b>true</b>;</li>
		 * <li>Increases explosion radius until it reaches <b>MAX_RADIUS</b>;</li>
		 * <li>Decreases radius until it reaches <b>0</b>;</li>
		 * <li>deleted the bomb from the queue;</li>
		 * </ol>
		 */
		private void explode() {
			if (!exploding) {
				exploding = true;
				expanding = true;
			}

			if (expanding) {
				radius += 10;
				if (radius >= MAX_RADIUS)
					expanding = false;
			} else if (radius > 0)
				radius -= 10;
			else {
				bombs.poll();
				// System.out.println("deleted bomb");
			}
			// System.out.println("exploding. radius: " + radius);
		}

		/**
		 * Moves the bomb down, if 'explosion height' is not reached,<br>
		 * else calls <b>explode()</b> and moves the explosion.
		 */
		public void update() {

			if (y <= Game.HEIGHT / 3) {
				width *= 0.95;
				height *= 0.95;
				y *= 1.2;
			} else {
				x *= 1.02;
				y *= 1.03;
				explode();
			}
		}

		/**
		 * Draws the bomb or its explosion at the appropriate position.
		 * 
		 * @param g
		 *            - the Graphics Object on which the bomb is drawn
		 */
		public void draw(Graphics g) {
			if (exploding) {
				g.fillOval((int) getX() - radius, (int) (getY() - radius),
						radius * 2, radius * 2);
			} else
				g.drawImage(bombImg, (int) (getX() - width / 2),
						(int) (getY() - height), (int) width, (int) height,
						null);

			/*
			 * g.setColor(Color.BLACK); g.fillOval((int) getX(), (int) getY(),
			 * 1, 1); g.setColor(Color.ORANGE);
			 */
		}
	}

	/**
	 * Timeout before the next bomb can be dropped ('0' means bomb can be
	 * dropped).
	 */
	private int timeout;

	/**
	 * Default constructor:<br>
	 * Creates a plane in the center, facing forward, no bombs, '<b>0</b>'
	 * timeout.
	 */
	public Plane() {

		pos = 0;
		dir = 'c';
		setImg();
		setBombImg();
		bombs = new LinkedList<>();
		timeout = 0;
	}

	public int getPos() {
		return pos;
	}

	/**
	 * Sets the plane image depending on the current direction.
	 */
	public void setImg() {
		try {
			img = ImageIO.read(new File("./data/plane/" + dir + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Setter for the bomb image.
	 */
	public void setBombImg() {
		try {
			bombImg = ImageIO.read(new File("./data/plane/bomb.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Counts down till next bomb can be dropped.
	 */
	private void timeout() {
		if (timeout > 0)
			timeout--;
	}

	/**
	 * Creates new bomb, sets <b>timeout</b> to 1 sec.
	 */
	private void dropBomb() {

		bombs.add(new Bomb());

		timeout = 1000 / Game.FRAMERATE;
		// System.out.println("bomb dropped");
	}

	/**
	 * Gets the currently exploding bomb.
	 * 
	 * @return <b>bomb</b>, if exploding,<br><b>null</b> otherwise.
	 */
	public Bomb getExplosion() {
		if (!bombs.isEmpty() && bombs.peek().exploding)
			return bombs.peek();
		else
			return null;
	}

	/**
	 * Moves the plane towards the cursor with the speed depending on the
	 * distance to the cursor.<br>
	 * (The farer the cursor, the faster the plane moves)
	 */
	private void move() {
		int speed = (mousePos - pos - Game.WIDTH / 2) / 10;
		pos += speed;

		if (speed > 5 && dir != 'l') {
			dir = 'l';
			setImg();
		} else if (speed < -5 && dir != 'r') {
			dir = 'r';
			setImg();
		} else if (speed >= -5 && speed <= 5 && dir != 'c') {
			dir = 'c';
			setImg();
		}
	}

	/**
	 * Draws the plane in the appropriate location.
	 * 
	 * @param g
	 *            - Graphics object on which to be drawn
	 */
	private void drawPlane(Graphics g) {
		g.drawImage(img, pos - WIDTH / 2 + Game.WIDTH / 2, Game.HEIGHT / 6,
				WIDTH, HEIGTH, null);
	}

	/**
	 * Updates the plane and the bombs:
	 * <ol>
	 * <li>Moves the plane;</li>
	 * <li>Moves the bombs;</li>
	 * <li>Counts down till the next bomb can be dropped;</li>
	 */
	public void update() {

		move();
		for (Bomb b : bombs)
			b.update();

		timeout();
	}

	/**
	 * Draws the plane and the bombs.
	 * 
	 * @param g
	 *            - Graphics object on which to be drawn
	 */
	public void draw(Graphics g) {
		for (Bomb b : bombs)
			b.draw(g);
		drawPlane(g);
	}

	/**
	 * Mouse click event listener.<br>
	 * Drops a new bomb if possible.
	 */
	@Override
	public void mouseClicked(MouseEvent arg0) {

		try {
			if (timeout <= 0)
				dropBomb();
		} catch (ConcurrentModificationException e) {
			e.getMessage();
		}
	}

	/**
	 * Mouse move event listener.<br>
	 * Updates the mouse X position field.
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		mousePos = e.getX();
	}

	/*
	 * Unused mouse listener methods. Do nothing.
	 */
	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}
}
