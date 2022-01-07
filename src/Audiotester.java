
import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.sound.sampled.*;

public class Audiotester extends JPanel implements Runnable {
	int pos, window;
	SoundProcessor sp;
	
	Audiotester() throws Exception {
		
		pos = 0; window = 5000;
		
		sp = new SoundProcessor("test.wav");
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		for(int c = 0; c < sp.channels(); c++) {
			for(int i = 0; i < window; i++) {
				g.drawLine(i/5, sp.get(c,(pos+i)%sp.sze())/500+50 + 100*c, (i+1)/5, sp.get(c,(pos+i+1)%sp.sze())/500+50 + 100*c);
			}
		}
		
		g.drawString(Integer.toString(pos), 10, 10);
	}
	
	public static void main(String[] args) throws Exception {
		
		Audiotester AT = new Audiotester();
		
		JFrame frame = new JFrame();
		frame.setPreferredSize(new Dimension(1000, 300));
		frame.add(AT);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Thread t = new Thread(AT);
		t.start();
	}

	@Override
	public void run() {
		try {
			sp.play();
			while(pos < sp.sze()) {
				pos = sp.audioPos();
				repaint();
				Thread.sleep(10);
			} 
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
