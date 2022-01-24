import java.util.Objects;
/**
 * @author max13
 * basic complex number that supports arithmetic operations. 
 * immutable. 
 */
public class Complex implements Comparable<Complex> {
	private double re, im;			//real and imaginary component of complex number
	private final double EPS = 1e-4;
	/**
	 * constructor for complex number
	 * @param real real value
	 * @param imag imaginary value
	 */
	public Complex(double real, double imag) {re = real; im = imag;}
	
	/**
	 * second "constructor"
	 * @param arg angle
	 * @param mag magnitude
	 * @return complex number with specified angle and magnitude
	 */
	public static Complex polar(double arg, double mag) {return new Complex(Math.cos(arg)*mag, Math.sin(arg)*mag);}
	
	/**
	 * @return magnitude of complex number
	 */
	public double abs() {return Math.hypot(re, im);}
	/**
	 * @return angle of complex number
	 */
	public double arg() {return Math.atan(im/re) + (re<0?Math.PI:0);}
	/**
	 * @return conjugate of complex number
	 */
	public Complex conj() {return new Complex(re, -im);}
	/**
	 * divides magnitude
	 * @param d parameter to divide magnitude by
	 * @return new complex with divided magnitude
	 */
	public Complex div(double d) {return new Complex(re/d,im/d);}
	/**
	 * subtracts number
	 * @param c parameter to subtract by
	 * @return new resulting complex
	 */
	public Complex minus(Complex c) {return new Complex(re-c.re,im-c.im);}
	/**
	 * subtracts number
	 * @param d real number to subtract by
	 * @return resulting complex
	 */
	public Complex minus(double d) {return new Complex(re-d,im);}
	/**
	 * multiplies number
	 * @param c complex to multiply by
	 * @return resulting complex
	 */
	public Complex mult(Complex c) {return new Complex(re*c.re-im*c.im,re*c.im+im*c.re);}
	/**
	 * multiplies number
	 * @param d real number to multiply by
	 * @return resulting complex
	 */
	public Complex mult(double d) {return new Complex(re*d,im*d);}
	/**
	 * adds number
	 * @param c complex to add by
	 * @return resulting complex
	 */
	public Complex plus(Complex c) {return new Complex(re+c.re,im+c.im);}
	/**
	 * adds number
	 * @param d real to add by
	 * @return resulting complex
	 */
	public Complex plus(double d) {return new Complex(re+d,im);}
	
	/**
	 * @return real component of complex number
	 */
	public double real() {return re;}
	/**
	 * @return imaginary component of complex number
	 */
	public double imag() {return im;}
	
	/**
	 * @param o compare to
	 * @return whether or not this number is roughly equal to o
	 */
	public boolean equals(Object o) {return minus((Complex)o).abs()<EPS;}
	public int hashCode() {return Objects.hash(re, im);}
	/**
	 * @param o complex to compare to
	 * @return sorts in decreasing real value order.
	 */
	public int compareTo(Complex o) {
		return (int)((o.real()-real())*100);
	}
}
