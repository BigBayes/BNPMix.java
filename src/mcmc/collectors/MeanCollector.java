package mcmc.collectors;

import mcmc.Sampleable;

/**
 * Mean collector.
 * @author ywteh
 */
public class MeanCollector extends StatsCollector<Double,Double>{
  /**
   * @param model A sampleable model.
   * @param property The property to be collected.
   * @param argument Argument.
   */
	public MeanCollector(Sampleable model, String property, Object argument) {
		super(model,property,argument,0.0);
	}
	@Override Double add(Double sum, Double datum) {
		return sum+datum;
	}
  /**
   * Returns mean of samples.
   */
	public double getMean() {
		return stats/number;
	}
}
