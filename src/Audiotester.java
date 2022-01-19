
import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.sound.sampled.*;

public class Audiotester extends JPanel implements Runnable {
	int pos;
	SoundProcessor sp;
	
	Audiotester() throws Exception {
		
		pos = 0;
		
		sp = new SoundProcessor("avril.wav");
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);/*
		for(int c = 0; c < sp.channels(); c++) {
			for(int i = 0; i < window; i++) {
				g.drawLine(i/5, sp.get(c,(pos+i)%sp.sze())/500+50 + 100*c, (i+1)/5, sp.get(c,(pos+i+1)%sp.sze())/500+50 + 100*c);
			}
			if(pos+512<sp.sze()) {
				short[] sbdata = sp.getsubdata(c, pos, pos+512);
				float[] arr;
				arr = DFT.fFFT(sbdata);
				g.setColor(Color.RED);
				for(int i = 0; i+1 < 256; i++) {
					g.drawLine((int)(100*Math.log(i)), 300+200*c+-(int)arr[i]/10000, (int)(100*Math.log(i+1)), 300+200*c+-(int)arr[(i+1)%256]/10000);
				}
				g.setColor(Color.BLACK);
			}
		}*/
		int N = 11;
		short[] sbdata = sp.getsubdata(0, pos, pos+(1<<N));
		float[] arr = DFT.fFFT(sbdata);
		double coef = 6/Math.log(1.0594630943592953);
		for(int i = 1; i < arr.length-1; i++) {
			g.drawLine((int)(Math.log(i)*coef), 550-(int)(arr[i]/(10<<N)), (int)(Math.log(i+1)*coef), 550-(int)(arr[i+1]/(10<<N)));
		}
		
		int[] sbdata_i = sp.getsubdata_interp(0, pos, pos+(1<<N)+1, 8);
		N += 3;
		arr = DFT.fFFT(sbdata_i);
		for(int i = 1; i < arr.length-1; i++) {
			g.drawLine((int)(Math.log(i)*coef), 250-(int)(arr[i]/(10<<N)), (int)(Math.log(i+1)*coef), 250-(int)(arr[i+1]/(10<<N)));
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
