import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * @author Dorian, maxwell
 * Jan 24, 2022
 * Main class for program. Handles scoring, page navigation, file selection, input selection.
 */

public class Main extends JPanel implements Runnable, MouseListener, KeyListener, MouseMotionListener {

	//Initialize variables
	Game game;
	static JPanel panel;
	static JFrame frame;
	Image [] images;
	Image offScreenImage;
	Graphics offScreenBuffer;
	static Stack navigation;
	boolean usingMouse;
	static String fileName;
	static int field = 1;
	static String name = "default";
	static String date = "01242022";
	Thread gamethread;
	static int paintedbutton = 0;

	final int HOME=0, PLAY = 1, LEADERBOARD = 2, SETTINGSMOUSE = 3, PLAYBUTTON = 4, LEADERBUTTON = 5, SETTINGSBUTTON = 6, SETTINGSKEYBOARD = 7, NAMEANDDATE = 8;
	static int gameState = 0;

	//Main method
	Main() throws FileNotFoundException {

		//Sets a defult file name
		if (fileName==null) {
			fileName="keshi.wav";
		}
		
		//Defalt input is mouse
		usingMouse = true;

		//Page navigation system
		navigation = new Stack <Integer> ();
		navigation.add(HOME);

		//Grabs all required images
		images = new Image [10];
		try {
			images[HOME] = ImageIO.read(getClass().getResourceAsStream("resources/opening screen.png"));
			images[LEADERBOARD] = ImageIO.read(getClass().getResourceAsStream("resources/leaderboardscreen.png"));
			images[SETTINGSMOUSE] = ImageIO.read(getClass().getResourceAsStream("resources/settingsscreenmouse.png"));
			images[PLAYBUTTON] = ImageIO.read(getClass().getResourceAsStream("resources/playbuttondark.png"));
			images[LEADERBUTTON] = ImageIO.read(getClass().getResourceAsStream("resources/leaderboardbuttondark.png"));
			images[SETTINGSBUTTON] = ImageIO.read(getClass().getResourceAsStream("resources/settingsbuttondark.png"));
			images[SETTINGSKEYBOARD] = ImageIO.read(getClass().getResourceAsStream("resources/settingsscreenkeyboard.png"));
			images[NAMEANDDATE] = ImageIO.read(getClass().getResourceAsStream("resources/highscore.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Adds mouse, motion, and keylisteners
		addMouseListener (this);
		frame.addKeyListener (this);
		addMouseMotionListener(this);
		
		//Initializes the score
		score.create();
	}

	/**
	 * Purpose: Clears a specified location
	 * @param  	g Graphics used to paint
	 * @param 	x Integer of x coordinate
	 * @param 	y Integer of y coordinate
	 * @param 	w Integer of width
	 * @param 	h Integer of height
	 * @return 	void
	 */
	public static void clearBoard(Graphics g, int x, int y, int w, int h) {
		g.setColor(Color.WHITE);
		g.fillRect(x, y, w, h);
	}

	/**
	 * Purpose: Paint component that will update screens based on game state
	 * @param 	g Graphics used to paint
	 * @return 	void
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (offScreenBuffer == null)
		{
			offScreenImage = createImage (1000,  700);
			offScreenBuffer = offScreenImage.getGraphics ();
		}

		if (gameState!=PLAY) {
			update(g, gameState);
		}
		else {
			if(game != null) game.repaint(g);
		}
	}

	/**
	 * Purpose:	Sets up the Jframe and Jpanel
	 */
	public static void main(String args[]) throws FileNotFoundException {
		frame = new JFrame ();
		frame.setPreferredSize(new Dimension(1014, 738));
		panel = new Main();
		frame.add (panel);
		frame.pack ();
		frame.setVisible (true);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * Purpose: Updates the the graphics displayed
	 * @param  	g Graphics used to paint
	 * @param 	state Integer representing the current game state
	 * @return 	void
	 */
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
			clearBoard(g, 65, 465, 870, 35);
			g.setColor(Color.black);
			g.drawString(fileName, 70, 485);
		}
		else if (state==NAMEANDDATE && field==1) {
			clearBoard(g,65, 275, 870, 35);
			g.setColor(Color.black);
			g.drawString(name, 70, 295);
			g.drawString(date, 70, 485);
		}
		else if (state==NAMEANDDATE && field==2) {
			clearBoard(g, 65, 465, 870, 35);
			g.setColor(Color.black);
			g.drawString(name, 70, 295);
			g.drawString(date, 70, 485);
		}

	}
	
	/**
	 * Purpose: Updates the buttons on the screen when hovered on
	 * @param  	g Graphics used to paint
	 * @param 	state Integer representing current game state
	 * @return 	void
	 */
	public void updateButton(Graphics g, int state) {
		if (state==PLAYBUTTON) {
			g.drawImage(images[PLAYBUTTON], 0,0,1000,700, this);
		}
		else if (state==LEADERBUTTON) {
			g.drawImage(images[LEADERBUTTON], 0,0,1000,700, this);
		}
		else if (state==SETTINGSBUTTON) {
			g.drawImage(images[SETTINGSBUTTON], 0,0,1000,700, this);
		}
		else {
			update(g,HOME);
		}

	}

	/**
	 * Purpose:	Displays the leaderboard rankings
	 * @param  	g Graphics used to paint
	 * @return 	void
	 */
	public void displayLeaderboard(Graphics g) {
		int y = 200;
		g.setFont(new Font("Courier", Font.PLAIN, 20));
		g.drawString(String.format("%-25s%-15s%-25s", "Name", "Score", "Date"), 175, 175);
		for (score s : score.ts) {
			g.drawString(String.format("%-25s%-15d%-25s", s.getName().trim(),s.getScore(),s.getDate()), 175, y);
			y+=25;
		}
	}

	/**
	 * Purpose: Tracks the button clicked
	 * @param  	x Integer representing x coordinate
	 * @param 	y Integer representing y coordinate
	 * @return 	Integer representing button clicked
	 */
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

	/*
	 * Starts the thread, and plays the game
	 */
	@Override
	public void run(){
		removeMouseListener(this);
		frame.removeKeyListener(this);
		removeKeyListener(this);
		frame.setPreferredSize(new Dimension(1614, 938));
		frame.pack();
		game = new Game(900, 1600, usingMouse, fileName);
		if(usingMouse) addMouseMotionListener((MouseMotionListener) game.getListener());
		else addKeyListener((KeyListener) game.getListener());
		game.playAudio();
		requestFocus();
		int state = 0;
		while(game.cycle()) {
			repaint();
		}
		game.stopAudio();
		frame.setPreferredSize(new Dimension(1014, 738));
		frame.pack();
		if(usingMouse) removeMouseMotionListener((MouseMotionListener) game.getListener());
		else removeKeyListener((KeyListener) game.getListener());
		addMouseListener(this);
		addKeyListener(this);
		addMouseMotionListener(this);
	}

	/**
	 * Purpose:	Handle mouse clicks
	 * @param  	e MouseEvent
	 * @return 	void
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		if (buttonTracker(e.getX(), e.getY())==PLAY) {
			gameState = PLAY;
			navigation.push(PLAY);
			repaint();
			gamethread = new Thread((Runnable) panel);
			gamethread.start();
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

	/**
	 * Purpose:	Handles mouse movement
	 * @param  	e MouseEvent
	 * @return 	void
	 */
	public void mouseMoved(MouseEvent e) {
		if (gameState==HOME) {
			if (buttonTracker(e.getX(), e.getY())==PLAY && paintedbutton!=PLAY) {
				paintedbutton=PLAY;
				updateButton(getGraphics(), PLAYBUTTON);
			}
			else if (buttonTracker(e.getX(), e.getY())==LEADERBOARD && paintedbutton !=LEADERBUTTON) {
				paintedbutton=LEADERBUTTON;
				updateButton(getGraphics(), LEADERBUTTON);
			}
			else if ((buttonTracker(e.getX(), e.getY())==SETTINGSMOUSE || buttonTracker(e.getX(), e.getY())==SETTINGSKEYBOARD) && paintedbutton!=SETTINGSKEYBOARD) {
				paintedbutton = SETTINGSKEYBOARD;
				updateButton(getGraphics(), SETTINGSBUTTON);
			}
			else if (buttonTracker(e.getX(), e.getY())==-1){
				paintedbutton=HOME;
				updateButton(getGraphics(), HOME);
			}
		}
	}
	
	/**
	 * Purpose: Handles key strokes
	 * @param  	e KeyEvent
	 * @return 	void
	 */
	public void keyTyped(KeyEvent e) {
		
		//Entering file name
		if (gameState == SETTINGSMOUSE || gameState ==SETTINGSKEYBOARD) {
			if(e.getKeyChar() != KeyEvent.CHAR_UNDEFINED && e.getKeyChar() != KeyEvent.VK_ESCAPE) {
				if (e.getKeyChar()==KeyEvent.VK_BACK_SPACE && fileName.length() > 0)fileName = fileName.substring(0, fileName.length()-1);
				else if (e.getKeyChar()==KeyEvent.VK_ENTER) {
					try {
						SoundProcessor sp = new SoundProcessor(fileName);
					} catch (IOException ee) { fileName = "keshi.wav";}
					game = new Game(700, 1000, usingMouse, fileName);
				}
				else fileName += e.getKeyChar();
			}
			update(getGraphics());
		}
		
		//Entering name + date for highscore
		else if (gameState == NAMEANDDATE) {
			if(e.getKeyChar() != KeyEvent.CHAR_UNDEFINED && e.getKeyChar() != KeyEvent.VK_ESCAPE && field==1) {
				if (e.getKeyChar()==KeyEvent.VK_BACK_SPACE && name.length() > 0)name = name.substring(0, name.length()-1);
				else if (e.getKeyChar()==KeyEvent.VK_ENTER) {
					field=2;
				}
				else name += e.getKeyChar();
			}
			else if(e.getKeyChar() != KeyEvent.CHAR_UNDEFINED && e.getKeyChar() != KeyEvent.VK_ESCAPE && field==2) {
				if (e.getKeyChar()==KeyEvent.VK_BACK_SPACE && date.length() > 0)date = date.substring(0, date.length()-1);
				else if (e.getKeyChar()==KeyEvent.VK_ENTER) {
					field=1;
					while (navigation.size()> 1) {
						navigation.pop();
						gameState = (int) navigation.peek();
						repaint();
					}
					try {
						score.addentry(name, (int)game.getScore(), date);
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				else date += e.getKeyChar();
			}
			update(getGraphics());
		}
	}

	/**
	 * Purpose: Handles the release of keys
	 * @param  	e KeyEvent
	 * @return 	void
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		
		//Goes back to home screen
		if (e.getKeyCode()==KeyEvent.VK_ESCAPE && gameState!=PLAY && gameState!=NAMEANDDATE) {
			while (navigation.size()> 1) {
				navigation.pop();
				gameState = (int) navigation.peek();
				repaint();
			}
		}
		
		//Goes to highscore after game is won or lost
		else if (e.getKeyCode()==KeyEvent.VK_ESCAPE && gameState == PLAY) {
			while (navigation.size()>1) {
				navigation.pop();
			}
			navigation.push(NAMEANDDATE);
			gameState = NAMEANDDATE;
			repaint();
		}
	}

	//Unused methods
	public void keyPressed(KeyEvent e) { }
	public void mouseDragged(MouseEvent e) { }
	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
}


class score implements Comparable {
	
	/*
	 *	Class for a score object
	 *	Stores all the information needed for the highscore
	 */

	//Class variables
	public static TreeSet <score> ts = new TreeSet();
	private static String filename="leaderboard.txt";
	
	//Instance variables
	private String name;
	private String date;
	private int scorevalue;

	//Constructor
	score(String inname, int inscore, String indate) throws FileNotFoundException{
		name = inname;
		scorevalue = inscore;
		date = indate;
	}

	//Class method to add entries
	public static void addentry(String inname, int inscore, String indate) throws FileNotFoundException {
		ts.add(new score (inname, inscore, indate));
		refreshText();
		create();
	}
	
	//Class method to create the scores
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
	
	//Class method to refresh text
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
	
	//toString method
	public String toString() {
		return String.format("%s\t\t%d\t\t%s", name, scorevalue, date);
	}
	
	@Override
	//CompareTo for sorting
	public int compareTo(Object o) {
		score myScore = (score)o;

		if (myScore.scorevalue - this.scorevalue==0) {
			return myScore.name.compareTo(this.name);
		}
		return myScore.scorevalue - this.scorevalue;
	}

	//Returns name
	public String getName() {
		return name;
	}
	
	//Returns score
	public int getScore() {
		return scorevalue;
	}
	
	//Returns date
	public String getDate() {
		return date;
	}

}