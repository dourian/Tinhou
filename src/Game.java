import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;
public class Game {
	public final int ENTITYLIM = 100;
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
		sweeper.setVel(player.pos().minus(sweeper.pos()).mult(0.04));
		
		//add bullets
		if(updateFlag) {
			float[][] means = sp.bassAnalyze();
			if(means != null) {
				if(means[0][0] > (means[1][0]+means[2][0]+0.85)) {
					addEntity(new Bullet(sweeper.pos(),Complex.polar(sweeper.vel().arg(), 150), 1));
				}
				if(means[0][1] > (means[1][1]+means[2][1]+0.85)) {
					addEntity(new Bullet(sweeper.pos(),Complex.polar(sweeper.vel().arg(), 175), 3));
				}
				if(means[0][2] > means[1][2]+means[2][2]+0.85) {
					addEntity(new Bullet(sweeper.pos(),Complex.polar(sweeper.vel().arg(), 200), 0));
				}
			}
			float cnt[][] = sp.trebleAnalyze();
			if(cnt != null) {
				float frac[][] = new float[cnt.length][2];
				for(int i = 0; i < cnt.length; i++) {
					for(int i2 = 0; i2 < cnt[i].length; i2++) {
						frac[i][0] += cnt[i][i2]*i2; frac[i][1] += cnt[i][i2];
					}
					frac[i][0] /= frac[i][1];
				}
				if(frac[0][0] > (frac[1][0] + frac[2][0])/2 + 0.1) {
					for(int i = 0; i < frac[0][0]/3; i++) addEntity(new Bullet(faucet.pos(), Complex.polar(Math.random()*Math.PI*2, 275), frac[0][0]>1.9?2:4));
				}
			}
			cnt = sp.midAnalyze();
			if(cnt != null) {
				float mx = 0;
				for(int i = 0; i < cnt[0].length; i++) {
					cnt[0][i] += cnt[1][i] + cnt[2][i];
					mx = Math.max(cnt[0][i], mx);
				}
				for(int i = 0; i < cnt[0].length; i++) {
					if(Math.random() < 0.1 && cnt[0][i] > 100)
						addEntity(new Bullet(faucet.pos(), Complex.polar(Math.PI*2*i/12, 300), i%6));
				}
			}
			updateFlag = false;
		}
		return newlist.contains(player);
	}
	public synchronized void repaint(Graphics g) {
		g.drawImage(backgroundImage, 0, 0, null);
		for(Entity e: list) e.repaint(g);
		int window = 4096;
		sp.fftNow(window, 0);
		updateFlag = true;
		if(sp.audioPos()+window < sp.sze()) {
			float[] data = sp.fftget(0); window = data.length/2;
			double coef = width/Math.log(window);
			
			g.setColor(Color.WHITE);
			//draw visualizer
			for(int i = 0; i+1 < window; i++) {
				int lg = (int)(Math.log(i)*coef);
				int lgp1 = (int)(Math.log(i+1)*coef);
				g.drawLine(lg, height-(int)(data[i])-50, lgp1, height-(int)(data[i+1])-50);
			}
			//draw guidelines
			for(int i = 1; i < window; i *= 2) {
				g.drawLine((int)(Math.log(i)*coef), height, (int)(Math.log(i)*coef), height-100);
				g.drawString(Integer.toString(i), (int)(Math.log(i)*coef)-5, height-110);
			}
			g.drawLine(0, height-50, width, height-50);
			g.drawLine(0, height-100, width, height-100);
			//draw analysis
			try {
				float[][] cnt = sp.midAnalyze();
				if(cnt != null) {
					for(int i = 0; i < cnt[0].length; i++) {
						cnt[0][i] += cnt[1][i] + cnt[2][i];
						g.fillRect(0, height-5-5*i, (int)cnt[0][i], 5);
					}
				}
			} catch(NullPointerException e) {}
		}
		
	}
	public long getScore() {return score;}
}
