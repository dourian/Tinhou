import java.awt.Image;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public abstract class Player extends Entity {
	private int HP, speed;
	Player(Complex position) throws IOException {
		super(position, new Complex(0, 0), 10, ImageIO.read(new File("character.png")));
		HP = 10; speed = 300;
	}
	public boolean cycle(float f) {
		Complex rvel = new Complex(vel.real(),vel.imag());
		if(rvel.abs()>speed*f) rvel = rvel.div(rvel.abs()).mult(speed*f);
		pos = pos.plus(rvel);
		return HP > 0;
	}
	public int getHP() {return HP;}
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
				vel = vel.plus(new Complex(0, -100));
				W = true;
			}
			break;
		case KeyEvent.VK_A:
			if(!A) {
				vel = vel.plus(new Complex(-100, 0));
				A = true;
			}
			break;
		case KeyEvent.VK_S:
			if(!S) {
				vel = vel.plus(new Complex(0, 100));
				S = true;
			}
			break;
		case KeyEvent.VK_D:
			if(!D) {
				vel = vel.plus(new Complex(100, 0));
				D = true;
			}
			break;
		}
	}
	public void keyReleased(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KeyEvent.VK_W:
			if(W) {
				vel = vel.minus(new Complex(0, -100));
				W = false;
			}
			break;
		case KeyEvent.VK_A:
			if(A) {
				vel = vel.minus(new Complex(-100, 0));
				A = false;
			}
			break;
		case KeyEvent.VK_S:
			if(S) {
				vel = vel.minus(new Complex(0, 100));
				S = false;
			}
			break;
		case KeyEvent.VK_D:
			if(D) {
				vel = vel.minus(new Complex(100, 0));
				D = false;
			}
			break;
		}
	}
}