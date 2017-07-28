package xfamily;

/**
 * Interface for an exponential family distribution with a conjugate prior.
 * An object of this type denotes a distribution from which iid draws may be observed.
 * 
 * @author Yee Whye Teh <mailto:ywteh@gatsby.ucl.ac.uk>
 *
 * @param <D> The base type over which the distribution is defined.
 * @param <F> The exponential family type.
 * @param <P> The conjugate exponential family prior type.
 */
public interface XHierarchyMarginalized<D,F extends XFamily<D>,P extends XPrior<F>>
	extends XHierarchy<D,F,P> {
  /**
   * Returns the posterior distribution over parameters of the exponential family.
   * @return The posterior distribution.
   */
	public P getPosterior();
  /**
   * Returns the posterior mean of exponential family natural parameters.
   * @return The posterior mean parameters.
   */
	public F getMean();
}
