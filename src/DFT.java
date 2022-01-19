
/**
 * @author max13
 * class that computes the discrete Fourier transform of a sequence of numbers.
 * uses formula found here: https://brilliant.org/wiki/discrete-fourier-transform/
 */
public class DFT {
	
	public static Complex[] compute(short[] arr) { //naive N^2 DFT for answer checking
		Complex[] ret = new Complex[arr.length/2];
		for(int i = 0; i < arr.length/2; i++) ret[i] = compute(arr, i);
		return ret;
	}
	public static Complex[] FFT(short[] arr) { //NlogF DFT computation. assumes arr is length 2^k
		if(Integer.bitCount(arr.length) > 1) {
			System.out.println("Error FFT length: " + arr.length);
			return compute(arr);
		}
		Complex[] ret = new Complex[arr.length], eret, oret; ret[0] = new Complex(arr[0], 0); 
		if(arr.length == 1) return ret;
		short[][] sub = new short[2][arr.length/2];
		for(int i = 0; i < arr.length; i++) sub[i%2][i/2] = arr[i];
		eret = FFT(sub[0]); oret = FFT(sub[1]);
		for(int i = 0; i < arr.length/2; i++) {
			ret[i] = 				eret[i].plus	(oret[i].mult(Complex.polar(-2*Math.PI*i/arr.length, 1)));
			ret[i+arr.length/2] = 	eret[i].minus	(oret[i].mult(Complex.polar(-2*Math.PI*i/arr.length, 1)));
		}
		return ret;
	}
	public static float[] fFFT(short[] arr) {
		Complex[] a = FFT(arr);
		float[] ret = new float[arr.length];
		for(int i = 0; i < arr.length; i++) ret[i] = (float)a[i].abs();
		return ret;
	}
	public static Complex compute(short[] arr, double k) {
		Complex c = new Complex(0, 0);
		for(int n = 0; n < arr.length; n++) c = c.plus(Complex.polar(2*Math.PI*k*n/arr.length, arr[n]));
		return c;
	}
	
	public static Complex[] compute(int[] arr) { //naive N^2 DFT for answer checking
		Complex[] ret = new Complex[arr.length/2];
		for(int i = 0; i < arr.length/2; i++) ret[i] = compute(arr, i);
		return ret;
	}
	public static Complex[] FFT(int[] arr) { //NlogF DFT computation. assumes arr is length 2^k
		if(Integer.bitCount(arr.length) > 1) {
			System.out.println("Error FFT length: " + arr.length);
			return compute(arr);
		}
		Complex[] ret = new Complex[arr.length], eret, oret; ret[0] = new Complex(arr[0], 0); 
		if(arr.length == 1) return ret;
		int[][] sub = new int[2][arr.length/2];
		for(int i = 0; i < arr.length; i++) sub[i%2][i/2] = arr[i];
		eret = FFT(sub[0]); oret = FFT(sub[1]);
		for(int i = 0; i < arr.length/2; i++) {
			ret[i] = 				eret[i].plus	(oret[i].mult(Complex.polar(-2*Math.PI*i/arr.length, 1)));
			ret[i+arr.length/2] = 	eret[i].minus	(oret[i].mult(Complex.polar(-2*Math.PI*i/arr.length, 1)));
		}
		return ret;
	}
	public static float[] fFFT(int[] arr) {
		Complex[] a = FFT(arr);
		float[] ret = new float[arr.length];
		for(int i = 0; i < arr.length; i++) ret[i] = (float)a[i].abs();
		return ret;
	}
	public static Complex compute(int[] arr, double k) {
		Complex c = new Complex(0, 0);
		for(int n = 0; n < arr.length; n++) c = c.plus(Complex.polar(2*Math.PI*k*n/arr.length, arr[n]));
		return c;
	}
}
