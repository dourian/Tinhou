
import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.sound.sampled.*;

public class Audiotester extends JPanel implements Runnable {
	int pos;
	SoundProcessor sp;
	
	Audiotester() throws Exception {
		
		pos = 0;
		
		sp = new SoundProcessor("keshi.wav");
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		int N = 10;
		sp.fftNow(1<<N, 0);
		float[] arr = sp.fftget(0);
		double coef = 6/Math.log(1.0594630943592953);
		for(int i = 1; i < arr.length-1; i++) {
			g.drawLine((int)(Math.log(i)*coef), 550-(int)arr[i], (int)(Math.log(i+1)*coef), 550-(int)arr[i+1]);
		}
		
		sp.fftNow(1<<N, 2);
		N += 2;
		arr = sp.fftget(0);
		for(int i = 1; i < arr.length-1; i++) {
			g.drawLine((int)(Math.log(i)*coef), 250-(int)arr[i], (int)(Math.log(i+1)*coef), 250-(int)arr[i+1]);
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
				//Thread.sleep(10);
			} 
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
