import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class Main extends JPanel implements Runnable, MouseListener {
	Game game;
	
	static JPanel panel;
	static JFrame frame;
	Image [] images;
	Graphics g;
	
	
	Main() {
		game = new Game(700, 1000, false, "keshi 2.wav");
		addKeyListener((KeyListener) game.getListener());
		
		images = new Image [1];
		images[0] = Toolkit.getDefaultToolkit().getImage("openingscreen.png");
		
		addMouseListener (this);
		
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(images[0],0, 0, 1000, 700,this);
//		game.repaint(g);
	}
	public static void main(String args[]) {
		frame = new JFrame ();
		frame.setPreferredSize(new Dimension(1000, 700));
		panel = new Main();
		frame.add (panel);
		frame.pack ();
		frame.setVisible (true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		new Thread(panel).start();
	}
	
	public int buttonTracker(int x, int y) {
		if (x>300 && x<700) {
			if (y>325 && y<425) {
				return 1;
			}
			else if (y>450 && y<525) {
				return 2;
			}
			else if (y>550 && y<625) {
				return 3;
			}
		}
		
		return -1;
	}
	
	
	
	@Override
	public void run() {
		playGame();
	}
	
	public void playGame() {
		game.playAudio();
		while(true) {
			game.cycle();
			repaint();
			requestFocus();
		}
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		/*
		 * 1: PLAY
		 * 2: LEADERBOARD
		 * 3: SETTINGS
		 */
		
		if (buttonTracker(e.getX(), e.getY())==1) {
			g.drawRect(0, 0, 1000, 700);
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
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
