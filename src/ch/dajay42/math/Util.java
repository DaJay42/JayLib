package ch.dajay42.math;

import java.util.Arrays;
import java.util.Comparator;

public final class Util {
	
	private Util(){throw new UnsupportedOperationException();} //no instances for you.
	
	//note: for addition, see Double::sum
	public static double subtraction(double a, double b){
		return a - b;
	}
	public static double multiplication(double a, double b){
		return a * b;
	}
	public static double division(double a, double b){
		return a / b;
	}
	public static double rightDivision(double a, double b){
		return b / a;
	}
	
	public static double identity(double a){
		return a;
	}
	public static double negation(double a){
		return -a;
	}

	public static final Comparator<Double> ABS_VALUE_COMPARATOR = Comparator.comparingDouble(Math::abs);
	
	public static double clamp(double min, double max, double val){
		return Math.min(max, Math.min(max, val));
	}
	
	public static double lerp(double min, double max, double p){
		return p*(max-min)+min;
	}
	
	public static double unLerp(double min, double max, double val){
		return (val-min)/(max-min);
	}
	
	public static double reLerp(double oldMin, double oldMax, double newMin, double newMax, double oldVal){
		return (oldVal-oldMin)*(newMax-newMin)/(oldMax-oldMin)+newMin;
	}
	
	public static double angleLerpRad(double min, double max, double p){
		double twoPi = 2*Math.PI;
		double dAngle = (min - max + twoPi) % twoPi;
		
		return max + (dAngle < Math.PI ? p*dAngle : p*(dAngle - twoPi));
	}
	
	public static double angleLerpDeg(double min, double max, double p){
		double dAngle = (min - max + 360d) % 360d;
		
		return max + (dAngle < 180 ? p*dAngle : p*(dAngle - 360d));
	}
	
	public static double sum(double[] args){
		double r = 0.0;
		for(double d : args){
			r += d;
		}
		return r;
	}
	
	public static double preciseSum(Double[] args){
		double r = 0.0;
		Arrays.sort(args, ABS_VALUE_COMPARATOR);
		for(double d : args){
			r += d;
		}
		return r;
	}
	
	public static double mean(double[] args){
		return sum(args)/args.length;
	}
	
	public static double leastSquaresLoss(double truth, double guess){
		double d = truth - guess;
		return 0.5*Math.pow(d,2);
	}
	
	public static int gcd(int a, int b){
		return b == 0 ? a : gcd(b,a % b);
	}
}
