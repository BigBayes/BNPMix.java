/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mixture;

import java.io.IOException;
import java.io.PrintStream;
import mcmc.collectors.Collector;
import mixture.Mixture;

/**
 *
 * @author ywteh
 */
public class MixtureDataFiler implements Collector {
	Mixture client;
	PrintStream data;

	/**
	 * Constructs a mixture filing collector.
	 * Writes a space separated line of statistics each time it collects.
	 * @parameter client_ A mixture model.
	 * @parameter filename The name of file to which output is to be written.
	 * @parameter properties_ List of properties to collect.
	 * @parameter args_ Arguments of properties.
	 */
  public MixtureDataFiler(Mixture client_, String datafile) throws IOException {
	client = client_;
	data = new PrintStream(datafile);
  }

  @Override public void collect() {
    for ( int i=0; i< client.numData(); i++) {
      Object d = client.getDatum(i);
      data.print(d.toString()+" ");
    }
    data.println();
  }
  @Override public void flush() {
    data.flush();
  }
  @Override public void finish() {
    data.close();
  }
}
