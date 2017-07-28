package xfamily;

public interface XHierarchySampled<D,F extends XFamily<D>,P extends XPrior<F>>
	extends XHierarchy<D,F,P> {
  /**
   * Returns the current parameters of the exponential family.
   * @return The current parameters.
   */
  @Override F getParameter();
}
