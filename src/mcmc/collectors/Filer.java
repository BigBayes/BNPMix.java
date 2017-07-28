package mcmc.collectors;

import mcmc.Sampleable;
import java.io.*;

/**
 * A collector that simply writes statistics to file, one set per line.
 * @author ywteh
 *
 */
public class Filer extends AbstractFiler {
	String[] properties;
	Object[] args;

	/**
	 * Constructs a filing collector.
	 * Writes a space separated line of statistics to a PrintStream each time it collects.
	 * @param model A sampleable model.
	 * @param stream The output print stream.
	 * @param properties List of properties to collect.
	 * @param args Arguments of properties. Use null as argument if property has no argument.
	 */
	public Filer(Sampleable model, PrintStream stream,
          String[] properties, Object[] args) {
    super(model,stream);
    this.properties = properties;
    this.args = args;
    assert properties.length == args.length;
  }
	/**
	 * Constructs a filing collector.
	 * Writes a space separated line of statistics to file each time it collects.
	 * @param model A sampleable model.
	 * @param filename The name of file to which output is to be written.
	 * @param properties List of properties to collect.
	 * @param args Arguments of properties.
	 */
  public Filer(Sampleable model, String filename,
          String[] properties, Object[] args) throws IOException {
    super(model,filename);
    this.properties = properties;
    this.args = args;
    assert args==null || properties.length == args.length;
  }
  @Override public void collect() {
    int len = properties.length;
    for ( int ii = 0 ; ii < len ; ii ++ ) {
      Object sample;
      if ( args==null || args[ii] == null ) {
        sample = client.get(properties[ii]);
      } else {
        sample = client.get(properties[ii],args[ii]);
      }
      if (sample instanceof Object[]) {
    	  Object[] samplearray = (Object[]) sample;
    	  for ( int i = 0 ; i < samplearray.length ; i ++ ) 
    		  output.print(samplearray[i].toString()+" ");
      } else
    	  output.print(sample.toString()+" ");
    }
    output.println();
    output.flush();
  }

  @Override public void flush() {
    output.flush();
  }
  @Override public void finish() {
    if (toclose) output.close();
  }
}
