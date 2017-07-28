package utilities;

import java.lang.Math;

public class Summary {
	private double sum_x;
	private double sum_xx;
	private double num_x;
	private double min_x = Double.MAX_VALUE;
	private double max_x = Double.MIN_VALUE;
	
	public Summary() {}
	
	public void add(double x) {
		sum_x += x;
		sum_xx += x*x;
		num_x += 1.0;
		min_x = Math.min(min_x,x);
		max_x = Math.max(max_x,x);
	}
	
	public double mean() {
		return sum_x/num_x;
	}
	
	public double max() {
		return max_x;
	}
	
	public double min() {
		return min_x;
	}
	
	public double var() {
		return sum_xx/num_x - mean()*mean();
	}
	
	public double stddev() {
		return Math.sqrt(var());
	}
}
