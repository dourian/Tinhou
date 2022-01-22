import java.awt.*;
import java.awt.image.BufferedImage;
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
		timetolive = 1.9;
		int W = icon.getWidth(null), H = icon.getHeight(null);
		BufferedImage rotated = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = rotated.createGraphics();
		g.rotate(vel.arg()+Math.PI/2, W/2, H/2);
		g.drawImage(icon, 0, 0, null);
		icon = rotated;
	}
	
	public boolean cycle(float f) {
		timetolive -= f;
		pos = pos.plus(vel.mult(f));
		return timetolive > 0;
	}
	
	public double angle() { return vel.arg(); }
}
