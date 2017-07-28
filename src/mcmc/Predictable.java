package mcmc;

import mcmc.Sampleable;

/**
 * An interface for Sampleable models which can compute log predictive probabilities on test points.
 * Assumes there is a property "logPredictive" taking a test point as argument, returning the
 * log predictive.
 * @author ywteh
 *
 * @param <D> Type of test points.
 */
public interface Predictable<D> extends Sampleable {
	/**
	 * Log predictive probability of test point.
	 * @param datum Test point.
	 * @return Log predictive probability.
	 */
	public double logPredictive(D datum);
}
