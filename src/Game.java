import java.awt.Graphics;
import java.io.IOException;
import java.util.*;
public class Game {
	private ArrayList<Entity> list;
	private Player player;
	private int height, width;
	private long previousCycle;
	Game(int h, int w, boolean mouse) {
		height = h; width = w;
		list = new ArrayList<Entity>();
		try {
			if(mouse) {}
			else player = new PlayerKeyboard(new Complex(h/2, w/2));
		} catch (IOException e) { e.printStackTrace(); }
		list.add(player);
		previousCycle = -1;
	}
	public Player getListener() { return player; }
	public void addEntity(Entity e) { list.add(e); }
	public void cycle() {
		if(previousCycle == -1) previousCycle = System.currentTimeMillis()-1;
		float f = (System.currentTimeMillis()-previousCycle)/1000.0f;
		previousCycle=System.currentTimeMillis();
		for(Entity e: list) {
			e.cycle(f);
			e.correctPos(height, width);
		}
		//TODO collision and stuff
	}
	public void repaint(Graphics g) {
		for(Entity e: list) e.repaint(g);
		//TODO draw background and music visualizer
	}
}
