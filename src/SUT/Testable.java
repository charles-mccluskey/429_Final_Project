package SUT;

public class Testable {

	public Testable() {
		
	}
	
	public static double AreaRectangle(double a, double b) {
		double result = a*b;
		return result;
	}
	
	public static double AreaCircle(double radius) {
		double result = radius*radius*Math.PI;
		return result;
	}
	
	public static double Circumference(double radius) {
		double result = 2*radius*Math.PI;
		return result;
	}
	
}
