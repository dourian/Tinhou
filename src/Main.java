import java.awt.*;
import java.awt.event.*;
import java.util.BitSet;
import java.util.Stack;
import java.util.Vector;

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

	final int HOME=0, PLAY = 1, LEADERBOARD = 2, SETTINGS = 3, PLAYBUTTON = 4, LEADERBUTTON = 5, SETTINGSBUTTON = 6;
	static int gameState = 0;

	Main() {
		usingMouse = true;
		game = new Game(700, 1000, usingMouse, "keshi.wav");
		
		navigation = new Stack <Integer> ();
		navigation.add(HOME);

		images = new Image [10];
		images[HOME] = Toolkit.getDefaultToolkit().getImage("openingscreen.png");
		images[LEADERBOARD] = Toolkit.getDefaultToolkit().getImage("leaderboardscreen.png");
		images[SETTINGS] = Toolkit.getDefaultToolkit().getImage("settingsscreen.png");
		images[PLAYBUTTON] = Toolkit.getDefaultToolkit().getImage("playbuttondark.png");
		images[LEADERBUTTON] = Toolkit.getDefaultToolkit().getImage("leaderboardbuttondark.png");
		images[SETTINGSBUTTON] = Toolkit.getDefaultToolkit().getImage("settingsbuttondark.png");
		
		addMouseListener (this);
		frame.addKeyListener (this);
	}

	public void clearBoard(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 1000, 700);
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

	public static void main(String args[]) {
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
//		System.out.println(state);
		offScreenBuffer.clearRect (0, 0, 1920, 1080);
		offScreenBuffer.drawImage(images[state],0, 0, 1000, 700,this);
		g.drawImage(offScreenImage,0,0,this);
	}

	public int buttonTracker(int x, int y) {
		if (x>300 && x<700) {
			if (y>325 && y<425) {
				return PLAY;
			}
			else if (y>450 && y<525) {
				return LEADERBOARD;
			}
			else if (y>550 && y<625) {
				return SETTINGS;
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
		else if (buttonTracker(e.getX(), e.getY())==SETTINGS) {
			gameState = SETTINGS;
			navigation.push(SETTINGS);
			repaint();
		}

	}
	
	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mouseMoved(MouseEvent e) { }
	public void keyTyped(KeyEvent e) { }
	public void keyPressed(KeyEvent e) { }

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
//		System.out.println("esc");
		if (e.getKeyCode()==KeyEvent.VK_ESCAPE) {
			if (navigation.size()> 1 && gameState!=PLAY) {
				navigation.pop();
				gameState = (int) navigation.peek();
				repaint();
			}
		}
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