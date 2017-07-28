package mixture;

import static java.lang.Math.exp;
import static java.lang.Math.min;
import static utilities.Generator.generator;

import java.util.ArrayList;
import java.util.Collections;

import nrmi.NRMI;

import utilities.Factory;
import xfamily.XFamily;
import xfamily.XHierarchy;
import xfamily.XPrior;


public class MixtureSlice<
        Data,
        Parameter extends XFamily<Data>,
        Prior extends XPrior<Parameter>,
        Hierarchy extends XHierarchy<Data,Parameter,Prior>>
		extends Mixture<Data,Parameter,Prior,Hierarchy> {

	double threshold = 1.0;
	double minMass = Double.POSITIVE_INFINITY;
	ArrayList<Double> slice;
	ArrayList<Cluster<Hierarchy>> clusterarray;
	
	public MixtureSlice(NRMI nrmi, Prior prior, Factory<Hierarchy,Prior> factory) {
		super(nrmi,prior,factory);
		slice = new ArrayList<Double>();
		clusterarray = new ArrayList<Cluster<Hierarchy>>();
	}
	@Override public void add(Data datum) {
		super.add(datum);
		slice.add(getCluster(numData()-1).logmass - generator.nextExponential());
	}
	@Override public void addData(Data[] traindata) {
		super.addData(traindata);
		int numdata = traindata.length;
		for ( int i = 0 ; i < numdata ; i ++ ) {
			slice.add(getCluster(i).logmass - generator.nextExponential());
		}
    // update partition structure variables, but not cluster parameters
		for ( Cluster<Hierarchy> cc : clusters) {
			cc.logmass = nrmi.drawLogMass(cc.number);
			//cc.parameter.sample();
		}

		minMass = Double.POSITIVE_INFINITY;
		for ( int i=0; i<numData(); i++ ) {
			double s = getCluster(i).logmass - generator.nextExponential();
      slice.set(i, s);
			minMass=min(minMass,s);
    }

    double[] masses = nrmi.drawLogMasses(minMass);
    //System.out.println("numnewclusters: "+masses.length);
    clusterarray.clear();
		clusterarray.addAll(clusters);
		for ( double mass : masses )
			clusterarray.add(newCluster(mass));
		Collections.sort(clusterarray, Cluster.comparator);
    sampleAssignments();
	}
	@Override void remove(int i, Data datum) {
		super.remove(i,datum);
		slice.remove(i);
	}
	@Override public int numEmptyClusters() {
		return clusterarray.size()-numClusters();
	}
		
	@Override void sampleClusters() {
		for ( Cluster<Hierarchy> cc : clusters) {
			cc.logmass = nrmi.drawLogMass(cc.number);
			cc.parameter.sample();
		}

		minMass = Double.POSITIVE_INFINITY;
		for ( int i=0; i<numData(); i++ ) {
			double s = getCluster(i).logmass - generator.nextExponential();
      slice.set(i, s);
			minMass=min(minMass,s);
    }

    double[] masses = nrmi.drawLogMasses(minMass);
    //System.out.println("numnewclusters: "+masses.length);
    clusterarray.clear();
		clusterarray.addAll(clusters);
		for ( double mass : masses )
			clusterarray.add(newCluster(mass));
		Collections.sort(clusterarray, Cluster.comparator);
	}

	@Override public void sampleAssignments() {
		int numdata = numData();
		for (int i=0; i<numdata; i++) {
			sampleAssignment(i);
		}
		clusters.clear();
    int num = 0;
		for ( Cluster<Hierarchy> cc : clusterarray ) {
			if (!cc.isEmpty()) {
        clusters.add(cc);
      } else {
        factory.destruct(prior, cc.parameter);
      }
      num += cc.number;
    }
    assert num==numData();
    //System.out.println(numClusters()+" "+prior.get("numClusters"));

	}
  
	@Override void sampleAssignment(int index) {
		Data datum = getDatum(index);
		unassign(index,datum);
		double thisslice = slice.get(index);
		for ( Cluster<Hierarchy> cc : clusterarray ) {
			if (cc.logmass<thisslice) break;
			cc.w = cc.parameter.logPredictive(datum);
		}
		double m = Double.NEGATIVE_INFINITY;
		for ( Cluster<Hierarchy> cc : clusterarray ) {
			if (cc.logmass<thisslice) break;
			if (m<cc.w) m = cc.w;
		}
		double s = 0.0;
		for ( Cluster<Hierarchy> cc : clusterarray ) {
			if (cc.logmass<thisslice) break;
			s += cc.w = exp(cc.w-m);
		}
		double r = generator.nextUniform(s);
		for ( Cluster<Hierarchy> cc : clusterarray ) {
			r -= cc.w;
			if (r<=0.0) {
				assign(index,datum,cc);
				break;
			}
		}
    assert r<=0.0;
	}
	
}
