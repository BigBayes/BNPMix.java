package mcmc.collectors;

import mcmc.Predictable;
import java.util.ArrayList;

/**
 * A collector for the log predictive probability of a model evaluated at a fixed grid of
 * test points.
 * @author ywteh
 *
 * @param <D> Type of test points.
 */
public class LogPredictiveCollector<D> implements Collector {
	Predictable<D> model;
	ArrayList<D> tests;
	ArrayList<GeometricMeanCollector> collectors;
	/**
	 * Constructor for a log predictive collector.
	 * @param model The model.  Assume this is a Predictable<D>, with a "logPredictive" property
	 * 				taking a test point as argument.
	 * @param tests List of test points.
	 */
	public LogPredictiveCollector(Predictable<D> model, ArrayList<D> tests) {
		this.tests = tests;
		collectors = new ArrayList<GeometricMeanCollector>();
		for ( D datum : tests )
			collectors.add(new GeometricMeanCollector(model,"logPredictive",datum));
	}
	@Override public void collect() {
		for ( int i=0; i<tests.size(); i++ ) {
			collectors.get(i).collect();
		}
	}
	public ArrayList<Double> getLogPredictive() {
		ArrayList<Double> result = new ArrayList<Double>();
		for ( GeometricMeanCollector collector : collectors )
			result.add(collector.getGeometricMean());
		return result;
	}
  @Override public void flush() {}
	public void finish() {}
}
