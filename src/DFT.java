
/**
 * @author max13
 * class that computes the discrete Fourier transform of a sequence of numbers.
 * uses formula found here: https://brilliant.org/wiki/discrete-fourier-transform/
 */
public class DFT {
	public static double[] compute(int[] arr) {
		double[] ret = new double[arr.length];
		for(int k = 0; k < arr.length; k++)
			for(int n = 0; n < arr.length; n++)
				ret[k] = Complex.polar(-2*Math.PI*k*n/arr.length, 1).mult(arr[n]).abs();
		return ret;
	}
}
