import java.awt.Graphics;
import java.awt.Image;
/***
 * @author maxwell
 * Jan 24, 2022
 */
/**
 * @author maxwell
 * entity class. stores entity data such as position, size, hit detection information, icon
 */
public abstract class Entity {
	protected Complex pos, vel;	//position and velocity of entity in 2d space
	protected double rad;		//radius of entity hitbox
	protected Image icon;		//sprite of entity
	/**
	 * constructs entity
	 * @param position position of entity
	 * @param velocity velocity of entity
	 * @param radius radius of entity hitbox
	 * @param i sprite of entity
	 */
	Entity(Complex position, Complex velocity, double radius, Image i) {
		pos = position; vel = velocity; rad = radius; icon = i;
	}
	/**
	 * @return position of entity
	 */
	public Complex pos() {return pos;}
	/**
	 * @return velocity of entity
	 */
	public Complex vel() {return vel;}
	/**
	 * @param c value to set position to
	 */
	public void setPos(Complex c) {pos = c;}
	/**
	 * @param c value to set velocity to
	 */
	public void setVel(Complex c) {vel = c;}
	/**
	 * places entity back within given boundaries
	 * @param H maximum height boundary (Y value)
	 * @param W maximum width boundary (X value)
	 * @return
	 * true if entity was previously out of boundaries
	 */
	public boolean correctPos(int H, int W) {
		Complex oldPos = pos;
		pos = new Complex(Math.min(W-2*rad, Math.max(rad, pos.real())),Math.min(H-2*rad, Math.max(rad, pos.imag())));
		return !oldPos.equals(pos);
	}
	/**
	 * checks if collision has taken place between this and c
	 * @param c entity to check
	 * @return
	 * if the hitbox of this and c collide
	 */
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
	/**
	 * paints icon at position
	 * @param g
	 * Graphics component to paint on
	 */
	public void repaint(Graphics g) {
		
		g.drawImage(icon, (int)(pos.real()-icon.getWidth(null)/2),(int)(pos.imag()-icon.getHeight(null)/2), null);
		//g.drawOval((int)(pos.real()-rad), (int)(pos.imag()-rad), (int)(2*rad), (int)(2*rad)); //DEBUG
	}
}
