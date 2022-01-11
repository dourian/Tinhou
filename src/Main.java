import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class Main extends JPanel implements Runnable {
	Game game;
	Main() {
		game = new Game(700, 1000, false, "sun.wav");
		addKeyListener((KeyListener) game.getListener());
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		game.repaint(g);
	}
	public static void main(String args[]) {
		JFrame frame = new JFrame ();
		frame.setPreferredSize(new Dimension(1000, 700));
		Main panel = new Main();
		frame.add (panel);
		frame.pack ();
		frame.setVisible (true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		new Thread(panel).start();
	}
	@Override
	public void run() {
		game.playAudio();
		while(true) {
			game.cycle();
			repaint();
			requestFocus();
		}
	}
}
