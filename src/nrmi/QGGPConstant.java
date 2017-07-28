/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nrmi;

import utilities.SpecialFunctions;
import static java.lang.Math.pow;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.PI;
import java.util.Collection;
import mcmc.kernel.SliceStepOut;
import mixture.Cluster;
import utilities.DoubleComparator;

/**
 * Pitman-Yor mixture model implementation using QGGP framework.
 * Basic method of sampling U and Tau used here.
 * No updates to theta.
 * @author ywteh
 */
public class QGGPConstant extends QGGP {

  public QGGPConstant(double Salpha, double Sbeta) {
    this(Salpha,Sbeta,0.0,default_minslice,default_maxclusters);
  }
  public QGGPConstant(double Salpha, double Sbeta, double logtau) {
    this(Salpha,Sbeta,logtau,default_minslice,default_maxclusters);
  }
  public QGGPConstant(double Salpha, double Sbeta, double logtau,
          double minslice, double maxclusters) {
    super(Salpha,Sbeta,minslice,maxclusters);
    this.logtau = logtau;
    this.tauslicer = new TauDoNothing();
  }

	class TauDoNothing extends QGGP.TauSlicer {
    @Override
		<H extends Cluster> void sample(Collection<H> clusters) {
		}
  }
  
  @Override
  double F(double sigma, double logtau) {
    return exp(logF(sigma,logtau));
  }

  @Override
  double logF(double sigma, double logtau) {
    if (DoubleComparator.isEqual(this.logtau, logtau)) {
      return 0.0;
    } else {
      return Double.NEGATIVE_INFINITY;
    }
  }
  
}
