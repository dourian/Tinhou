import java.io.*;
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
	int sze() {return data[0].length;}
	
	void play() {audio.start();}
	int audioPos() {return audio.getFramePosition();}
	int channels() {return af.getChannels();}
}
