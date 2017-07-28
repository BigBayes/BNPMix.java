package mcmc.kernel;

import static java.lang.Math.exp;
import static java.lang.Math.min;
import static utilities.DataIO.vlog;
import static utilities.DataIO.vlogln;
import static utilities.Generator.generator;

/**
 * Abstract class implementing Metropolis update.
 * This is a MCMC kernel with a reversible proposal distribution with probabilities of forward
 * and reverse proposals exactly equal.
 * @author ywteh
 *
 * @param <D> Type of variable sampled.
 */
public abstract class Metropolis<D> {
	double acceptancerate;
	int num;
	/**
	 * Constructor.
	 */
	public Metropolis() {
		acceptancerate = 0.0;
		num = 0;
	}
	/**
	 * Method to compute ratio of log densities.
	 * @param newvalue Proposed value of variable.
	 * @param curvalue Current value of variable.
	 * @return log p(y) - log p(x)
	 */
	abstract public double logratio(D newvalue, D curvalue);
	/**
	 * Generates a proposed new value.
	 * @param value Current value of variable.
	 * @return Proposed value.
	 */
	abstract public D propose(D value);
	
	/**
	 * Proposes a new value for variable, then accept/reject this value.
	 * @param value Current value.
	 * @return New value (if accepted) or current value (if rejected).
	 */
	public D sample(D value) {
		D newvalue = propose(value);
		double arate = min(1.0,exp(logratio(newvalue,value)));
		vlog(2,"acceptance rate="+arate);
		acceptancerate += (arate-acceptancerate)/(++num);
		if (generator.nextBoolean(arate)) {
			vlogln(2," ...accepted");
			return newvalue;
		} else {
			vlogln(2," ...rejected");
			return value;
		}
	}
	/**
	 * @return Empirical acceptance rate.
	 */
	public double getAcceptanceRate() {
		return acceptancerate;
	}
	/**
	 * Resets computation of empirical acceptance rate.
	 */
	public void resetAcceptanceRate() {
		acceptancerate = 0.0;
		num = 0;
	}
}
