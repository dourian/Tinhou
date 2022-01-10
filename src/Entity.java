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
	public void move() {pos = pos.plus(vel);}
	
	public boolean Collides(Entity c) {return pos.minus(c.pos).abs()<rad+c.rad;}
	
	/**
	 * cycle function that engine calls every game tick to update entity
	 * @param f
	 * elapsed time
	 * @return
	 * true if entity is allowed to exist
	 * false if entity should be deleted
	 */
	public abstract boolean cycle(float f);
}