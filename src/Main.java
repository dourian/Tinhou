import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.BitSet;
import java.util.Comparator;
import java.util.Scanner;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.Vector;
import java.awt.event.KeyEvent;
import javax.swing.*;

public class Main extends JPanel implements Runnable, MouseListener, KeyListener {

	Game game;
	static JPanel panel;
	static JFrame frame;
	Image [] images;
	Image offScreenImage;
	Graphics offScreenBuffer;
	static Stack navigation;
	boolean usingMouse;
	static String fileName;

	final int HOME=0, PLAY = 1, LEADERBOARD = 2, SETTINGSMOUSE = 3, PLAYBUTTON = 4, LEADERBUTTON = 5, SETTINGSBUTTON = 6, SETTINGSKEYBOARD = 7;
	static int gameState = 0;

	Main() throws FileNotFoundException {

		if (fileName==null) {
			fileName="keshi.wav";
		}
		usingMouse = true;
		
		game = new Game(700, 1000, usingMouse, fileName);

		navigation = new Stack <Integer> ();
		navigation.add(HOME);

		images = new Image [20];
		images[HOME] = Toolkit.getDefaultToolkit().getImage("openingscreen.png");
		images[LEADERBOARD] = Toolkit.getDefaultToolkit().getImage("leaderboardscreen.png");
		images[SETTINGSMOUSE] = Toolkit.getDefaultToolkit().getImage("settingsscreenmouse.png");
		images[PLAYBUTTON] = Toolkit.getDefaultToolkit().getImage("playbuttondark.png");
		images[LEADERBUTTON] = Toolkit.getDefaultToolkit().getImage("leaderboardbuttondark.png");
		images[SETTINGSBUTTON] = Toolkit.getDefaultToolkit().getImage("settingsbuttondark.png");
		images[SETTINGSKEYBOARD] = Toolkit.getDefaultToolkit().getImage("settingsscreenkeyboard.png");

		addMouseListener (this);
		frame.addKeyListener (this);

		score.create();
		//		for (score s: score.ts)System.out.println(s);
	}

	public static void clearBoard(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(65, 465, 870, 35);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (offScreenBuffer == null)
		{
			offScreenImage = createImage (1000,  700);
			offScreenBuffer = offScreenImage.getGraphics ();
		}

		if (gameState!=PLAY) {
			//			System.out.println(gameState);
			update(g, gameState);
		}
		else {
			game.repaint(g);
		}
	}

	public static void main(String args[]) throws FileNotFoundException {
		frame = new JFrame ();
		frame.setPreferredSize(new Dimension(1000, 700));
		panel = new Main();
		frame.add (panel);
		frame.pack ();
		frame.setVisible (true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void update(Graphics g, int state) {
		if (offScreenBuffer == null)
		{
			offScreenImage = createImage (1000,  700);
			offScreenBuffer = offScreenImage.getGraphics ();
		}

		offScreenBuffer.clearRect (0, 0, 1000, 700);
		offScreenBuffer.drawImage(images[state],0, 0, 1000, 700,this);
		g.drawImage(offScreenImage,0,0,this);

		if (state==LEADERBOARD) displayLeaderboard(g);
		else if (state==SETTINGSMOUSE || state==SETTINGSKEYBOARD) {
			clearBoard(g);
			g.setColor(Color.black);
			g.drawString(fileName, 70, 485);
		}
	}

	public void displayLeaderboard(Graphics g) {
		int y = 200;
		g.setFont(new Font("Courier", Font.PLAIN, 20));
		g.drawString(String.format("%15s%25s%25s", "Name", "Score", "Date"), 60, 175);
		for (score s : score.ts) {
			g.drawString(String.format("%15s%25d%25s", s.getName(),s.getScore(),s.getDate()), 60, y);
			y+=25;
		}
	}

	public int buttonTracker(int x, int y) {
		if (gameState == HOME) {
			if (x>300 && x<700) {
				if (y>325 && y<425) {
					return PLAY;
				}
				else if (y>450 && y<525) {
					return LEADERBOARD;
				}
				else if (y>550 && y<625) {
					if (usingMouse)return SETTINGSMOUSE;
					return SETTINGSKEYBOARD;
				}
			}
		}
		else if (gameState == SETTINGSMOUSE || gameState==SETTINGSKEYBOARD) {
			if (y>265 && y<362) {
				if (x>205 && x<500) {
					usingMouse = true;
					return SETTINGSMOUSE;
				}
				else if (x>500 && x<795) {
					usingMouse = false;
					return SETTINGSKEYBOARD;
				}
			}
		}
		return -1;
	}

	@Override
	public void run() {
		playGame();
	}

	public void playGame() {
		if(usingMouse) addMouseMotionListener((MouseMotionListener) game.getListener());
		else addKeyListener((KeyListener) game.getListener());
		game.playAudio();
		requestFocus();
		while(true) {
			game.cycle();
			repaint();
		}
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		if (buttonTracker(e.getX(), e.getY())==PLAY) {
			gameState = PLAY;
			navigation.push(PLAY);
			repaint();
			new Thread((Runnable) panel).start();
		}
		else if (buttonTracker(e.getX(), e.getY())==LEADERBOARD) {
			gameState = LEADERBOARD;
			navigation.push(LEADERBOARD);
			repaint();
		}
		else if (buttonTracker(e.getX(), e.getY())==SETTINGSMOUSE) {
			if (gameState!=SETTINGSMOUSE) {
				gameState = SETTINGSMOUSE;
				navigation.push(SETTINGSMOUSE);
				repaint();
			}
		}
		else if (buttonTracker(e.getX(), e.getY())==SETTINGSKEYBOARD) {
			if (gameState!=SETTINGSKEYBOARD) {
				gameState = SETTINGSKEYBOARD;
				navigation.push(SETTINGSKEYBOARD);
				repaint();
			}
		}
	}

	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mouseMoved(MouseEvent e) { }
	public void keyTyped(KeyEvent e) {
		if (gameState == SETTINGSMOUSE || gameState ==SETTINGSKEYBOARD) {
			if(e.getKeyChar() != KeyEvent.CHAR_UNDEFINED) {
				if (e.getKeyChar()==KeyEvent.VK_BACK_SPACE && fileName.length() > 0)fileName = fileName.substring(0, fileName.length()-1);
				else if (e.getKeyChar()==KeyEvent.VK_ENTER) {
					game = new Game(700, 1000, usingMouse, fileName);
				}
				else fileName += e.getKeyChar();
			}
			update(getGraphics());
		}
	}
	public void keyPressed(KeyEvent e) { }

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode()==KeyEvent.VK_ESCAPE) {
			while (navigation.size()> 1) {
				navigation.pop();
				gameState = (int) navigation.peek();
				repaint();
			}
		}
	}
}

class score implements Comparable {

	public static TreeSet <score> ts = new TreeSet();
	private static String filename="leaderboard.txt";

	private String name;
	private String date;
	private int scorevalue;

	score(String inname, int inscore, String indate) throws FileNotFoundException{
		name = inname;
		scorevalue = inscore;
		date = indate;
	}

	public static void addentry(String inname, int inscore, String indate) throws FileNotFoundException {
		ts.add(new score (inname, inscore, indate));
		refreshText();
		create();
	}
	public static void create() throws FileNotFoundException {
		Scanner in = new Scanner (new File("leaderboard.txt"));
		score.ts.clear();
		int count = 1;
		while (in.hasNext() && count<=10) {
			StringTokenizer tp = new StringTokenizer(in.nextLine(), "\t");
			ts.add(new score(tp.nextToken(),Integer.parseInt(tp.nextToken()),tp.nextToken()));
			count++;
		}
		in.close();
		refreshText();
	}
	public static void refreshText () throws FileNotFoundException {
		PrintWriter out = new PrintWriter("leaderboard.txt");
		int count=1;
		for (score temp : ts) {
			if (count>10)break;
			out.println(temp);
			count++;
		}
		out.close();
	}
	public String toString() {
		return String.format("%s\t\t%d\t\t%s", name, scorevalue, date);
	}
	@Override
	public int compareTo(Object o) {
		score myScore = (score)o;

		if (myScore.scorevalue - this.scorevalue==0) {
			return myScore.name.compareTo(this.name);
		}
		return myScore.scorevalue - this.scorevalue;
	}

	public String getName() {
		return name;
	}
	public int getScore() {
		return scorevalue;
	}
	public String getDate() {
		return date;
	}

}



class CentralListener implements MouseMotionListener, MouseListener, KeyListener, Runnable {

	public final byte PRESS_EVENT = 1, RELEASE_EVENT = 0, NULL_EVENT = -1;

	private BitSet keyDown;			//true if key is held at given time
	private BitSet keyPressed;		//true if key is held while previously key was unheld at given time
	private BitSet mouseDown;		//true if mouse is held at given time
	private BitSet mousePressed;	//true if mouse is held while previously mouse was unheld at given time
	private int mouseX, mouseY;		//x and y position of cursor

	private JComponent jcomp;		//component to get info from

	Vector<EventClass> buffer;		//buffer (vectors are threadsafe)

	CentralListener(JComponent comp) {

		keyDown = new BitSet(1<<16);
		keyPressed = new BitSet(1<<16);
		mouseDown = new BitSet(MouseInfo.getNumberOfButtons()+1);
		mousePressed = new BitSet(MouseInfo.getNumberOfButtons()+1);

		jcomp = comp;
		jcomp.addMouseMotionListener(this);
		jcomp.addMouseListener(this);
		jcomp.addKeyListener(this);

		buffer = new Vector<EventClass>();

		new Thread(this).start();
	}

	//set keys
	public void keyTyped(KeyEvent e) { }
	public void keyPressed(KeyEvent e) { buffer.add(new EventClass(e, PRESS_EVENT)); }
	public void keyReleased(KeyEvent e) { buffer.add(new EventClass(e, PRESS_EVENT)); }

	//get keys
	public boolean isHeld(int keyCode) { return keyDown.get(keyCode); }
	public boolean isPressed(int keyCode) { return keyPressed.get(keyCode); }

	//set mouse
	public void mouseClicked(MouseEvent e) { }
	public void mousePressed(MouseEvent e) { buffer.add(new EventClass(e, PRESS_EVENT)); }
	public void mouseReleased(MouseEvent e) { buffer.add(new EventClass(e, RELEASE_EVENT)); }
	public void mouseEntered(MouseEvent e) { buffer.add(new EventClass(e, NULL_EVENT)); }
	public void mouseExited(MouseEvent e) { buffer.add(new EventClass(e, NULL_EVENT)); }
	public void mouseDragged(MouseEvent e) { buffer.add(new EventClass(e, NULL_EVENT)); }
	public void mouseMoved(MouseEvent e) { buffer.add(new EventClass(e, NULL_EVENT)); }

	//get mouse info
	public boolean mouseIsHeld(int button) {return mouseDown.get(button);}
	public boolean mouseIsPressed(int button) {return mousePressed.get(button);}
	public int getX() {return mouseX;}
	public int getY() {return mouseY;}

	public void run() {
		while(true) {
			keyPressed.clear();
			mousePressed.clear();
			for(EventClass e: buffer) {
				if(e.event instanceof KeyEvent) {
					KeyEvent event = (KeyEvent)(e.event);
					keyPressed.set(event.getKeyCode(), e.type==1?true:false);
					keyDown.set(event.getKeyCode(), e.type==1?true:false);
				}
				if(e.event instanceof MouseEvent) {
					MouseEvent event = (MouseEvent)(e.event);
					if(e.type != NULL_EVENT) {
						mousePressed.set(event.getButton(), e.type==1?true:false);
						mouseDown.set(event.getButton(), e.type==1?true:false);
					}
					mouseX = event.getX();
					mouseY = event.getY();
				}
			}
			buffer.clear();
			try {
				Thread.sleep(20);
			} catch (InterruptedException e1) { e1.printStackTrace(); }
		}
	}

	private class EventClass {
		InputEvent event;
		byte type;
		EventClass(InputEvent e, byte t) {event = e; type = t;}
	}

}