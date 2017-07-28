package mcmc.collectors;

import mcmc.Sampleable;
import static java.lang.Math.ceil;
import static java.lang.Math.floor;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import java.util.*;

/**
 * An empirical collector for real-valued statistics.
 *
 * @author ywteh
 */
public class DoubleEmpiricalCollector extends EmpiricalCollector<Double>{
	boolean sorted = false;
  /**
   * Constructor for double empirical collector.
   *
   * @param model A sampleable model.
   * @param property The statistics to be collected.
   * @param argument Additional argument for statistics.
   */
	public DoubleEmpiricalCollector(Sampleable model, String property, Object argument) {
		super(model,property,argument);
	}
	@Override public void collect() {
		sorted = false;
		super.collect();
	}
	void sort() {
		if (sorted) return;
		Collections.sort(data);
		sorted = true;
	}
  /**
   * Returns the p'th percentile among collected values.
   *
   * @param p Number between 0 and 1.
   * @return The p'th percentile value.
   */
	public double getPercentile(double p) {
		if (data.isEmpty()) return Double.NaN;
		sort();
		double s = p*(double)data.size();
		double l = floor(s);
		double u = ceil(s);
		int il = (int) l;
		int iu = (int) u;
		if (il==iu) return data.get(il);
		else return data.get(il)*(s-l)/(u-l)+data.get(iu)*(u-s)/(u-l);
	}
  /**
   * Returns the lower quartile value.
   * @return The lower quartile value.
   */
	public double getLowerQuantile() {
		return getPercentile(.25);
	}
  /**
   * Returns the upper quartile value.
   * @return The upper quartile value.
   */
	public double getUpperQuantile() {
		return getPercentile(.75);
	}
  /**
   * Returns the median.
   * @return The median.
   */
	public double getMedian() {
		return getPercentile(.5);
	}
  /**
   * Returns the minimum value.
   * @return The minimum value.
   */
	public double getMin() {
		if (data.isEmpty()) return Double.NaN;
		sort();
		return data.get(0);
	}
  /**
   * Returns the maximum value.
   * @return The maximum value.
   */
	public double getMax() {
		if (data.isEmpty()) return Double.NaN;
		sort();
		return data.get(data.size()-1);
	}
  /**
   * Returns the mean.
   * @return The mean.
   */
	public double mean() {
		if (data.isEmpty()) return Double.NaN;
		double sum = 0.0;
		for ( Double datum : data ) 
			sum += datum;
		return sum/data.size();
	}
  /**
   * Returns the geometric mean.
   * @return The geometric mean.
   */
	public double getGeometricMean() {
		double max = getMax();
		double sum = 0.0;
		for ( Double datum : data ) 
			sum += exp(datum-max);
		return max+log(sum);
	}
}
