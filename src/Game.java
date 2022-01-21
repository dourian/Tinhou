import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;
public class Game {
	private Vector<Entity> list;
	boolean updateFlag;
	private Player player;
	private int height, width;
	private long previousCycle;
	private long score;
	private SoundProcessor sp;
	private Image backgroundImage;
	Game(int h, int w, boolean mouse, String file) {
		updateFlag = false; score = 0;
		try {
			backgroundImage = ImageIO.read(new File("background_scaled.png"));
		} catch (IOException e1) { e1.printStackTrace(); }
		height = h; width = w;
		list = new Vector<Entity>();
		try {
			if(mouse) player = new PlayerMouse(new Complex(h/2,w/2));
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
	public void addEntity(Entity e) { list.add(e); score++; }
	public synchronized boolean cycle() {
		if(previousCycle == -1) previousCycle = System.currentTimeMillis()-1;
		float f = (System.currentTimeMillis()-previousCycle)/1000.0f;
		previousCycle=System.currentTimeMillis();
		Vector<Entity> newlist = new Vector<Entity>();
		for(Entity e: list) {
			if(e.cycle(f)) newlist.add(e);
			e.correctPos(height, width);
		}
		list = newlist;
		for(Entity e: list) if(e != player && e.collides(player)) player.hit(1);
		
		//add bullets
		if(updateFlag) {
			float[] cur = sp.fftget(0);
			float[] prv = sp.fftget(1);
			double coef = 2*Math.PI/Math.log(Math.min(cur.length/2,prv.length/2));
			for(int i = 0; i < cur.length/2 && i < prv.length/2; i++) if(cur[i]/(prv[i]+1)>7){
				double angle = Math.log(i)*coef;
				addEntity(new Bullet(new Complex(width/2,height/2),Complex.polar(angle, 200), 1));
			}
			updateFlag = false;
		}
		return newlist.contains(player);
	}
	public synchronized void repaint(Graphics g) {
		g.drawImage(backgroundImage, 0, 0, null);
		for(Entity e: list) e.repaint(g);
		//TODO draw background and music visualizer and ui and stuff
		int window = 4096;
		sp.fftNow(window, 0);
		updateFlag = true;
		if(sp.audioPos()+window < sp.sze()) {
			int pos = sp.audioPos();
			float[] data = sp.fftget(0);
			window = data.length;
			double coef = width/Math.log(window);
			for(int i = 0; i+1 < window; i++) {
				int lg = (int)(Math.log(i)*coef);
				int lgp1 = (int)(Math.log(i+1)*coef);
				g.drawLine(lg, height-(int)(data[i])-50, lgp1, height-(int)(data[i+1])-50);
			}
		}
		g.setColor(Color.WHITE);
		g.drawString(Integer.toString(player.getHP()), 10, 50);
		g.setColor(Color.BLACK);
	}
	public long getScore() {return score;}
}
