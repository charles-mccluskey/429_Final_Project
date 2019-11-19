package Mutants;

public class Mutant18 {

	public Mutant18() {
		
	}
	
	public double AreaRectangle(double a, double b) {
		double result = a*b;
		return result;
	}
	
	public double AreaCircle(double radius) {
		double result = radius*radius*Math.PI;
		return result;
	}
	
	public double Circumference(double radius) {
		double result = 2*radius*Math.PI;
		return result;
	}
	
	public double Power(double base, double pow) {
	        if (pow == 0) { 
	            return 1; 
	        }
	        double answer = base; 
	        double increment = base; 
	        double i, j; 
	        for (i = 1; i < pow; i++) { 
	            for (j = 1; j < base; j++) { 
 	                answer /= increment;//MUTANT LINE
	            } 
	            increment = answer; 
	        } 
	        return answer; 
	    }
}
