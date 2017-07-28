package mcmc.collectors;

import mcmc.Sampleable;
import static java.lang.Math.log1p;
import static java.lang.Math.exp;
import static java.lang.Math.log;

/**
 * Geometric mean summary statistics collector.
 * @author ywteh
 */
public class GeometricMeanCollector extends StatsCollector<Double,Double>{
  /**
   * @param model A sampleable model.
   * @param property The property to be collected.
   * @param argument Argument.
   */
	public GeometricMeanCollector(Sampleable model, String property, Object argument) {
		super(model,property,argument,Double.NEGATIVE_INFINITY);
	}
	@Override Double add(Double sum, Double datum) {
		if (sum>datum) 
			return sum+log1p(exp(datum-sum));
		else 
			return datum+log1p(exp(sum-datum));
	}
  /**
   * Returns the geometric mean of the samples.
   */
	public double getGeometricMean() {
		return stats-log(number);
	}
	
}
