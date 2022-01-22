import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class BlackHole extends Entity {
	BlackHole(Complex position) throws IOException {
		super(position, new Complex(0, 0), 15, ImageIO.read(new File("blackhole.png")));
	}
	public boolean cycle(float f) {
		pos = pos.plus(vel.mult(f));
		return true;
	}
}
