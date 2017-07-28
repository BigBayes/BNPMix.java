package xfamily.Normal;

import xfamily.XHierarchy;
import static java.lang.Math.*;


public abstract class NormalNonConjugateHierarchy
	implements XHierarchy<Double,Normal,NormalGammaIndependent> {
	
	static double neghalflog2pi = -.5*log(2*PI);
	
	NormalGammaIndependent prior;
	double sumX;
	double sumXX;
	int number;
		
	/**
	 * Independent Gaussian, Inverse Gamma non-conjugate prior for mean, precision
	 * parameter of Gaussian exponential family.
	 * @param prior a NormalGammaIndependent prior.
	 */
	public NormalNonConjugateHierarchy(NormalGammaIndependent prior) {
		this.prior = prior;
		sumX = 0.0;
		sumXX = 0.0;
		number = 0;
	}
	/**
	 * Independent Gaussian, Inverse Gamma non-conjugate prior for mean, precision
	 * parameter of Gaussian exponential family.
	 * @param hier A NormalNonConjugateHierarchy.
	 */
	public NormalNonConjugateHierarchy(NormalNonConjugateHierarchy hier) {
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
	
	@Override public String toString() {
		return getClass().getSimpleName()+"(h="+prior+
        ",n="+number+
				",s="+sumX+
        ",s2="+sumXX+")";
	}

  @Override public boolean equals(XHierarchy h) {
    if (!(h instanceof NormalNonConjugateHierarchy))
      return false;
    NormalNonConjugateHierarchy nh = (NormalNonConjugateHierarchy)h;
    return prior==nh.prior &&
            abs(sumX-nh.sumX)/((abs(sumX)+abs(nh.sumX)))<1e-5 &&
            abs(sumXX-nh.sumXX)/((abs(sumXX)+abs(nh.sumXX)))<1e-5 &&
            number == nh.number;
  }

}

