
import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.sound.sampled.*;

public class Audiotester extends JPanel implements Runnable {
	int pos, window;
	short arr[];
	AudioInputStream ais;
	String file;
	
	Audiotester() throws Exception {
		
		ais = AudioSystem.getAudioInputStream(new File(file = "sun.wav"));
		pos = 0; window = 5000;
		
		int len = (int)ais.getFrameLength();
		int sze = (int)ais.getFormat().getFrameSize();
		System.out.println(len);
		byte[] barr = new byte[len*sze];
		ais.read(barr);
		
		arr = new short[len];
		for(int i = 0; i < len*sze;i++) {
			arr[i/sze] += barr[i]<<(i%2==1?8:0);
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		for(int i = 0; i < window; i++) {
			g.drawLine(i/5, arr[(pos+i)%arr.length]/500+150, (i+1)/5, arr[(pos+i+1)%arr.length]/500+150);
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
			ais = AudioSystem.getAudioInputStream(new File(file));
			Clip play = AudioSystem.getClip();
			play.open(ais);
			play.start();
			while(pos < arr.length) {
				pos = play.getFramePosition();
				repaint();
				Thread.sleep(10);
			} 
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
