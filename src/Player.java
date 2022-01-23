import java.awt.Image;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public abstract class Player extends Entity {
	protected int HP, speed;
	Player(Complex position) throws IOException {
		super(position, new Complex(0, 0), 5, ImageIO.read(new File("player_v2.png")));
		HP = 1; speed = 300;
	}
	public boolean cycle(float f) {
		Complex rvel = new Complex(vel.real(),vel.imag());
		if(rvel.abs()>speed*f) rvel = rvel.div(rvel.abs()).mult(speed*f);
		pos = pos.plus(rvel);
		return HP > 0;
	}
	public int getHP() {return HP;}
	public void hit(int val) {HP -= val;}
}

class PlayerKeyboard extends Player implements KeyListener {
	private boolean W,A,S,D;
	PlayerKeyboard(Complex position) throws IOException {
		super(position);W=A=S=D=false;
	}
	public void keyTyped(KeyEvent e) { }
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

class PlayerMouse extends Player implements MouseMotionListener {
	private Complex targ;
	PlayerMouse(Complex position) throws IOException {
		super(position); targ = position;
	}
	public boolean cycle(float f) {
		vel = targ.minus(pos);
		if(vel.abs()>speed*f) vel = vel.div(vel.abs()).mult(speed*f);
		pos = pos.plus(vel);
		return HP > 0;
	}
	public void mouseDragged(MouseEvent e) { }
	public void mouseMoved(MouseEvent e) {
		targ = new Complex(e.getX(),e.getY());
	}
}