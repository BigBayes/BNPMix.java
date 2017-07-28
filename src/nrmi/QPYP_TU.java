/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nrmi;

import utilities.SpecialFunctions;
import static java.lang.Math.pow;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import java.util.Collection;
import mcmc.kernel.SliceStepOut;
import mixture.Cluster;
import static utilities.Generator.generator;

/**
 * Pitman-Yor mixture model implementation using QGGP framework.
 * Basic method of sampling U and Tau used here.
 * No updates to theta.
 * @author ywteh
 */
public class QPYP_TU extends QGGP {
  double Tshape, Tinvscale;
  double theta;

  public QPYP_TU(double Salpha, double Sbeta, double Tshape, double Tinvscale) {
    this(Salpha,Sbeta,Tshape,Tinvscale,default_minslice,default_maxclusters);
  }
  public QPYP_TU(double Salpha, double Sbeta, double Tshape, double Tinvscale,
          double minslice, double maxclusters) {
    super(Salpha,Sbeta,minslice,maxclusters);
    this.Tshape = Tshape;
    this.Tinvscale = Tinvscale;
    this.theta = Tshape/Tinvscale;
  }
  @Override
  double F(double sigma, double logtau) {
    return exp(logF(sigma,logtau));
  }

  @Override
  double logF(double sigma, double logtau) {
    return logF(sigma,theta,logtau);
  }
  double logF(double sigma, double theta, double logtau) {
    return log(sigma) - SpecialFunctions.logGamma(theta/sigma)
            + (theta)*logtau - exp(sigma*logtau);
  }
  
  
	public double getTheta() { return theta; }
  
	@Override public <H extends Cluster> void sample(int numData, Collection<H> clusters) {
    //super.sample(numData, clusters);
		sigmasampler.sample(clusters);
    thetaslicer.sample(clusters);
    sampleU(numData,clusters);
    tauslicer.sample(clusters);
  }
  
	class ThetaSlicer extends SliceStepOut {
		Collection<? extends Cluster> clusters;
    public ThetaSlicer() {
      super(2.0,20,generator);
    }
		<H extends Cluster> void sample(Collection<H> clusters) {
			this.clusters = clusters;
			theta = exp(sample(log(theta)));
		}
		@Override public double logDensity(double x) {
			double result = 
            Tshape*x
          - Tinvscale*exp(x)
          + logF(sigma,exp(x),logtau);
			return result;
		}
	}
	ThetaSlicer thetaslicer = new ThetaSlicer();
  
  
}
