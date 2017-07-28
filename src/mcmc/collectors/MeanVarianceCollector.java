package mcmc.collectors;

import mcmc.Sampleable;

/**
 * Mean and variance collector.
 * @author ywteh
 */
public class MeanVarianceCollector extends StatsCollector<Double[],Double>{
	static Double[] zero = {0.0,0.0};
  /**
   * @param model A sampleable model.
   * @param property The property to be collected.
   * @param argument Argument.
   */
	public MeanVarianceCollector(Sampleable model, String property, Object argument) {
		super(model,property,argument,zero);
	}
	@Override Double[] add(Double[] sum, Double datum) {
		sum[0] += datum;
		sum[1] += datum*datum;
		return sum;
	}
	public double getMean() {
		return stats[0]/number;
	}
	public double getVariance() {
		double mean = getMean();
		return stats[1]/(number-1)-mean*mean;
	}
}
