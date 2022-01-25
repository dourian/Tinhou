import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;
/**
 * @author Dorian, maxwell
 * Jan 24, 2022
 * game class that contains all game logic
 */
public class Game {
	public final int ENTITYLIM = 150;
	private Vector<Entity> list;		//list of entities
	boolean updateFlag;					//whether or not fft has been updated since last cycle
	private Player player;				//player entity
	private BlackHole faucet, sweeper;	//bullet sources - faucet is main source, sweeper follows and targets player to keep player moving
	private double faucetarg;			//faucet spewing angle
	private int height, width;			//height and width boundaries of game
	private long previousCycle;			//time of previous cycle
	private long score;					//current score
	private SoundProcessor sp;			//sound processing structure
	private Image backgroundImage;		//background image
	private Image winscreen, losescreen;//win/lose
	
	/**
	 * constructs game object
	 * @param h height boundary
	 * @param w width boundary
	 * @param mouse whether or not to use a mouse as input
	 * @param file sound file to play from
	 */
	Game(int h, int w, boolean mouse, String file) {
		updateFlag = false; score = 0;
		try {
			backgroundImage = ImageIO.read(new File("resources/background_scaled_1600_900.png"));
			
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
			faucet = new BlackHole(new Complex(w/2,h/2)); faucetarg = 0;
			sweeper = new BlackHole(new Complex(w/2, h/2));
		} catch (IOException e) { e.printStackTrace(); }
		
	}
	/**
	 * @return player
	 */
	public Player getListener() { return player; }
	/**
	 * starts audio
	 */
	public void playAudio() {sp.play();}
	/**
	 * stops audio
	 */
	public void stopAudio() {sp.stop();}
	/**
	 * adds entity to list.
	 * entity will not be added if entity limit has been reached. 
	 * @param e entity to be added
	 */
	public void addEntity(Entity e) { if(list.size() < ENTITYLIM) {list.add(e); score++;} }
	/**
	 * main game logic
	 * @return
	 * if game is still going
	 */
	public synchronized boolean cycle() {
		if(!list.contains(player) && player.getHP() > 0) list.add(player); 	//bandaid for small bug encountered
		if(sp.audioPos() >= sp.sze() || !list.contains(player)) {			//game ended, either by winning or losing
			return false;
		}
		if(previousCycle == -1) previousCycle = System.currentTimeMillis()-1;
		float f = (System.currentTimeMillis()-previousCycle)/1000.0f;
		previousCycle=System.currentTimeMillis();
		Vector<Entity> newlist = new Vector<Entity>();
		for(Entity e: list) {												//cycle every entity
			boolean flag = e.cycle(f);
			flag = flag && !(e instanceof Bullet && e.correctPos(height, width));	//if bullet has gone out of boundaries, remove it
			if(flag) newlist.add(e);
		}
		sweeper.cycle(f); faucetarg += f;
		list = newlist;
		for(Entity e: list) if(e != player && e.collides(player)) player.hit(1);
		sweeper.setVel(player.pos().minus(sweeper.pos()).mult(0.04));
		
		//add bullets
		if(updateFlag) {
			//interpret bassy audio
			float[][] means = sp.bassAnalyze();
			if(means != null) {
				if(means[0][0] > (means[1][0]+means[2][0]+0.85)) {
					addEntity(new Bullet(sweeper.pos(),Complex.polar(sweeper.vel().arg(), 200), 1));
				}
				if(means[0][1] > (means[1][1]+means[2][1]+0.85)) {
					addEntity(new Bullet(sweeper.pos(),Complex.polar(sweeper.vel().arg(), 225), 3));
				}
				if(means[0][2] > means[1][2]+means[2][2]+0.85) {
					addEntity(new Bullet(sweeper.pos(),Complex.polar(sweeper.vel().arg(), 250), 0));
				}
			}
			//interpret high pitched audio
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
					for(int i = 0; i < frac[0][0]/5; i++) addEntity(new Bullet(faucet.pos(), Complex.polar(Math.random()*Math.PI*2, 275), frac[0][0]>1.75?2:4));
				}
			}
			//interpret audio in between
			float[] hist = sp.fftget(1), cur = sp.fftget(0);
			if(hist != null) {
				for(int i = 1; i < cur.length; i++) {
					if(Math.random() < 0.07 && cur[i]-hist[i]>30) {
						double note = Math.log(0.658507740825688*i)/0.05776226504666215;
						addEntity(new Bullet(faucet.pos(), Complex.polar(faucetarg+note, 300), (int)(7*note)%6));
					}
				}
			}
			updateFlag = false;
		}
		return true;
	}
	/**
	 * repaints the entire game
	 * @param g Graphics used to paint
	 */
	public synchronized void repaint(Graphics g) {
		if(!list.contains(player)) {
			try {
				if(Math.random() < 0.99) {
					g.drawImage(ImageIO.read(new File("resources/losescreen_v2.png")), 0, 0, null);
					g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 50));
					g.setColor(Color.WHITE);
					g.drawString(Long.toString(getScore()), 511, 367);
				}
				else {
					g.drawImage(ImageIO.read(new File("resources/losescreen.png")), 0, 0, null);
				}
			} catch (IOException e) {e.printStackTrace();}
			return;
		}
		int window = 4096;
		if(sp.audioPos()+window < sp.sze()) {
			g.drawImage(backgroundImage, 0, 0, null);
			sp.fftNow(window, 0);
			updateFlag = true;
			float[] data = sp.fftget(0); window = data.length/2;
			double coef = width/Math.log(window);
			
			g.setColor(Color.GRAY);
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
				float[] tmpdata = sp.fftget(1);
				g.setColor(Color.DARK_GRAY);
				for(int i = 0; i+1 < window; i++) {
					int lg = (int)(Math.log(i)*coef);
					int lgp1 = (int)(Math.log(i+1)*coef);
					g.drawLine(lg, height-(int)(data[i]-tmpdata[i])-50, lgp1, height-(int)(data[i+1]-tmpdata[i+1])-50);
				}
			} catch(NullPointerException e) {}
			{
				BufferedImage bim = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
				Graphics g2 = bim.getGraphics();
				for(Entity e: list) e.repaint(g2);
				g.drawImage(bim, 0, 0, null);
			}
			faucet.repaint(g);
			sweeper.repaint(g);
		}
		else {
			try {
				if(Math.random() < 0.99) {
					g.drawImage(ImageIO.read(new File("resources/winscreen_v2.png")), 0, 0, null);
					g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 50));
					g.setColor(Color.WHITE);
					g.drawString(Long.toString(getScore()), 511, 367);
				}
				else {
					g.drawImage(ImageIO.read(new File("resources/winscreen.png")), 0, 0, null);
				}
			} catch (IOException e) {e.printStackTrace();}
		}
	}
	public long getScore() {return score;}
	public boolean isWin() {return player.getHP() > 0;}
}
