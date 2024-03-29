import java.io.*;
import java.util.Arrays;
import javax.sound.sampled.*;

/**
 * @author maxwell
 * Jan 24, 2022
 * sound processor class handles the input and output of sound files into audio and extracts characteristics of songs that can be interpreted as bullet patterns
 */
public class SoundProcessor {
	private AudioFormat af;
	private Clip audio;		//used to play audio file itself
	private short[][] data;	//raw audio data
	float[][] fftdata;		//previous fft outputs in circular array
	float[] historic;		//historic averages for fft outputs
	int pointer;			//pointer used in circular array
	/**
	 * initializes audio processor
	 * @param fileName audio file
	 * @throws IOException error reading file
	 */
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
			historic = new float[1<<15];
			ais.close();
			
		} catch(UnsupportedAudioFileException | LineUnavailableException e) {
			e.printStackTrace();
			return;
		}
		
		fftdata = new float[5][1];
		pointer = 0;
	}
	/**
	 * gets data
	 * @param x index
	 * @param y index
	 * @return data[x][y]
	 */
	short get(int x, int y) {return data[x][y];}
	/**
	 * gets subarray of data
	 * @param x index
	 * @param a begin
	 * @param b end
	 * @return data[x][[a,b)]
	 */
	short[] getsubdata(int x, int a, int b) {return Arrays.copyOfRange(data[x],a,b);}
	/**
	 * gets subarray of data (with interpolation)
	 * uses quadratic interpolation
	 * @param x index
	 * @param a begin
	 * @param b end
	 * @param f interpolation quotient
	 * @return data[x][[a,b)] with x interpolation
	 */
	int[] getsubdata_interp(int x, int a, int b, int f) {
		short[] arr = getsubdata(x, a, b);
		int[] ret = new int[(arr.length-1)*f];
		for(int i = 1; i < arr.length-1; i++) {
			double A = ((int)arr[i+1])+arr[i-1]>>1, B = ((int)arr[i+1])-arr[i-1]>>1, C = arr[i];
			for(int i2 = -f; i2 <= f; i2++) {
				double tmp = (double)i2/f;
				if(i+i2 >= 0 && i+i2 < (arr.length-1)*f) ret[i+i2] += (int)(A*tmp*tmp+B*tmp+C)/2;
			}
		}
		return ret;
	}
	/**
	 * @return length of data
	 */
	int sze() {return data[0].length;}
	/**
	 * plays audio
	 */
	void play() {audio.start();}
	/**
	 * stops audio
	 */
	void stop() {audio.stop();}
	/**
	 * @return position of clip
	 */
	int audioPos() {return audio.getFramePosition();}
	/**
	 * @return # of channels
	 */
	int channels() {return af.getChannels();}
	/**
	 * ffts current audio position
	 * @param window fft window
	 * @param interp interpolation quotient
	 */
	void fftNow(int window, int interp) {
		int pos = audioPos();
		for(int i = 0; i < fftdata[pointer].length; i++)
			historic[i] = Math.max(historic[i]*0.995f, fftdata[pointer][i]*0.5f + historic[i]*0.5f);
		pointer++; pointer %= 5;
		if(interp == 0) fftdata[pointer] = DFT.fFFT(getsubdata(0, pos, pos+window));
		else fftdata[pointer] = DFT.fFFT(getsubdata_interp(0, pos, pos+window+1, interp));
		float lg = (float) ((500<<(int)Math.log(window))/Math.log(1.5));
		for(int i = 0; i <fftdata[pointer].length; i++) {
			fftdata[pointer][i] /= lg / Math.log(i);
		}
	}
	/**
	 * accesses fft data
	 * @param idx index, 0 for current fft, negative for historic fft, 1 for historic averages
	 * @return fft data
	 */
	float[] fftget(int idx) {
		if(idx == 1) return historic;
		return fftdata[((pointer+idx)%5+5)%5];
	}
	//disclaimer: i have no idea what the ranges for treble and bass are. im just using these terms to describe the functionalities of the methods. 
	/**
	 * analyzes bass data
	 * @return 2d array representing bass data used in bullet patterns
	 */
	float[][] bassAnalyze() {
		float[][] data = {fftget(0), fftget(-1), fftget(-2)};
		int len = Math.min(Math.min(data[0].length, data[1].length), Math.min(data[2].length, data[2].length));
		if(len == 4096) {
			len /= 2;
			float[][] means = new float[data.length][3];
			for(int i = 0; i < means.length; i++)
				for(int i2 = 1; i2 < 25; i2++) means[i][i2<8?0:(i2<10?1:2)] += data[i][i2]/25;
			return means;
		}
		return null;
	}
	/**
	 * analyzes treble data
	 * @return 2d array representing treble data used in bullet patterns
	 */
	float[][] trebleAnalyze() {
		float[][] data = {fftget(0), fftget(-1), fftget(-2)};
		int len = Math.min(Math.min(data[0].length, data[1].length), Math.min(data[2].length, data[2].length));
		if(len == 4096) {
			len /= 2;
			float cnt[][] = new float[data.length][1000];
			for(int i = 0; i < data.length; i++)
				for(int i2 = 256; i2 < len; i2++)
					cnt[i][(int)data[i][i2]]++;
			return cnt;
		}
		return null;
	}
}
