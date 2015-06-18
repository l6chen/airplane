package airplane;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Class representing the animated background.
 */
public class Background {
	
	/**
	 * Image slides.
	 */
	private Image slides[];
	/**
	 * Index of the current slide to be drawn.
	 */
	private int currSlide = 0;
	/**
	 * Rate of change of slides.
	 */
	private int rate, currRateNum;
	
	/**
	 * Default constructor.
	 */
	public Background(){
		
		setImg();
		setRate(10);
	}
	
	/**
	 * 
	 */
	public void setImg(){
		File files[] = new File("./data/bg").listFiles();
		
		slides = new Image[files.length];
		for(int i=0; i<slides.length; i++)
			try {
				slides[i] = ImageIO.read(files[i]);
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	/**
	 * Sets number of frames needed for one slide to change.
	 * @param rate - slides change rate.
	 */
	public void setRate(int rate){
		this.rate = rate;
		currRateNum = rate;
	}
	
	/**
	 * Updates the background. 
	 */
	public void update() {
		if(currRateNum >= rate){
			currSlide++;
			currSlide %= slides.length;
			currRateNum = 1;
		}
		else{
			currRateNum++;
		}
	}
	
	/**
	 * Draws the current background slide.
	 * @param g - Graphics Object to be drawn to
	 */
	public void draw(Graphics g){
		g.drawImage(slides[currSlide], 0, 0, null);
		
	}
}
