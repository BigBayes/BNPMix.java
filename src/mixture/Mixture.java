package mixture;


import xfamily.Normal.NormalConjugateFactorySampled;
import xfamily.Normal.NormalGamma;
import xfamily.Normal.NormalConjugateHierarchy;
import xfamily.Normal.NormalConjugateFactory;
import xfamily.Normal.Normal;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.ceil;
import static java.lang.Math.min;
import java.util.Arrays;
import static utilities.DataIO.vlogln;
import static utilities.Generator.generator;
import static utilities.SpecialFunctions.logGamma;
import static utilities.SpecialFunctions.logsumexp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import nrmi.NGGP;
import nrmi.QGGP;
import nrmi.QPYP_TU;
import nrmi.NRMI;

import utilities.Factory;
import xfamily.*;

import mcmc.kernel.Metropolis;
import mcmc.Predictable;
import mcmc.Sampleable;


/**
 * Abstract class for a normalized random measure mixture model.
 *
 * An object of this type denotes a distribution from which iid draws may be 
 * observed, each of which comes from one of a countable number of mixture
 * components.
 *
 * The mixture model has a prior given by a normalized random measure with
 * independent increments.
 * 
 * @author Yee Whye Teh <mailto:ywteh@gatsby.ucl.ac.uk>
 *
 * @parameter <D> the base type over which the distribution is defined.
 * @parameter <F> Exponential family of component distributions.
 * @parameter <P> Exponential family of prior over component parameters.
 * @parameter <H> Exponential family hierarchy.
 *
 */
public abstract class Mixture
        <Data,
         Parameter extends XFamily<Data>,
         Prior extends XPrior<Parameter>,
         Hierarchy extends XHierarchy<Data,Parameter,Prior>>
	implements Predictable<Data>, Sampleable {

  static int numPredictiveClusters = 10;
	Prior prior;
	Factory<Hierarchy,Prior> factory;
	NRMI nrmi;
	//double U = 1.0;	

	ArrayList<Data> data;
	ArrayList<Cluster<Hierarchy>> map;
	Collection<Cluster<Hierarchy>> clusters;

  static double numClustersRatio = 5.0;
	
	/**
	 * Constructor for mixture object.
	 * 
	 * @parameter nrmi Normalized random measure.
	 * @parameter prior Base distribution (prior over component parameters).
	 * @parameter factory Factory object to generate component parameters from prior.
	 */
	public Mixture(NRMI nrmi, Prior prior, Factory<Hierarchy,Prior> factory) {
		this.nrmi = nrmi;
		this.prior = prior;
		this.factory = factory;
		data = new ArrayList<Data>();
		map = new ArrayList<Cluster<Hierarchy>>();
		clusters = new HashSet<Cluster<Hierarchy>>();
	}
	
		
	/**
	 * Returns the number of data items in training set drawn from the mixture model.
	 * 
	 * @return 	The number of observations.
	 */
	public int numData() { 
		return data.size(); 
	}

	/**
	 * Returns the number of non-empty components in the mixture model.
	 * 
	 * @return 	The number of components.
	 */
	public int numClusters() { 
		return clusters.size(); 
	}

	/**
	 * Adds data item as an iid observation drawn from the mixture model.
	 * 
	 * @parameter datum	The data item.
	 */
	public void add(Data datum) {
		Cluster<Hierarchy> cc = newCluster(nrmi.logMeanMass(1));
		clusters.add(cc);
		map.add(null);
		data.add(datum);
		assign(data.size()-1,datum,cc);
	}

  /**
	 * Adds data items into mixture model.
	 * @parameter traindata Collection of data items.
	 */
	public void addData(Data[] traindata) {
		int numdata = traindata.length;
    data.addAll(Arrays.asList(traindata));
		int numclusters = min(numdata,(int)(numClustersRatio*ceil(nrmi.meanNumClusters(numdata))));
		ArrayList<Cluster<Hierarchy>> clusterlist = new ArrayList<Cluster<Hierarchy>>();
		for ( int i = 0 ; i < numclusters ; i++ ) {
			Cluster<Hierarchy> cc = newCluster(nrmi.logMeanMass(1));
			clusterlist.add(cc);
			clusters.add(cc);
			map.add(null);
			assign(i,data.get(i),cc);
		}
		for ( int i = numclusters ; i < numdata ; i++ ) {
			Cluster<Hierarchy> cc = clusterlist.get(generator.nextUniform(numclusters));
			map.add(null);
			assign(i,data.get(i),cc);
		}
	}

	/**
	 * Removes datum as an iid observation drawn from the mixture model.
	 * 
	 * @parameter datum	The data item.
	 */
	public boolean remove(Data datum) {
		int i = data.lastIndexOf(datum);
		if (i==-1) return false;
		remove(i,datum);
		return true;
	}
	/**
	 * Removes datum number i from mixture model.
	 * @parameter i Index of datum.
	 */
	void remove(int i) {
		Data datum = getDatum(i);
		remove(i,datum);
	}
	/**
	 * Removes datum at index i from mixture model.
	 * @parameter i Index of datum.
	 * @parameter datum The datum itself.
	 */
	void remove(int i, Data datum) {
		unassign(i,datum);
		data.remove(i);
		map.remove(i);
	}
	/**
	 * Returns datum in mixture model.
	 * @parameter i The index of datum returned.
	 * @return The datum.
	 */
	public Data getDatum(int i) {
		assert i>=0 && i<data.size();
		return data.get(i);
	}
	/**
	 * Returns the cluster in which is the ith datum.
	 * @parameter i The index of datum.
	 * @return The cluster.
	 */
	public Cluster<Hierarchy> getCluster(int i) {
		assert i>=0 && i<map.size();
		return map.get(i);
	}
	
	/**
	 * Assigns datum to cluster.
	 * Assumes that datum is currently not assigned to any other cluster.
	 * @parameter i The index of the datum.
	 * @parameter datum The datum.
	 * @parameter cc The cluster.
	 */
	void assign(int i, Data datum, Cluster<Hierarchy> cc) {
    assert map.size()>i || map.get(i)==null;
		assert datum.equals(getDatum(i));
		if (datum!=null) cc.parameter.addDatum(datum);
		cc.number ++;
		map.set(i, cc);
	}
	/**
	 * Removes datum from the cluster it currently belongs to.
	 * @parameter i the index of the datum.
	 * @parameter datum The datum.
	 * @return The cluster which datum currently belongs to.
	 */
	Cluster<Hierarchy> unassign(int i, Data datum) {
		assert (datum==null&&getDatum(i)==null) || datum.equals(getDatum(i));
		assert i>=0 && i<map.size();
		Cluster<Hierarchy> cc = map.get(i);
		map.set(i, null);
		assert cc!=null;
    if (cc.parameter==null)
      System.out.println("cc parameter null");
		if (datum!=null) cc.parameter.removeDatum(datum);
		cc.number --;
		assert cc.number>=0;
		return cc;
	}

	/**
	 * Returns a new cluster with given (unnormalized) mass.
	 * @parameter mass The mass of the new cluster.
	 * @return The cluster.
	 */
	Cluster<Hierarchy> newCluster(double logmass) {
		return newCluster(logmass,factory.construct(prior));
	}
	/**
	 * Returns a new cluster with given (unnormalized) mass.
	 * @parameter mass The mass of the new cluster.
	 * @return The cluster.
	 */
	Cluster<Hierarchy> newCluster(double logmass, Hierarchy h) {
		return new Cluster<Hierarchy>(0,logmass,h);
	}

	/**
	 * Returns the log joint probability of observations and parameters of the mixture model.
	 * @return	The log joint probability.
	 */
	public double logJoint() {
		double numdata = numData();
		double result =
				  (numdata-1)*nrmi.getLogU()
				- logGamma(numdata)
				- nrmi.laplace();
		for ( Cluster<Hierarchy> cc : clusters ) {
      result += nrmi.logGamma(cc.number) + cc.parameter.logJoint();
    }
		return result;
	}

	/**
	 * Returns the log probability of datum conditioned on the other observations and on the
	 * parameters of the mixture model.
	 * 
	 * @parameter datum	The data item.
	 * @return 	The log probability of datum.
	 */
  @Override public double logPredictive(Data datum) {
		double logpred = Double.NEGATIVE_INFINITY;
		for ( int ii = 0 ; ii < numPredictiveClusters ; ii ++ ) {
      Hierarchy hh = factory.construct(prior);
			logpred = logsumexp(logpred,hh.logPredictive(datum));
		}
		double logmixprop = nrmi.logMeanTotalMass(clusters.size());
		logpred += logmixprop - log(numPredictiveClusters);
		for ( Cluster<Hierarchy> cc : clusters ) {
			logmixprop = logsumexp(logmixprop,cc.logmass = nrmi.logMeanMass(cc.number));
			logpred = logsumexp(logpred,cc.logmass+cc.parameter.logPredictive(datum));
		}
		return logpred - logmixprop;
	}

	
	/**
	 * Returns the log probability of data items conditioned on the observations and on the
	 * parameters of the mixture model.
	 * 
	 * @parameter data	The data items.
	 * @return 	The log probability of datum.
	 */
	public Double[] logPredictive(Data[] data) {
		int numdata = data.length;
		double lognewmass = nrmi.logMeanTotalMass(clusters.size());
		double logmixprop = lognewmass;
		for ( Cluster<Hierarchy> cc : clusters )
			logmixprop = logsumexp(logmixprop,cc.logmass = nrmi.logMeanMass(cc.number));
    ArrayList<Cluster<Hierarchy>> empties = new ArrayList<Cluster<Hierarchy>>(numPredictiveClusters);
		for ( int ii = 0 ; ii < numPredictiveClusters ; ii ++ ) {
      Cluster<Hierarchy> cc = newCluster(lognewmass-log((double)numPredictiveClusters));
      empties.add(cc);
      clusters.add(cc);
		}
		Double[] logpred = new Double[numdata];
		for ( int i = 0 ; i < numdata ; i ++ ) {
			double lp = Double.NEGATIVE_INFINITY;
			for ( Cluster<Hierarchy> cc : clusters )
				lp = logsumexp(lp,cc.logmass+cc.parameter.logPredictive(data[i]));
			logpred[i] = lp - logmixprop;
		}
    for ( Cluster<Hierarchy> cc : empties )
      clusters.remove(cc);
		return logpred;
	}

  /**
   * Initializes MCMC sampler.
   */
	@Override public void initializeSampler() {
		for ( int i=0; i<10; i++ ) {
			sampleClusters();
			sampleAssignments();
		}
	}

  /**
   * Finishes MCMC sampler.
   */
	@Override public void finishSampler() {}

	/**
	 * MCMC samples variables associated with this datum in the mixture model.
	 * 
	 * @parameter datum The data item.
	 */
	@Override public void sample() {
		sampleClusters();
		sampleAssignments();
		sampleNRMIParameters();
		sampleClusterHyperparameters();
	}
	
	/**
	 * Samples parameters of the NRMI prior.
	 */
	void sampleNRMIParameters() {
		nrmi.sample(numData(), clusters);
		vlogln(1,"-------------- sampleNRMIParameters");
		vlogln(1,""+nrmi);
	}
	
	/**
	 * Samples assignments of data items to clusters.
	 */
	abstract void sampleAssignments();
	
	/**
	 * Samples assignment of data item to clusters.
	 * @parameter i Index of data item.
	 */
	abstract void sampleAssignment(int i);
	
	/**
	 * Samples the cluster masses and parameters.
	 */
  void sampleClusters() {
		for ( Cluster<Hierarchy> cc : clusters) {
			cc.parameter.sample();
		}
	}
	
	/**
	 * Samples the hyperparameters of the cluster parameter prior.
	 */
	void sampleClusterHyperparameters() {

    HashSet<Hierarchy> cdata = new HashSet<Hierarchy>();
    for ( Cluster<Hierarchy> cluster : clusters ) {
      cdata.add(cluster.parameter);
    }
    prior.sample(cdata);

  }



	/**
	 * Returns partition of data items.  Each cluster is given a unique integer index in no
	 * particular order.
	 * @return ArrayList of cluster indices, one for each data item.
	 */
	public ArrayList<Integer> getPartition() {
		HashMap<Cluster<Hierarchy>,Integer> cmap = new HashMap<Cluster<Hierarchy>,Integer>();
		int index = 0;
		for ( Cluster<Hierarchy> cc : clusters )
			cmap.put(cc,index++);
		ArrayList<Integer> result = new ArrayList<Integer>();
		for ( index = 0 ; index<numData(); index++ ) 
			result.add(cmap.get(map.get(index)));
		return result;
	}
	
	/**
	 * Returns collection of non-empty clusters in mixture model.
	 * @return Collection of clusters.
	 */
	public Collection<Cluster<Hierarchy>> getClusters() {
		return clusters;
	}
	
	/**
	 * Returns collection of non-empty clusters in mixture model.  
	 * Each cluster now given by instantiated parameters.
	 * @return Collection of clusters.
	 */
	public Collection<Cluster<Parameter>> getSampledClusters() {
		int numclusters = numClusters();
		ArrayList<Cluster<Parameter>> result = new ArrayList<Cluster<Parameter>>(numclusters);
		for ( Cluster<Hierarchy> cc : clusters ) {
			result.add(new Cluster<Parameter>(cc.number,cc.logmass,cc.parameter.getParameter()));
		}
		return result;
	}
	
	/**
	 * Returns number of empty clusters represented in mixture model.
	 * @return Number of empty clusters represented in mixture model.
	 */
	abstract int numEmptyClusters();

	
	public enum Properties {
		nggp_alpha, nggp_sigma, nggp_tau, nggp_logtau, 
    qggp_sigma, qggp_tau, qpyp_theta, qggp_logtau,
    logU, U,
		numClusters,
		numEmptyClusters,
		clusters,
		logPredictive,
		logPredictives,
		assignments,
    mean,
    degFreedom,
    invScale,
    relPrecision,
    meanMean,
    meanPrecision,
    precisionShape,
    precisionInvScale,
		NOVALUE;
		public static Properties toValue(String str) {
			try {
				return valueOf(str);
			} catch (Exception ex) {
				return NOVALUE;
			}
		}
	}
	@Override public Object get(String property) {
		switch(Properties.toValue(property)) {
		case nggp_alpha:	return ((NGGP)nrmi).getAlpha();
		case nggp_sigma:	return ((NGGP)nrmi).getSigma();
		case nggp_tau:	return ((NGGP)nrmi).getTau();
		case nggp_logtau:	return ((NGGP)nrmi).getLogTau();
		case qggp_sigma:	return ((QGGP)nrmi).getSigma();
		case qggp_tau:	return ((QGGP)nrmi).getTau();
		case qggp_logtau:	return ((QGGP)nrmi).getLogTau();
		case qpyp_theta:	return ((QPYP_TU)nrmi).getTheta();
		case U:	return nrmi.getU();
		case logU:	return nrmi.getLogU();
		case numClusters: return numClusters();
		case numEmptyClusters: return numEmptyClusters();
    case mean:
    case degFreedom:
    case invScale:
    case relPrecision:
    case meanMean:
    case meanPrecision:
    case precisionShape:
    case precisionInvScale:
         return prior.get(property);
		case clusters: 
			return getClusters();
		case assignments: return getPartition();
		case logPredictive:
			throw new Error("Property logPredictive 1 argument expected.");
		default: throw new Error("Unknown property "+property);
		}
	}
	
	@Override public Object get(String property, Object arg) {
		if (arg==null) return get(property);
		switch(Properties.toValue(property)) {
		case logPredictive:
			return logPredictive((Data) arg);
		case logPredictives:
			return logPredictive((Data[]) arg);
		default: throw new Error("Unknown property "+property);
		}
	}

	/**
	 * Displays the internal data structures of the mixture model.
	 */
	public void displayInnards() {
		System.out.println(this.getClass().getSimpleName());
		System.out.println("Clusters: #="+numClusters());
		for ( Cluster<Hierarchy> cc : clusters ) {
			System.out.println("s:"+cc.number+" lm:"+cc.logmass+" p:"+cc.parameter+" "+cc.parameter.getParameter());
		}
		System.out.println("Data: #="+numData());
		int numdata = numData();
		for ( int i=0; i<numdata; i++ ) {
			System.out.println(data.get(i)+": "+map.get(i).parameter);
		}	
	}

  public void verify() {
    boolean ok = true;
    HashMap<Cluster<Hierarchy>,Cluster<Hierarchy>> newclust =
            new HashMap<Cluster<Hierarchy>,Cluster<Hierarchy>>();
    for ( Cluster<Hierarchy> cc : clusters ) {
      ok &= cc.number>0;
      ok &= cc.number==cc.parameter.numDatum();
      newclust.put(cc, newCluster(0.0));
      if (!ok) {
        displayInnards();
        assert false;
      }
    }
    for ( int i=0; i<numData(); i++ ) {
      Cluster<Hierarchy> cc = getCluster(i);
      Cluster<Hierarchy> tt = newclust.get(cc);
      tt.number += 1;
      tt.parameter.addDatum(getDatum(i));
    }
    for ( Cluster<Hierarchy> cc : clusters ) {
      Cluster<Hierarchy> tt = newclust.get(cc);
      ok &= (tt.number==cc.number);
      ok &= (tt.parameter.equals(cc.parameter));
      if (!ok) {
        displayInnards();
        assert false;
      }
    }
  }


  void samplePrior() {
		sampleNRMIParameters();
		sampleClusterHyperparameters();
    for (int index=0; index<numData(); index++) {
      Data datum = getDatum(index);
      Cluster<Hierarchy> tt = unassign(index,datum);
      if (tt.isEmpty()) clusters.remove(tt);
      double s = exp(nrmi.logMeanTotalMass(clusters.size()));
      //System.out.println();
      for ( Cluster<Hierarchy> cc : clusters ) {
        assert !cc.isEmpty();
        s += exp(cc.logmass = nrmi.logMeanMass(cc.number));
        //System.out.println("w="+cc.mass);
      }
      //System.out.println("s="+s);
      double r = generator.nextUniform(s);
      for ( Cluster<Hierarchy> cc : clusters ) {
        r -= exp(cc.logmass);
        if (r<=0.0) {
        	assign(index,datum,cc);
         break;
        }
      }
      if (r>0.0) {
        tt = newCluster(nrmi.logMeanMass(1));
        clusters.add(tt);
        assign(index,datum,tt);
      }
    }
  }
  void sampleJoint() {
    //displayInnards();
    //verify();
    sample();
    //verify();

    for (int index=0; index<numData(); index++) {
      Cluster<Hierarchy> cc = getCluster(index);
      if (cc==null || cc.parameter==null) {
        System.out.println(cc);
        System.out.println(cc.parameter);
      }
      if (data.get(index)==null)
        System.out.println(data.get(index));

      cc.parameter.removeDatum(data.get(index));
      data.set(index, cc.parameter.drawDatum());
      cc.parameter.addDatum(data.get(index));
    }
    for (Cluster<Hierarchy> cc : clusters)
      cc.parameter.clearData();
    for (int index=0; index<numData(); index++) {
      Cluster<Hierarchy> cc = getCluster(index);
      cc.parameter.addDatum(data.get(index));
    }
    verify();
  }

  static public void main(String[] args) {
		int numNewClusters = 3;
		double alphaShape = 3;
		double alphaInvScale = 3;
		double sigmaAlpha = 1;
		double sigmaBeta = 10;
		double tauShape = 3;
		double tauInvScale = 3;

    double mean = 0.0;
		double degFreedom = 5.0;
		double invScale = 1;
		double relPrecision = .1;
    double meanscale = 1.0;

		NGGP nggp = new NGGP(alphaShape,alphaInvScale,sigmaAlpha,sigmaBeta,tauShape,tauInvScale);
		NormalGamma prior = new NormalGamma(mean,relPrecision,degFreedom,invScale,meanscale);
		NormalConjugateFactory factory = new NormalConjugateFactorySampled();

    /*
    NormalHierarchySampled h = factory.construct(prior);
    double[] data2 = new double[5];
    for (int i=0;i<5; i++) {
      data2[i] = h.drawDatum();
      h.addDatum(data2[i]);
    }
    for (int iter=0; iter<1000; iter++) {
      System.out.print(h+" ");
      for (int i=0;i<5; i++) {
        System.out.print(data2[i]+" ");
      }
      System.out.println();
      h.sample();
      h.clearData();
      for (int i=0;i<5; i++) {
        data2[i] = h.drawDatum();
        h.addDatum(data2[i]);
      }
    }
     */

    Mixture neal = new MixtureNeal8<Double,Normal,NormalGamma,NormalConjugateHierarchy>(
					nggp,prior,factory,numNewClusters);
		Mixture slice = new MixtureSlice<Double,Normal,NormalGamma,NormalConjugateHierarchy>(
					nggp,prior,factory);

    int numiter = 100000;
    Double[] data = new Double[30];
    for (int i=0; i<30; i++)
      data[i] = prior.drawSample().drawSample();
    neal.addData(data);
    slice.addData(data);

    double meanK;

    for (int i=0; i<1000; i++) {
      neal.samplePrior();
    }

    meanK = 0.0;
    for (int i=0; i<numiter; i++) {
      neal.samplePrior();
      meanK += neal.numClusters();
    }
    System.out.println("neal prior numclusters = "+(meanK/numiter));

    meanK = 0.0;
    for (int i=0; i<numiter; i++) {
      neal.sampleJoint();
      meanK += neal.numClusters();
    }
    System.out.println("neal joint numclusters = "+(meanK/numiter));
    
    for (int i=0; i<numiter; i++)
      slice.samplePrior();

    meanK = 0.0;
    for (int i=0; i<numiter; i++) {
      slice.sampleJoint();
      meanK += slice.numClusters();
    }
    System.out.println("slice joint numclusters = "+(meanK/numiter));
    meanK = 0.0;
    for (int i=0; i<numiter; i++) {
      slice.samplePrior();
      meanK += slice.numClusters();
    }
    System.out.println("slice prior numclusters = "+(meanK/numiter));
     
  }
}
