package mcmc.kernel;

import utilities.Generator;
/**
 * Abstract class implementing a slice sampling update over a bounded interval.
 * This is a simple version of slice sampling in a one dimensional bounded interval.
 * @author ywteh
 *
 */
public abstract class SliceFiniteInterval {
	double lower, upper;
  Generator gen;
	/**
	 * Constructor for a slice sampler over bounded interval.
	 * @param lower Lower boundary of interval.
	 * @param upper Upper boundary of interval.
	 */
	public SliceFiniteInterval(double lower, double upper, Generator gen) {
		this.lower = lower;
		this.upper = upper;
    this.gen = gen;
		assert lower < upper && lower>Double.NEGATIVE_INFINITY && upper<Double.POSITIVE_INFINITY;
	}
	/**
	 * Method to compute log density (up to constant) at value of variable.
	 * @param value Variable value.
	 * @return log p(x).
	 */
	abstract public double logDensity(double value);
	/**
	 * Slice samples a new value for variable.
	 * @param value Current value of variable.
	 * @return New value.
	 */
	public double sample(double value) {
		double slice = logDensity(value) - gen.nextExponential();
    return sample(value,slice);
  }
	/**
	 * Slice samples a new value for variable.
	 * @param value Current value of variable.
   * @param slice Slice value.
	 * @return New value.
	 */
  double sample(double value, double slice) {
		double l = lower;
		double u = upper;
		while (true) {
			double newvalue = gen.nextUniform(l, u);
			if (logDensity(newvalue)>slice) return newvalue;
			if (newvalue>value) u = newvalue;
			else l = newvalue;
		}
	}
  public void setLower(double l) {
    lower = l;
    assert lower<=upper;
  }
  public void setUpper(double u) {
    upper = u;
    assert lower<=upper;
  }
}
