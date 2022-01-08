
/**
 * @author max13
 * class that computes the discrete Fourier transform of a sequence of numbers.
 * uses formula found here: https://brilliant.org/wiki/discrete-fourier-transform/
 */
public class DFT {
	public static float[] compute(short[] arr) {
		float[] ret = new float[arr.length/2];
		for(int i = 0; i < arr.length/2; i++) ret[i] = compute(arr, i);
		return ret;
	}
	public static float compute(short[] arr, double k) {
		Complex c = new Complex(0, 0);
		for(int n = 0; n < arr.length; n++) c = c.plus(Complex.polar(2*Math.PI*k*n/arr.length, arr[n]));
		return (float)c.abs();
	}
}
