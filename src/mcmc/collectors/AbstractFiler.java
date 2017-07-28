package mcmc.collectors;

import mcmc.Sampleable;
import java.io.*;

/**
 * A collector that simply writes statistics to file, one set per line.
 * @author ywteh
 *
 */
public abstract class AbstractFiler implements Collector {
	Sampleable client;
	PrintStream output;
  boolean toclose;

	/**
	 * Constructs a filing collector.
	 * Writes a space separated line of statistics to a PrintStream each time it collects.
	 * @param model A sampleable model.
	 * @param stream The output print stream.
	 * @param properties List of properties to collect.
	 * @param args Arguments of properties. Use null as argument if property has no argument.
	 */
	public AbstractFiler(Sampleable model, PrintStream stream) {
    client = model;
    output = stream;
    toclose = false;
  }
	/**
	 * Constructs a filing collector.
	 * Writes a space separated line of statistics to file each time it collects.
	 * @param model A sampleable model.
	 * @param filename The name of file to which output is to be written.
	 * @param properties List of properties to collect.
	 * @param args Arguments of properties.
	 */
  public AbstractFiler(Sampleable model, String filename) throws IOException {
    client = model;
    output = new PrintStream(filename);
    toclose = true;
  }

  @Override public void flush() {
    output.flush();
  }
  @Override public void finish() {
    if (toclose) output.close();
  }
}
