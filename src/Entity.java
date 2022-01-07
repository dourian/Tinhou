import java.awt.Image;

/**
 * @author max13
 * entity class. stores entity data such as position, size, hit detection information, icon
 */
public class Entity {
	private Complex pos, vel;
	private double rad;
	private Image icon;
	Entity(Complex position, Complex velocity, double radius, Image i) {
		pos = position; vel = velocity; rad = radius; icon = i;
	}
	public Complex pos() {return pos;}
	public Complex vel() {return vel;}
	public void setPos(Complex c) {pos = c;}
	public void setVel(Complex c) {vel = c;}
	public void move() {pos = pos.plus(vel);}
	
	public boolean Collides(Entity c) {return pos.minus(c.pos).abs()<rad+c.rad;}
}
