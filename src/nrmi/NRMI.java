package nrmi;

import static java.lang.Math.exp;
import static java.lang.Math.log;
import java.io.PrintStream;
import java.util.Collection;
import mcmc.kernel.Metropolis;
import mcmc.kernel.SliceStepOut;
import static utilities.Generator.generator;
import static utilities.DataIO.vlogln;

import mixture.Cluster;
import mixture.Mixture;
import utilities.Generator;

/**
 * Abstract class for a homogeneous normalized random measure.
 * 
 * 
 * @author ywteh
 *
 */
public abstract class NRMI {
  double logU = 0.0;

	/**
	 * Logarithm of the Levy density.
	 * @param mass Mass.
	 * @return log rho(mass)
	 */
	abstract public double logLevy(double mass);
	/**
	 * Logarithm of the Levy density of NRMI.
	 * @param mass Mass.
	 * @param u Parameter for exponential tilting.
	 * @return -u*mass + log rho(mass)
	 */
	abstract double logLevy(double mass, double logu);
	/**
	 * Logarithm of the Laplace transform.
	 * @param u Parameter for exponential tilting.
	 * @return log E[e^{-u*T}] = log int_0^infty (1-e^{-u*x}) rho(x) dx
	 */
	abstract public double laplace(double logu);
  
	/**
	 * Logarithm of the Laplace transform evaluated at U.
	 * @return log E[e^{-U*T}] = log int_0^infty (1-e^{-U*x}) rho(x) dx
	 */
  public double laplace() {
    return laplace(logU);
  }
	/**
	 * Gamma integral.
	 * @param num Index of integral.
	 * @param u Parameter for exponential tilting.
	 * @return log int_0^infty x^{num} e^{-u*x} rho(x) dx
	 */
	abstract double logGamma(int num, double logu);
	/**
	 * Gamma integral at U.
	 * @param num Index of integral.
	 * @return log int_0^infty x^{num} e^{-U*x} rho(x) dx
	 */
  public double logGamma(int num) {
    return logGamma(num,logU);
  }
	/**
	 * Draws a sample from the mass distribution with density proportional to 
	 * x^{num} e^{-u*x} rho(x)
	 * @param num Index.
	 * @param u Exponential tilting parameter.
	 * @return Drawn sample.
	 */
	abstract double drawLogMass(int num, double logu);
  
	/**
	 * Draws a sample from the mass distribution with density proportional to 
	 * x^{num} e^{-U*x} rho(x)
	 * @param num Index.
	 * @return Drawn sample.
	 */
  public double drawLogMass(int num) {
    return drawLogMass(num,logU);
  }
	
	/**
	 * Draws a sample from NRMI, restricting only to masses above a slice value.
	 * The set of masses has a law given by a Poisson process, with rate e^{-U*x} rho(x).
	 * @param slice The slice threshold value.
	 * @return Drawn set of masses, sorted in decreasing order.
	 */
	abstract public double[] drawLogMasses(double slice);

	/**
	 * Expected number of clusters in a partition of size num induced by the NRMI.
	 * @param num Number of elements of partition.
	 * @param u Exponential tilting parameter.
	 * @return Expected number of clusters.
	 */
	abstract double meanNumClusters(int num, double logu);
  
	/**
	 * Expected number of clusters in a partition of size num induced by the NRMI.
	 * @param num Number of elements of partition.
	 * @return Expected number of clusters.
	 */
  public double meanNumClusters(int num) { 
    return meanNumClusters(num,logU);
  }

	/**
	 * Mean of the mass distribution with density proportional to 
	 * x^{num} e^{-u*x} rho(x)
	 * @param num Index
	 * @param u Exponential tilting parameter.
	 * @return Mean mass.
	 */
	double logMeanMass(int num, double logu) {
		return logGamma(num+1,logu)-logGamma(num,logu);
	}
	/**
	 * Mean of the mass distribution with density proportional to 
	 * x^{num} e^{-U*x} rho(x)
	 * @param num Index
	 * @return Mean mass.
	 */
	public double logMeanMass(int num) {
		return logMeanMass(num,logU);
	}
	/**
	 * Mean of the total mass under a NRMI.
	 * @param u Exponential tilting parameter.
	 * @return E[T] = int_0^infty x e^{-u*x} rho(x) dx
	 */
	double logMeanTotalMass(int numclusters, double logu) {
		return logGamma(1,logu);
	}
	/**
	 * Mean of the total mass under a NRMI evaluated at U.
	 * @return E[T] = int_0^infty x rho(x) dx
	 */
	public double logMeanTotalMass(int numclusters) {
		return logMeanTotalMass(numclusters,logU);
	}
  
  /**
   * The Auxiliary variable U.
   * @return U
   */
  public double getU() {
    return exp(logU);
  }
  /**
   * Sets the auxiliary variable to u.
   * @param u 
   */
  public void setLogU(double logu) {
    this.logU = logu;
  }
  /**
   * Logarithm of the auxiliary variable U
   * @return log(U)
   */
  public double getLogU() {
    return logU;
  }
	
	/**
	 * Sample the parameters of the NRMI, given a likelihood p(partition,u|NRMI).
	 * @param clusters A partition induced by NRMI.
	 * @param u Exponential tilting parameter.
	 */
	public <H extends Cluster> void sample(int numData, Collection<H> clusters) {
    sampleU(numData,clusters);
  }

  class USlicer extends SliceStepOut {
    int numData;
    Collection<? extends Cluster> clusters;
    public USlicer() {
      super(1.0,20,generator);
    }

    @Override public double logDensity(double logU) {
			double result = 
					  (numData)*(logU) 
					- laplace(logU);
			for ( Cluster cc : clusters ) {
				assert !cc.isEmpty();
				result += logGamma(cc.number, logU);
			}
			return result;
    }
    <H extends Cluster> void sample(int numData, Collection<H> clusters) {
      this.numData = numData;
      this.clusters = clusters;
      logU = super.sample(logU);
    }
  }
  USlicer Uslicer = new USlicer();
	
	/**
	 * Samples auxiliary variable U.
	 */
	public <H extends Cluster> void sampleU(int numData, Collection<H> clusters) {
		Uslicer.sample(numData,clusters);
	}
}
