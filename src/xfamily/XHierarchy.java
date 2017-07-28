package xfamily;


/**
 * Interface for an exponential family distribution with an exponential family prior.
 * An object of this type denotes a distribution from which iid draws may be observed.
 * 
 * @author Yee Whye Teh <mailto:ywteh@gatsby.ucl.ac.uk>
 *
 * @param <D> The base type over which the distribution is defined.
 * @param <F> The exponential family type.
 * @param <P> The exponential family prior type.
 */
public interface XHierarchy<D,F extends XFamily<D>,P extends XPrior<F>> {
	/**
	 * Adds datum as an iid observation drawn from the distribution.
	 * 
	 * @param datum	The data item
	 */
	public void addDatum(D datum);
	
	/**
	 * Removes datum as an iid observation drawn from the distribution.
	 * 
	 * @param datum	The data item
	 */
	public void removeDatum(D datum);

  /**
   * Clears data from the distribution.
   */
  public void clearData();

	/**
	 * Returns the number of iid observations drawn from the distribution.
	 * @return 	The number of observations.
	 */
	public int numDatum();
	
	/**
	 * Returns the log joint probability of observations (and of parameters) of the distribution.
	 * @return	The log joint probability.
	 */
	public double logJoint();
  /**
   * Returns the log probability of datum under the distribution.
   * @param datum The data item.
   * @return The log probability of the data item.
   */
	public double logPredictive(D datum);
  /**
   * Updates the parameters of the hierarchy using some MCMC sampling update.
   */
	public void sample();
  /**
   * Returns a draw from the exponential family distribution.
   * @return A draw from the distribution.
   */
	public D drawDatum();
  /**
   * Returns the exponential family distribution.
   * @return The parameters of the distribution.
   */
	public F getParameter();
  /**
   * Tests for equality to another hierarchy.
   * @param h Exponential family hierarchy.
   * @return True if h has the same prior and data sufficient statistics.
   */
  public boolean equals(XHierarchy h);
}
