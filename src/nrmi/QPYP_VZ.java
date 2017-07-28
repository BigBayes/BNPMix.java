/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nrmi;

import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import java.util.Collection;
import mixture.Cluster;
import static utilities.Generator.generator;
import static utilities.SpecialFunctions.logsumexp;
/**
 * Pitman-Yor mixture model implementation using QGGP framework.
 * Better method of sampling U and Tau here.
 * No updates to theta.
 * @author ywteh
 */
public class QPYP_VZ extends QPYP_TU {
  
  public QPYP_VZ(double Salpha, double Sbeta, double Tshape, double Tinvscale) {
    this(Salpha,Sbeta,Tshape,Tinvscale,default_minslice,default_maxclusters);
  }
  public QPYP_VZ(double Salpha, double Sbeta, double Tshape, double Tinvscale,
          double minslice, double maxclusters) {
    super(Salpha,Sbeta,Tshape,Tinvscale,minslice,maxclusters);
  }
  
	@Override public <H extends Cluster> void sample(int numData, Collection<H> clusters) {
		sigmasampler.sample(clusters);
    thetaslicer.sample(clusters);
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
    
//    System.out.println("sigma="+sigma);
//    System.out.println("theta="+theta);
//    System.out.println("z="+exp(a-ab));
//    System.out.println("g="+g);
//    System.out.println("logu="+logU);
//    System.out.println("logtau="+logtau);
    //if (logU>100) {
    //  System.out.println("oh no");
    //}
    //sampleU(numData,clusters);
    //System.out.println("sigma="+sigma+" g="+g+" logU="+logU+" tau="+tau);
	}
  

}
