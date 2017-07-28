package xfamily.MVNormal;

import org.apache.commons.math3.linear.CholeskyDecomposition;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import xfamily.XHierarchy;
import static java.lang.Math.*;


public abstract class MVNormalNonConjugateHierarchy
	implements XHierarchy<RealVector,MVNormal,MVNormalWishartIndependent> {
	
	static double neghalflog2pi = -.5*log(2*PI);

  double constant;
	MVNormalWishartIndependent prior;
	RealVector sumX;
	RealMatrix sumXX;
	int number;
		
	/**
	 * Independent Gaussian, Inverse Gamma non-conjugate prior for mean, precision
	 * parameter of Gaussian exponential family.
	 * @param prior a MVNormalWishartIndependent prior.
	 */
	public MVNormalNonConjugateHierarchy(MVNormalWishartIndependent prior) {
		this.prior = prior;
		sumX = new ArrayRealVector(prior.getNumDimension());
		sumXX = new Array2DRowRealMatrix(prior.getNumDimension(),prior.getNumDimension());
		number = 0;
    constant = prior.getNumDimension()*neghalflog2pi;
	}
	/**
	 * Independent Gaussian, Inverse Gamma non-conjugate prior for mean, precision
	 * parameter of Gaussian exponential family.
	 * @param hier A MVNormalNonConjugateHierarchy.
	 */
	public MVNormalNonConjugateHierarchy(MVNormalNonConjugateHierarchy hier) {
		prior = hier.prior;
		sumX = hier.sumX.copy();
		sumXX = hier.sumXX.copy();
		number = hier.number;
	}
	
	@Override public void addDatum(RealVector datum) {
		number += 1;
		sumX = sumX.add(datum);
		sumXX = sumXX.add(datum.outerProduct(datum));
	}
	@Override public void removeDatum(RealVector datum) {
		number -= 1;
		sumX = sumX.subtract(datum);
		sumXX = sumXX.subtract(datum.outerProduct(datum));
		assert number >= 0;
//    System.out.println("removedatum: "+sumXX);
		assert (new CholeskyDecomposition(sumXX.add(prior.getPrecisionInvScale()))).getDeterminant()>-1e-10;
	}
  @Override public void clearData() {
		sumX = new ArrayRealVector(prior.getNumDimension());
		sumXX = new Array2DRowRealMatrix(prior.getNumDimension(),prior.getNumDimension());
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
    if (!(h instanceof MVNormalNonConjugateHierarchy))
      return false;
    MVNormalNonConjugateHierarchy nh = (MVNormalNonConjugateHierarchy)h;
    return prior==nh.prior &&
            number == nh.number &&
            sumX.equals(nh.sumX) && sumXX.equals(nh.sumXX);
  }

}

