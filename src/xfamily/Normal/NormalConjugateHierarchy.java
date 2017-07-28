package xfamily.Normal;

import xfamily.XHierarchy;
import static java.lang.Math.*;


public abstract class NormalConjugateHierarchy
	implements XHierarchy<Double,Normal,NormalGamma> {
	
	static double neghalflog2pi = -.5*log(2*PI);
	
	NormalGamma prior;
	double sumX;
	double sumXX;
	int number;
		
	/**
	 * Gaussian Inverse Gamma conjugate prior for meanMean, precision
	 * parameter of Gaussian exponential family.
	 * @param prior a NormalGamma prior.
	 */
	public NormalConjugateHierarchy(NormalGamma prior) {
		this.prior = prior;
		sumX = 0.0;
		sumXX = 0.0;
		number = 0;
	}
	/**
	 * Gaussian Inverse Gamma conjugate prior for meanMean, precision
	 * parameter of Gaussian exponential family.
	 * @param hier A NormalConjugateHierarchy.
	 */
	public NormalConjugateHierarchy(NormalConjugateHierarchy hier) {
		prior = hier.prior;
		sumX = hier.sumX;
		sumXX = hier.sumXX;
		number = hier.number;
	}
	
	@Override public void addDatum(Double datum) {
		number += 1;
		sumX += datum;
		sumXX += datum*datum;
	}
	@Override public void removeDatum(Double datum) {
		number -= 1;
		sumX -= datum;
		sumXX -= datum*datum;
		assert number >= 0;
		assert sumXX >= -1e-10;
	}
  @Override public void clearData() {
		sumX = 0.0;
		sumXX = 0.0;
		number = 0;
  }
	@Override public int numDatum() {
		return number;
	}
	public NormalGamma getPosterior() {
		double meanRelPrecision = prior.meanRelPrecision + number;
		double precisionDegFreedom = prior.precisionDegFreedom + number;
		double meanMean = (sumX + prior.meanRelPrecision * prior.meanMean) / meanRelPrecision;
		double precisionInvScale = (sumXX + prior.precisionInvScale)
				+ prior.meanRelPrecision*prior.meanMean*prior.meanMean
				- meanRelPrecision*meanMean*meanMean;
		return new NormalGamma(meanMean,prior.relPrecision,meanRelPrecision,
            precisionDegFreedom,precisionInvScale,
            prior.precisionInvScaleAlpha,prior.precisionInvScaleBeta);
	}
	
	@Override public String toString() {
		NormalGamma p = getPosterior();
		return getClass().getSimpleName()+"(n="+number+
				",m="+p.meanMean+
				",r="+p.meanRelPrecision+
				",v="+p.precisionDegFreedom+
				",s="+p.precisionInvScale+
				");("+sumX+","+sumXX+")";
	}

  @Override public boolean equals(XHierarchy h) {
    if (!(h instanceof NormalConjugateHierarchy))
      return false;
    NormalConjugateHierarchy nh = (NormalConjugateHierarchy)h;
    return prior==nh.prior &&
            abs(sumX-nh.sumX)/((abs(sumX)+abs(nh.sumX)))<1e-5 &&
            abs(sumXX-nh.sumXX)/((abs(sumXX)+abs(nh.sumXX)))<1e-5 &&
            number == nh.number;
  }

}

