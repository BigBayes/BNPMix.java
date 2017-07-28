package xfamily;

import mcmc.Collectable;

/**
 * Interface for an exponential family distribution.
 * An object of this type denotes a distribution from which iid draws may be observed.
 * 
 * @author Yee Whye Teh <mailto:ywteh@gatsby.ucl.ac.uk>
 *
 * @param <D> the base type over which the distribution is defined.
 */
public interface XFamily<D> extends Collectable {
  public int numDataDim();
  public int numParamDim();

  /**
	 * Returns the log probability of datum under the distribution.
	 * 
	 * @param datum	The data item.
	 * @return	The log probability of datum.
	 */
  public double logProbability(D datum);
  
  /**
   * Returns the log normalization constant.
   * @return The log normalization constant.
   */
	public double logNormalizer();

  /**
   * Returns the mean parameter.
   * @return The mean parameter.
   */
	public Double[] getMeanParameter();

  /**
   * Returns a sample drawn from the distribution.
   * @return A sample drawn from the distribution.
   */
	public D drawSample();
}
