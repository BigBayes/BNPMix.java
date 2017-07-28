package mcmc.collectors;

import mcmc.Sampleable;

/**
 * An abstract collector for summary statistics obtained from a sequence of
 * samples.  Given statistics d_1,...,d_n of n samples, keeps track of
 * s = sum_i=1^n d_i.
 *
 * @author ywteh
 *
 * @param <S> Type of summary statistics.
 * @param <D> Type of sample statistics.
 */
public abstract class StatsCollector<S,D> implements Collector {
	Sampleable model;
	String property;
	Object argument;
	int number;
	S stats;
  /**
   * @param model A sampleable model.
   * @param property The property to be collected.
   * @param argument Argument.
   * @param initSummary Initial value of summary statistics.
   */
	public StatsCollector(Sampleable model, String property, Object argument, S initSummary) {
		number = 0;
		this.model = model;
		this.property = property;
		this.argument = argument;
		this.stats = initSummary;
	}
  /**
   * Abstract method returning summary + sample.
   * @param summary Current summary statistics.
   * @param sample New sample statistics.
   * @return summary + sample.
   */
	abstract S add(S summary, D sample);

	public void collect() {
		number += 1;
		stats = add(stats, (D) model.get(property, argument));
	}
  /**
   * Returns current value of summary statistics.
   */
	public S getSummaryStatistics() {
		return stats;
	}
  /**
   * Returns number of samples collected.
   */
	public int getNumber() {
		return number;
	}

  @Override public void flush() {}
  @Override public void finish() {}

}
