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

/**
 * Pitman-Yor mixture model implementation using QGGP framework.
 * Basic method of sampling U and Tau used here.
 * No updates to theta.
 * @author ywteh
 */
public class QGGPLogNormal extends QGGP {
  double LogTauMean, LogTauVar;

  public QGGPLogNormal(double Salpha, double Sbeta, double LogTauMean, double LogTauVar) {
    this(Salpha,Sbeta,LogTauMean,LogTauVar,default_minslice,default_maxclusters);
  }
  public QGGPLogNormal(double Salpha, double Sbeta, double Tmean, double Tvar,
          double minslice, double maxclusters) {
    super(Salpha,Sbeta,minslice,maxclusters);
    this.LogTauMean = Tmean;
    this.LogTauVar  = Tvar;
  }
  @Override
  double F(double sigma, double logtau) {
    return exp(logF(sigma,logtau));
  }

  @Override
  double logF(double sigma, double logtau) {
    return -.5*(log(2*PI)+log(LogTauVar)+(logtau-LogTauMean)*(logtau-LogTauMean)/LogTauVar);
  }
  
}
