import java.util.Objects;
/**
 * @author max13
 * basic complex number that supports arithmetic operations. 
 */
public class Complex implements Comparable<Complex> {
	private double re, im;
	private final double EPS = 1e-6;
	
	public Complex(double real, double imag) {re = real; im = imag;}
	public static Complex polar(double arg, double mag) {return new Complex(Math.cos(arg)*mag, Math.sin(arg)*mag);}
	
	public double abs() {return Math.hypot(re, im);}
	public double arg() {return Math.atan(im/re) + (re<0?Math.PI:0);}
	public Complex conj() {return new Complex(re, -im);}
	public Complex div(double d) {return new Complex(re/d,im/d);}
	public Complex minus(Complex c) {return new Complex(re-c.re,im-c.im);}
	public Complex minus(double d) {return new Complex(re-d,im);}
	public Complex mult(Complex c) {return new Complex(re*c.re-im*c.im,re*c.im+im*c.re);}
	public Complex mult(double d) {return new Complex(re*d,im*d);}
	public Complex plus(Complex c) {return new Complex(re+c.re,im+c.im);}
	public Complex plus(double d) {return new Complex(re+d,im);}
	
	public double real() {return re;}
	public double imag() {return im;}
	
	public boolean equals(Object o) {return minus((Complex)o).abs()<EPS;}
	public int hashCode() {return Objects.hash(re, im);}
	public int compareTo(Complex o) {
		return (int)((o.real()-real())*100);
	}
}
