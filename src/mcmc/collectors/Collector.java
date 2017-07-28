package mcmc.collectors;

/**
 * Interface for a class for collecting statistics from a sequence of samples.
 * @author ywteh
 *
 */
public interface Collector {
	/**
	 * Collect statistics.
	 */
	public void collect();
  /**
   * Flush out any bufferred outputs.
   */
  public void flush();
	/**
	 * Finish collecting.
	 */
	public void finish();
}
