import java.awt.Graphics;
import java.awt.Image;

/**
 * @author max13
 * entity class. stores entity data such as position, size, hit detection information, icon
 */
public abstract class Entity {
	protected Complex pos, vel;
	protected double rad;
	protected Image icon;
	Entity(Complex position, Complex velocity, double radius, Image i) {
		pos = position; vel = velocity; rad = radius; icon = i;
	}
	public Complex pos() {return pos;}
	public Complex vel() {return vel;}
	public void setPos(Complex c) {pos = c;}
	public void setVel(Complex c) {vel = c;}
	public boolean correctPos(int H, int W) {
		Complex oldPos = pos;
		pos = new Complex(Math.min(W-2*rad, Math.max(rad, pos.real())),Math.min(H-2*rad, Math.max(rad, pos.imag())));
		return !oldPos.equals(pos);
	}
	public boolean collides(Entity c) {return pos.minus(c.pos).abs()<rad+c.rad;}
	
	/**
	 * cycle function that engine calls every game tick to update entity
	 * @param f
	 * elapsed time
	 * @return
	 * true if entity is allowed to exist
	 * false if entity should be deleted
	 */
	public abstract boolean cycle(float f);
	public void repaint(Graphics g) {
		
		g.drawImage(icon, (int)(pos.real()-icon.getWidth(null)/2),(int)(pos.imag()-icon.getHeight(null)/2), null);
		//g.drawOval((int)(pos.real()-rad), (int)(pos.imag()-rad), (int)(2*rad), (int)(2*rad)); //DEBUG
	}
}
