package mixture;

import java.io.*;
import java.util.*;

import xfamily.*;

import mcmc.collectors.*;


/**
 * A collector that writes cluster parameters and assignments to file, one per line.
 * @author ywteh
 *
 */
public class MixtureClusterFiler implements Collector {
	Mixture client;
	PrintStream clusters;
	PrintStream assignments;

	/**
	 * Constructs a mixture filing collector.
	 * Writes a space separated line of statistics each time it collects.
	 * @parameter client_ A mixture model.
	 * @parameter filename The name of file to which output is to be written.
	 * @parameter properties_ List of properties to collect.
	 * @parameter args_ Arguments of properties.
	 */
  public MixtureClusterFiler(Mixture client_, String clusterfile, String assignmentfile) throws IOException {
	client = client_;
	clusters = new PrintStream(clusterfile);
	assignments = new PrintStream(assignmentfile);
  }
  @Override public void collect() {
	  ArrayList<Cluster<XHierarchy>> map = client.map;
	  HashMap<Cluster,Integer> cid = new HashMap<Cluster,Integer>();
	  int maxid = 0;
	  for ( int i = 0 ; i < map.size(); i++ ) {
		  Cluster<XHierarchy> cc = map.get(i);
		  Integer id;
		  if (cid.containsKey(cc)) id = cid.get(cc);
		  else {
			  id = maxid++;
			  cid.put(cc, id);
			  XHierarchy hh = cc.parameter;
			  XFamily pp = hh.getParameter();
			  clusters.print(pp.toString()+" ");
		  }
		  assignments.print(id.toString()+" ");
	  }
	  clusters.println();
	  assignments.println();
  }
  @Override public void flush() {
    clusters.flush();
    assignments.flush();
  }
  @Override public void finish() {
    clusters.close();
    assignments.close();
  }
}
