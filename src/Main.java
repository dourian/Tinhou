import java.awt.*;
import java.awt.event.*;
import java.util.Stack;
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
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}
	@Override
	public void mouseEntered(MouseEvent e) {
		System.out.println(e.getX() +" "+ e.getY());

	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
//		System.out.println("esc");
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
//		System.out.println("esc");
	}

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
