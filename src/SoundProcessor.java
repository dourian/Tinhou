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
		} catch(UnsupportedAudioFileException | LineUnavailableException e) {
			e.printStackTrace();
			return;
		}
		
		af = ais.getFormat();
		data = new short[af.getChannels()][(int)(ais.getFrameLength()/af.getChannels())];
		
		try {
			ais = AudioSystem.getAudioInputStream(new File(fileName));
		} catch(UnsupportedAudioFileException e) {
			e.printStackTrace();
			return;
		}

		byte[] arr = new byte[(int)(ais.getFrameLength()*af.getFrameSize())];
		ais.read(arr);
		
		for(int i = 0, channel = 0; i < arr.length; i++) {
			data[channel][i/data.length/af.getFrameSize()] += arr[i]<<(i%af.getFrameSize()*8);
			if(i%af.getFrameSize() == af.getFrameSize()-1) channel= (channel+1)%data.length;
		}
		
		ais.close();
	}
	short get(int x, int y) {return data[x][y];}
	int sze() {return data[0].length;}
	
	void play() {audio.start();}
	int audioPos() {return audio.getFramePosition();}
}
