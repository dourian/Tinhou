
import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.sound.sampled.*;

public class Audiotester extends JPanel implements Runnable {
	int pos, window;
	SoundProcessor sp;
	
	Audiotester() throws Exception {
		
		pos = 0; window = 5000;
		
		sp = new SoundProcessor("sun.wav");
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		for(int c = 0; c < sp.channels(); c++) {
			for(int i = 0; i < window; i++) {
				g.drawLine(i/5, sp.get(c,(pos+i)%sp.sze())/500+50 + 100*c, (i+1)/5, sp.get(c,(pos+i+1)%sp.sze())/500+50 + 100*c);
			}
			if(pos+1024<sp.sze()) {
				float[] arr = DFT.compute(sp.getsubdata(c, pos, pos+512));
				for(int i = 0; i+1 < 256; i++) {
					g.drawLine((int)(100*Math.log(i)), 300+200*c+-(int)arr[i]/10000, (int)(100*Math.log(i+1)), 300+200*c+-(int)arr[(i+1)%256]/10000);
				}
			}
		}
		
		g.drawString(Integer.toString(pos), 10, 10);
	}
	
	public static void main(String[] args) throws Exception {
		
		Audiotester AT = new Audiotester();
		
		JFrame frame = new JFrame();
		frame.setPreferredSize(new Dimension(1000, 700));
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
