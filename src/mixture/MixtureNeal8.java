package mixture;

import java.util.ArrayList;

import nrmi.NRMI;

import utilities.Factory;
import xfamily.*;
import static java.lang.Math.log;
import static java.lang.Math.exp;
import static utilities.Generator.generator;

public class MixtureNeal8<
        Data,
        Parameter extends XFamily<Data>,
        Prior extends XPrior<Parameter>,
        Hierarchy extends XHierarchy<Data,Parameter,Prior>>
		extends Mixture<Data,Parameter,Prior,Hierarchy> {

	int numNewClusters;
  ArrayList<Cluster<Hierarchy>> empties;
	
	public MixtureNeal8(NRMI nrmi, Prior prior, Factory<Hierarchy,Prior> factory,
			int numNewClusters) {
		super(nrmi,prior,factory);
		if (numNewClusters<1) throw new Error("numNewClusters < 1.");
		this.numNewClusters = numNewClusters;
    this.empties = new ArrayList<Cluster<Hierarchy>>(numNewClusters);
		for ( int i=0 ; i<numNewClusters ; i++ ) {
			Cluster<Hierarchy> cc = newCluster(0.0);
			empties.add(cc);
		}
	}
	@Override public int numEmptyClusters() {
		return numNewClusters;
	}

	@Override public void addData(Data[] traindata) {
		super.addData(traindata);
    sampleAssignments();
	}
	

	@Override public void sampleAssignments() {
		int numdata = numData();
    for ( Cluster<Hierarchy> cc : clusters ) {
      cc.logmass = nrmi.logMeanMass(cc.number);
    }
    for (int i=0; i<numNewClusters; i++) {
      Cluster<Hierarchy> cc = empties.get(i);
      cc.logmass = Double.NaN;
      clusters.add(cc);
    }
		for (int i=0; i<numdata; i++) {
			sampleAssignment(i);
		}
    for (int i=0; i<numNewClusters; i++) {
      Cluster<Hierarchy> cc = empties.get(i);
      assert cc.isEmpty();
      clusters.remove(cc);
    }
	}


	@Override void sampleAssignment(int index) {
		Data datum = getDatum(index);
		Cluster<Hierarchy> tt = unassign(index,datum);

    // initialize empty clusters
    Cluster<Hierarchy> new0 = empties.get(0);
		if (!tt.isEmpty()) {
      tt.logmass = nrmi.logMeanMass(tt.number);
			new0.parameter.sample();
		} else {
      clusters.remove(tt);
      factory.destruct(prior, new0.parameter);
      new0.parameter = tt.parameter;
    }
 		for ( int i=1 ; i<numNewClusters ; i++ )
			empties.get(i).parameter.sample();

    // compute probabilities
    double m = Double.NEGATIVE_INFINITY;
    double ew = nrmi.logMeanTotalMass(clusters.size()-numNewClusters)
            -log((double)numNewClusters);
		for ( Cluster<Hierarchy> cc : clusters ) {
      double cm = cc.logmass;
      if (Double.isNaN(cm)) {
  			cc.w = ew;
      } else {
        cc.w = cm;
      }
      cc.w += cc.parameter.logPredictive(datum);
			if (m<cc.w) m = cc.w;
		}
		double s = 0.0;
    //System.out.println("===========================");
    //System.out.println("datum: "+datum);
		for ( Cluster<Hierarchy> cc : clusters ) {
			s += cc.w = exp(cc.w-m);
 //     System.out.println("cluster: "+cc.parameter.getParameter());
 //     System.out.println("w= "+cc.w);
    }

    // sample assignment
    double r = generator.nextUniform(s);
		for ( Cluster<Hierarchy> cc : clusters ) {
			r -= cc.w;
			if (r<=0.0) {
        if (!cc.isEmpty()) {
    			assign(index,datum,cc);
          cc.logmass = nrmi.logMeanMass(cc.number);
        } else {
          // create new cluster and copy parameter over.
          tt = newCluster(nrmi.logMeanMass(1));
          Hierarchy param = tt.parameter;
          tt.parameter = cc.parameter;
          cc.parameter = param;
          clusters.add(tt);
          assign(index,datum,tt);
  			}
       //System.out.println("picked: "+cc.parameter.getParameter());
       break;
      }
    }
		assert r<=0.0;
	}
}
