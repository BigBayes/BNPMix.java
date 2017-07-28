/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nrmi;

import static java.lang.Math.log;
import static java.lang.Math.exp;
import static java.lang.Math.pow;
import static java.lang.Math.max;
import static java.lang.Math.log1p;
import static java.lang.Math.expm1;
import java.util.Collection;
import mcmc.kernel.SliceFiniteInterval;
import mcmc.kernel.SliceStepOut;
import mixture.Cluster;
import static utilities.Generator.generator;
import utilities.SpecialFunctions;
import static utilities.SpecialFunctions.logsumexp;

/**
 *
 * Pitman-Yor mixture model implementation using QGGP framework.
 * auxiliary variables marginalized out
 * @author ywteh
 */
public class QPYP_marg extends QPYP_VZ {

  public QPYP_marg(double Salpha, double Sbeta, double Tshape, double Tinvscale) {
    this(Salpha,Sbeta,Tshape,Tinvscale,default_minslice,default_maxclusters);
  }
  public QPYP_marg(double Salpha, double Sbeta, double Tshape, double Tinvscale,
          double minslice, double maxclusters) {
    super(Salpha,Sbeta,Tshape,Tinvscale,minslice,maxclusters);
  }
  
  @Override double logMeanMass(int num, double u) {
    return log((double)num-sigma);
  }
  @Override double logMeanTotalMass(int numclusters, double u) {
    return log(theta + sigma * (double)numclusters);
  }
  
	@Override public <H extends Cluster> void sample(int numData, Collection<H> clusters) {
		sigmamargsampler.sample(clusters);
    //System.out.println("sigma = "+sigma);
    thetamargslicer.sample(clusters);
    //System.out.println("theta = "+theta);
    
    double a = generator.nextLogGamma(numData);
    double b = generator.nextLogGamma(theta);
    double ab = logsumexp(a,b);
    double g = generator.nextGamma(theta/sigma+clusters.size());
    logU = log(g)/sigma+a-ab;
    logtau = log(g)/sigma+b-ab;
    /*if (logtau>1e3) {
      System.out.println("theta="+theta+" sigma="+sigma+" K="+clusters.size());
      System.out.println("log(g)="+log(g)+" a="+a+" b="+b);
      System.out.println("log(u)="+logU+" log(tau)="+logtau);
    }*/
    
    //System.out.println("logU="+logU);
    //System.out.println("logTau="+logtau);
    
    //double z = generator.nextBeta(numData, theta);
    //double g = generator.nextGamma(theta/sigma+clusters.size());
    //logU = log(g)/sigma+log(z);
    //logtau = log(g)/sigma+log(1.0-z);

    //if (logU>100) {
    //  System.out.println("oh no");
    //}
    //sampleU(numData,clusters);
    //System.out.println("sigma="+sigma+" g="+g+" logU="+logU+" tau="+tau);
	}
  
  class SigmaSampler<C extends Cluster<?>> extends SliceFiniteInterval {
		Collection<C> clusters = null;
		public SigmaSampler() {
			super(0.0,1.0,generator);
		}
		public void sample(Collection<C> clusters) {
			this.clusters = clusters;
			sigma = sample(sigma);
		}
		@Override public double logDensity(double x) {
			double result = 
					+ (Salpha-1.0)*log(max(1e-16,x))
					+ (Sbeta-1.0)*log(max(1e-16,1.0-x))
          + (clusters.size()-1)*log(x)
          + SpecialFunctions.logGamma(theta/x+clusters.size())
          - SpecialFunctions.logGamma(theta/x+1);
			for ( C cc : clusters ) {
        assert !cc.isEmpty();
				if (!cc.isEmpty()) {
					result += SpecialFunctions.logGamma(cc.number - x)
                  - SpecialFunctions.logGamma(1 - x);
        }
			}
			return result;
		}		
	}
	QPYP_marg.SigmaSampler sigmamargsampler = new QPYP_marg.SigmaSampler();
  
	class ThetaMargSlicer extends SliceStepOut {
		Collection<? extends Cluster> clusters;
    int numData;
    public ThetaMargSlicer() {
      super(2.0,20,generator);
    }
		<H extends Cluster> void sample(Collection<H> clusters) {
			this.clusters = clusters;
      numData = 0;
      for ( Cluster cc : clusters ) {
        numData += cc.number;
      }
			theta = exp(sample(log(theta)));
		}
		@Override public double logDensity(double x) {
      double expx = exp(x);
			double result = 
            Tshape*x
          - Tinvscale*expx
          + SpecialFunctions.logGamma(expx/sigma+clusters.size())
          - SpecialFunctions.logGamma(expx/sigma+1.0)
          + SpecialFunctions.logGamma(expx+1.0)
          - SpecialFunctions.logGamma(expx+numData);          
			return result;
		}
	}
	ThetaMargSlicer thetamargslicer = new ThetaMargSlicer();
}
