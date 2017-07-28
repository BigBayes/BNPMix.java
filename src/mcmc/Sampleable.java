package mcmc;

import utilities.Generator;

/**
 * An object is sampleable if it implements a probabilistic model with an MCMC 
 * inference algorithm.
 * 
 * @author Yee Whye Teh <mailto:ywteh@gatsby.ucl.ac.uk>
 *
 */
public interface Sampleable extends Collectable {
	/**
	 * Initialize model at start of MCMC sampling. 
	 */
	public void initializeSampler();
	
	/**
	 * Clean up object at end of MCMC sampling. 
	 */
	public void finishSampler();

	/**
	 * Runs 1 iteration of the MCMC inference algorithm.
	 */
	public void sample();
	
}
