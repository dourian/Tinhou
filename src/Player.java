import java.awt.Image;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * base abstract player class
 * @author max13
 * contains HP and speed limit
 */
public abstract class Player extends Entity {
	protected int HP, speed;
	/**
	 * constructs player object
	 * @param position initial position of player
	 * @throws IOException if there was an issue reading icon data
	 */
	Player(Complex position) throws IOException {
		super(position, new Complex(0, 0), 5, ImageIO.read(new File("player_v2.png")));
		HP = 1; speed = 300;
	}
	/**
	 * generic cycle
	 * @param f time elapsed since last cycle
	 * @return if player may still exist (HP > 0)
	 */
	public boolean cycle(float f) {
		Complex rvel = new Complex(vel.real(),vel.imag());
		if(rvel.abs()>speed*f) rvel = rvel.div(rvel.abs()).mult(speed*f);
		pos = pos.plus(rvel);
		return HP > 0;
	}
	/**
	 * @return current HP
	 */
	public int getHP() {return HP;}
	/**
	 * hits player
	 * @param val amount of HP to hit for
	 */
	public void hit(int val) {HP -= val;}
}

/**
 * player class that uses keyboard for input
 * @author max13
 */
class PlayerKeyboard extends Player implements KeyListener {
	private boolean W,A,S,D; //whether or not W, A, S, and D are being held
	/**
	 * constructs player
	 * @param position initial position of player
	 * @throws IOException whether or not attempting to get icon has failed
	 */
	PlayerKeyboard(Complex position) throws IOException {
		super(position);W=A=S=D=false;
	}
	public void keyTyped(KeyEvent e) { }
	/**
	 * key events for player input
	 * @param e even to to be processed
	 */
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KeyEvent.VK_W:
			if(!W) {
				vel = vel.plus(new Complex(0, -speed));
				W = true;
			}
			break;
		case KeyEvent.VK_A:
			if(!A) {
				vel = vel.plus(new Complex(-speed, 0));
				A = true;
			}
			break;
		case KeyEvent.VK_S:
			if(!S) {
				vel = vel.plus(new Complex(0, speed));
				S = true;
			}
			break;
		case KeyEvent.VK_D:
			if(!D) {
				vel = vel.plus(new Complex(speed, 0));
				D = true;
			}
			break;
		}
	}
	/**
	 * key events for player input
	 * @param e even to to be processed
	 */
	public void keyReleased(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KeyEvent.VK_W:				
			if(W) {
				vel = vel.minus(new Complex(0, -speed));
				W = false;
			}
			break;
		case KeyEvent.VK_A:
			if(A) {
				vel = vel.minus(new Complex(-speed, 0));
				A = false;
			}
			break;
		case KeyEvent.VK_S:
			if(S) {
				vel = vel.minus(new Complex(0, speed));
				S = false;
			}
			break;
		case KeyEvent.VK_D:
			if(D) {
				vel = vel.minus(new Complex(speed, 0));
				D = false;
			}
			break;
		}
	}
}
/**
 * player class that uses mouse for input
 * @author max13
 */
class PlayerMouse extends Player implements MouseMotionListener {
	private Complex targ; //target position (position of mouse)
	/**
	 * constructs player
	 * @param position initial position of player
	 * @throws IOException throws if error with reading player icon
	 */
	PlayerMouse(Complex position) throws IOException {
		super(position); targ = position;
	}
	/**
	 * generic cycle. gravitations position towards targ
	 * @param f time elapsed since last cycle
	 * @return if player may still exist (HP > 0)
	 */
	public boolean cycle(float f) {
		vel = targ.minus(pos);
		if(vel.abs()>speed*f) vel = vel.div(vel.abs()).mult(speed*f);
		pos = pos.plus(vel);
		return HP > 0;
	}
	/**
	 * mouse event for input
	 * @param e event to be processed
	 */
	public void mouseDragged(MouseEvent e) {
		targ = new Complex(e.getX(),e.getY());
	}
	/**
	 * mouse event for input
	 * @param e event to be processed
	 */
	public void mouseMoved(MouseEvent e) {
		targ = new Complex(e.getX(),e.getY());
	}
}