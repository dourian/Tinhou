import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;

/**
 * @author Dorian, maxwell
 * Jan 24, 2022
 * bullet class. stores bullet data.
 */
public class Bullet extends Entity {
	static Image[] icons;		//list of possible bullet icons
	static double[] radii;		//list of possible bullet radii corresponding to icon
	private double timetolive;	//amount of time that bullet can exist
	static {
		icons = new Image[6];
		radii = new double[6];
		for(int i = 0; i < 6; i++) {
			try {
				icons[i] = ImageIO.read(Bullet.class.getResourceAsStream("resources/bullet" + (i+1) + ".png")); //static constructor that initializes icons and radii
			} catch (IOException e) {
				e.printStackTrace();
			}
			radii[i] = (double)icons[i].getWidth(null)/2;
		}
	}
	/**
	 * constructor for bullet
	 * @param position initial position of bullet
	 * @param velocity initial velocity of bullet
	 * @param type type of bullet,
	 * must be in range [0,6)
	 */
	Bullet(Complex position, Complex velocity, int type) {
		super(position, velocity, radii[type], icons[type]);
		timetolive = 3;
		int W = icon.getWidth(null), H = icon.getHeight(null);
		BufferedImage rotated = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = rotated.createGraphics();
		g.rotate(vel.arg()+Math.PI/2, W/2, H/2);
		g.drawImage(icon, 0, 0, null);
		icon = rotated;
	}
	/**
	 * overrides cycle function of entity.
	 * @param f time elapsed since last cycle
	 * @return if bullet may still exist
	 */
	public boolean cycle(float f) {
		timetolive -= f;
		pos = pos.plus(vel.mult(f));
		return timetolive > 0;
	}
	/**
	 * @return angle of bullet velocity
	 */
	public double angle() { return vel.arg(); }
}
