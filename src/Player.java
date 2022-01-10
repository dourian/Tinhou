import java.awt.Image;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public abstract class Player extends Entity {
	int HP, speed;
	Player(Complex position) throws IOException {
		super(position, new Complex(0, 0), 10, ImageIO.read(new File("character.png")));
		HP = 10; speed = 10;
	}
	public boolean cycle(float f) {
		Complex rvel = new Complex(vel.real(),vel.imag());
		if(rvel.abs()>speed) rvel = rvel.div(rvel.abs()).mult(speed);
		pos = pos.plus(rvel);
		return HP > 0;
	}
}

class PlayerKeyboard extends Player implements KeyListener {
	PlayerKeyboard(Complex position) throws IOException {
		super(position);
	}
	public void keyTyped(KeyEvent e) { }
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KeyEvent.VK_W:
			vel = vel.plus(new Complex(0, -100));
			break;
		case KeyEvent.VK_A:
			vel = vel.plus(new Complex(-100, 0));
			break;
		case KeyEvent.VK_S:
			vel = vel.plus(new Complex(0, 100));
			break;
		case KeyEvent.VK_D:
			vel = vel.plus(new Complex(100, 0));
			break;
		}
	}
	public void keyReleased(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KeyEvent.VK_W:
			vel = vel.minus(new Complex(0, -100));
			break;
		case KeyEvent.VK_A:
			vel = vel.minus(new Complex(-100, 0));
			break;
		case KeyEvent.VK_S:
			vel = vel.minus(new Complex(0, 100));
			break;
		case KeyEvent.VK_D:
			vel = vel.minus(new Complex(100, 0));
			break;
		}
	}
}