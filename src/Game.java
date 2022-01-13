import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;
public class Game {
	private ArrayList<Entity> list;
	private Player player;
	private int height, width;
	private long previousCycle;
	private SoundProcessor sp;
	private Image backgroundImage;
	Game(int h, int w, boolean mouse, String file) {
		try {
			backgroundImage = ImageIO.read(new File("background_scaled.png"));
		} catch (IOException e1) { e1.printStackTrace(); }
		height = h; width = w;
		list = new ArrayList<Entity>();
		try {
			if(mouse) {}
			else player = new PlayerKeyboard(new Complex(h/2, w/2));
		} catch (IOException e) { e.printStackTrace(); }
		list.add(player);
		previousCycle = -1;
		try {
			sp = new SoundProcessor(file);
		} catch (IOException e) { e.printStackTrace(); }
	}
	public Player getListener() { return player; }
	public void playAudio() {sp.play();}
	public void addEntity(Entity e) { list.add(e); }
	public void cycle() {
		if(previousCycle == -1) previousCycle = System.currentTimeMillis()-1;
		float f = (System.currentTimeMillis()-previousCycle)/1000.0f;
		previousCycle=System.currentTimeMillis();
		ArrayList<Entity> newlist = new ArrayList<Entity>();
		for(Entity e: list) {
			if(e.cycle(f)) newlist.add(e);
			e.correctPos(height, width);
		}
		list = newlist;
		for(Entity e: list) if(e != player && e.collides(player)) player.hit(1);
	}
	public void repaint(Graphics g) {
		g.drawImage(backgroundImage, 0, 0, null);
		for(Entity e: list) e.repaint(g);
		//TODO draw background and music visualizer and ui and stuff
		int window = 4096;
		if(sp.audioPos()+window < sp.sze()) {
			int pos = sp.audioPos();
			float[] data = DFT.fFFT(sp.getsubdata(0, pos, pos+window));
			double coef = width/Math.log(window);
			for(int i = 0; i+1 < window; i++) {
				int lg = (int)(Math.log(i)*coef);
				int lgp1 = (int)(Math.log(i+1)*coef);
				g.drawLine(lg, height-(int)(data[i]/300000*Math.log(i))-50, lgp1, height-(int)(data[i+1]/300000*Math.log(i+1))-50);
			}
		}
	}
}
