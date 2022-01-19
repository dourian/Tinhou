import java.io.*;
import java.util.Arrays;

import javax.sound.sampled.*;
/**
 * @author max13
 * sound processor class handles the input and output of sound files into audio and extracts characteristics of songs that can be interpreted as bullet patterns
 */
public class SoundProcessor {
	private AudioFormat af;
	private Clip audio;
	private short[][] data;
	SoundProcessor(String fileName) throws IOException {
		AudioInputStream ais = null; audio = null;
		
		try {
			ais = AudioSystem.getAudioInputStream(new File(fileName));
			
			audio = AudioSystem.getClip();
			audio.open(ais);
			af = ais.getFormat();
			
			ais = AudioSystem.getAudioInputStream(new File(fileName));
			
			data = new short[af.getChannels()][(int)(ais.getFrameLength())];
			byte[] arr = new byte[(int)(ais.getFrameLength()*af.getFrameSize())];
			ais.read(arr);
			
			for(int i = 0; i < arr.length; i++) {
				data[
				     i%af.getFrameSize()/2 //channel
				     ]
				    [
				     i/af.getFrameSize() //position
				     ] += arr[i]<<(i%(af.getSampleSizeInBits()/8)*8);
			}
			
			ais.close();
			
		} catch(UnsupportedAudioFileException | LineUnavailableException e) {
			e.printStackTrace();
			return;
		}
	}
	
	short get(int x, int y) {return data[x][y];}
	short[] getsubdata(int x, int a, int b) {return Arrays.copyOfRange(data[x],a,b);}
	int[] getsubdata_interp(int x, int a, int b, int f) {
		short[] arr = getsubdata(x, a, b);
		int[] ret = new int[(arr.length-1)*f];
		for(int i = 1; i < arr.length-1; i++) {
			double A = ((int)arr[i+1])+arr[i-1]>>1, B = ((int)arr[i+1])-arr[i-1]>>1, C = arr[i];
			for(int i2 = -f; i2 <= f; i2++) {
				double tmp = (double)i2/f;
				if(i+i2 >= 0 && i+i2 < (arr.length-1)*f) ret[i+i2] = (int)(A*tmp*tmp+B*tmp+C);
			}
		}
		return ret;
	}
	int sze() {return data[0].length;}
	
	void play() {audio.start();}
	int audioPos() {return audio.getFramePosition();}
	int channels() {return af.getChannels();}
}
