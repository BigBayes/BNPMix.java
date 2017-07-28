package mcmc.kernel;

import utilities.Generator;
/**
 * Abstract class implementing a slice sampling update over a bounded interval.
 * This is a simple version of slice sampling in a one dimensional bounded interval.
 * @author ywteh
 *
 */
public abstract class SliceStepOut extends SliceFiniteInterval {
  double stepsize;
  int numstep;
	/**
	 * Constructor for a slice sampler over bounded interval.
	 * @param lower Lower boundary of interval (can be -infinity).
	 * @param upper Upper boundary of interval (can be +infinity).
	 */
	public SliceStepOut(double stepsize, int numstep, Generator gen) {
    super(0.0,1.0,gen);
    this.stepsize = stepsize;
    this.numstep = numstep;
    assert stepsize > 0.0 && numstep > 0;
	}
	/**
	 * Slice samples a new value for variable.
	 * @param value Current value of variable.
	 * @return New value.
	 */
	@Override public double sample(double value) {
		double slice = logDensity(value) - gen.nextExponential();
		double l = value - stepsize*gen.nextUniform();
		double u = l + stepsize;
    int nl = gen.nextUniform(numstep);
		for (int i=0; i<nl; i++) {
      double y = logDensity(l);
      if (y < slice) {
        break;
      }
      l -= stepsize;
    }
    int nu = numstep - 1 - nl;
		for (int i=0; i<nu; i++) {
      double y = logDensity(u);
      if (y < slice) {
        break;
      }
      u += stepsize;
    }
    setLower(l);
    setUpper(u);
    return super.sample(value,slice);
	}
}
