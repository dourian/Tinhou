import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class Main extends JPanel implements Runnable {
	Game game;
	Main() {
		game = new Game(700, 700, false);
		game.addEntity(new Bullet(new Complex(100, 100), new Complex(10, 10), 1));
		addKeyListener((KeyListener) game.getListener());
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		game.repaint(g);
	}
	public static void main(String args[]) {
		JFrame frame = new JFrame ();
		frame.setPreferredSize(new Dimension(700, 700));
		Main panel = new Main();
		frame.add (panel);
		frame.pack ();
		frame.setVisible (true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		new Thread(panel).start();
	}
	@Override
	public void run() {
		while(true) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			game.cycle();
			repaint();
			requestFocus();
		}
	}
}
