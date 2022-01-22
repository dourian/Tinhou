import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;
public class Game {
	public final int ENTITYLIM = 50;
	private Vector<Entity> list;
	boolean updateFlag;
	private Player player;
	private BlackHole faucet, sweeper;
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
			if(mouse) player = new PlayerMouse(new Complex(w/4,h/4));
			else player = new PlayerKeyboard(new Complex(w/4, h/4));
		} catch (IOException e) { e.printStackTrace(); }
		list.add(player);
		previousCycle = -1;
		try {
			sp = new SoundProcessor(file);
		} catch (IOException e) { e.printStackTrace(); }
		
		try {
			faucet = new BlackHole(new Complex(w/2,h/2));
			sweeper = new BlackHole(new Complex(w/2, h/2));
			list.add(faucet); list.add(sweeper);
		} catch (IOException e) { e.printStackTrace(); }
	}
	public Player getListener() { return player; }
	public void playAudio() {sp.play();}
	public void addEntity(Entity e) { if(list.size() < ENTITYLIM) {list.add(e); score++;} }
	public synchronized boolean cycle() {
		if(previousCycle == -1) previousCycle = System.currentTimeMillis()-1;
		float f = (System.currentTimeMillis()-previousCycle)/1000.0f;
		previousCycle=System.currentTimeMillis();
		Vector<Entity> newlist = new Vector<Entity>();
		for(Entity e: list) {
			if(e.cycle(f)) newlist.add(e);
			boolean corrected = e.correctPos(height, width);
			if(e instanceof Bullet && corrected) newlist.remove(newlist.size()-1);
		}
		list = newlist;
		for(Entity e: list) if(e != player && e.collides(player)) player.hit(1);
		sweeper.setVel(player.pos().minus(sweeper.pos()).mult(0.1));
		
		//add bullets
		if(updateFlag) {
			float[][] data = {sp.fftget(0), sp.fftget(-1), sp.fftget(-2)};
			int len = Math.min(Math.min(data[0].length, data[1].length), Math.min(data[2].length, data[2].length));
			if(len == 4096) {
				len /= 2;
				float[][] means = new float[data.length][2];
				for(int i = 0; i < means.length; i++)
					for(int i2 = 1; i2 < 25; i2++) means[i][i2<16?0:1] += data[i][i2]/25;
				if(means[0][0] > (means[1][0]+means[2][0]+0.75) || means[0][1] > (means[1][1]+means[2][1]+0.75)) {
					addEntity(new Bullet(sweeper.pos(),Complex.polar(sweeper.vel().arg(), 200), 1));
				}
				long cnt[] = new long[data.length];
				for(int i = 0; i < data.length; i++)
					for(int i2 = 220; i2 < len; i2++) cnt[i] += data[i][i2-1]+data[i][i2+1]-2*data[i][i2]>2?1:0;
				if(cnt[0] > (cnt[1]+cnt[2])*4/5 && cnt[0] > 100) {
					System.out.println(cnt[0]);
					System.out.println(cnt[1]);
					System.out.println(cnt[2] + "\n");
					for(int i = 0; i < 6; i++)
						addEntity(new Bullet(faucet.pos(), Complex.polar(Math.random()*Math.PI*2, 150), 4));
				}
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
		
	}
	public long getScore() {return score;}
}
