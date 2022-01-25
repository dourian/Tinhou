import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * @author Dorian, maxwell
 * Jan 24, 2022
 * blackhole object. however, instead of gravitating objects, it spews them out. kinda like the opposite of a blackhole. oops. 
 */

public class BlackHole extends Entity {
	/**
	 * initializes black hole
	 * @param position initial position
	 * @throws IOException throws if error with reading icon
	 */
	BlackHole(Complex position) throws IOException {
		super(position, new Complex(0, 0), 15, null);
		icon = ImageIO.read(getClass().getResourceAsStream("resources/blackhole.png"));
	}
	/**
	 * generic cycle from entity superclass. not much to say. ooh, it advances the position by velocity!!! velocity has to be multiplied by f first otherwise the cycling is inconsistent!!!
	 * @param f time elapsed since last cycle
	 * @return always true.
	 */
	public boolean cycle(float f) {
		pos = pos.plus(vel.mult(f));
		return true;
	}
}
