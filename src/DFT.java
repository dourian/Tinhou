
/**
 * @author max13
 * class that computes the discrete Fourier transform of a sequence of numbers.
 * uses formula found here: https://brilliant.org/wiki/discrete-fourier-transform/
 */
public class DFT {
	public static float[] compute(short[] arr) {
		Complex[] proc = new Complex[arr.length/2];
		for(int i = 0; i < arr.length/2; i++) proc[i] = new Complex(0, 0);
		for(int k = 0; k < arr.length/2; k++)
			for(int n = 0; n < arr.length; n++)
				proc[k] = proc[k].plus(Complex.polar(-2*Math.PI*k*n/arr.length, arr[n]));
		float[] ret = new float[arr.length/2];
		for(int i = 0; i < arr.length/2; i++) ret[i] = (float)proc[i].abs();
		return ret;
	}
}
