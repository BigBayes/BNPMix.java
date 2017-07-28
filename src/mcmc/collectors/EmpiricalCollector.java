package mcmc.collectors;

import mcmc.Sampleable;
import java.util.ArrayList;

/**
 * A collector that simply stores a list of the statistics it has collected.
 * @author ywteh
 *
 * @param <D>
 */
public class EmpiricalCollector<D> implements Collector {
	Sampleable model;
	ArrayList<D> data;
	String property;
	Object argument;
	/**
	 * Constructs a collector for the statistics of a model given by model.get(property,argument)
	 * @param model A sampleable model.
	 * @param property The property of the model we are collecting.
	 * @param argument Argument for the property.
	 */
	public EmpiricalCollector(Sampleable model, String property, Object argument) {
		data = new ArrayList<D>();
		this.model = model;
		this.property = property;
		this.argument = argument;
	}
	@Override public void collect() {
		D datum = (D) model.get(property, argument);
		data.add(datum);
	}
	/**
	 * @return list of collected statistics
	 */
	public ArrayList<D> getData() {
		return data;
	}
  @Override public void flush() {}
	@Override public void finish() {}
}
