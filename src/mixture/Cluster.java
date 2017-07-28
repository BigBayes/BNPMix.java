package mixture;

import java.util.Comparator;

/**
 * A cluster (mixture component).  
 * 
 * @author ywteh
 *
 * @parameter <F> Type of the parameter.
 */
public class Cluster<Theta> {
  /**
   * Number of data items.
   */
	public int number;
  /**
   * Mass, or (possibly unnormalized) mixture proportion.
   */
	public double logmass;
  /**
   * Parameter
   */
	public Theta parameter;
	double w;

	/**
	 * Constructor for a cluster.
	 * @parameter number Number of data items assigned to cluster.
	 * @parameter mass Mass (mixture proportion) of cluster.
	 * @parameter parameter Parameter.
	 */
	Cluster(int number, double logmass, Theta param) {
		this.number = number;
		this.logmass = logmass;
		this.parameter = param;
	}
	/**
	 * 
	 * @return True if cluster has no assigned data items.
	 */
	public boolean isEmpty() { return number==0; }
	
	static class ClusterComparator implements Comparator<Cluster> {
		public int compare(Cluster c1, Cluster c2) {
			if (c1.logmass>c2.logmass) return -1;
			else if (c1.logmass<c2.logmass) return 1;
			else return 0;
		}
	}
  static ClusterComparator comparator = new ClusterComparator();
	
	public String toString() {
		return this.getClass().getSimpleName()+"(n="+number+",lm="+logmass+",p="+parameter+")";
	}
}
