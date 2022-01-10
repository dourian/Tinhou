import java.awt.*;
import java.io.*;

import javax.imageio.ImageIO;
/**
 * @author max13
 * bullet class. stores bullet data.
 */
public class Bullet extends Entity {
	static Image[] icons;
	static double[] radii;
	static {
		icons = new Image[6];
		radii = new double[6];
		for(int i = 0; i < 6; i++) {
			try {
				icons[i] = ImageIO.read(new File("bullet" + (i+1) + ".png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			radii[i] = (double)icons[i].getHeight(null)/2;
		}
	}
	
	private double timetolive;
	Bullet(Complex position, Complex velocity, int type) {
		super(position, velocity, radii[type], icons[type]);
		timetolive = 100;
	}
	
	public boolean cycle(float f) {
		timetolive -= f;
		pos = pos.plus(vel.mult(f));
		return timetolive > 0;
	}
	
	public double angle() { return vel.arg(); }
}
