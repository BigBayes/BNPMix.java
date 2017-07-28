package xfamily.Normal;

import xfamily.XHierarchyMarginalized;
import static java.lang.Math.*;


public class NormalConjugateHierarchyMarginalized
	extends NormalConjugateHierarchy
	implements XHierarchyMarginalized<Double,Normal,NormalGamma> {
	
	
	public NormalConjugateHierarchyMarginalized(NormalGamma prior) {
		super(prior);
	}
	public NormalConjugateHierarchyMarginalized(NormalConjugateHierarchy hier) {
		super(hier);
	}
	
	public Normal getParameter() {
		return getPosterior().drawSample();
	}
	public Double drawDatum() {
		return getParameter().drawSample();
	}
	
	public Normal getMean() {
		NormalGamma p = getPosterior();
		double pp = p.precisionDegFreedom / p.precisionInvScale;
		double mm = p.meanMean;
		return new Normal(mm,pp);
	}
	
	public double logJoint() {
		return  number*neghalflog2pi - prior.logNormalizer() + getPosterior().logNormalizer();
	}
	public double logPredictive(Double datum) {
		NormalConjugateHierarchy hier = new NormalConjugateHierarchyMarginalized(this);
		hier.addDatum(datum);
		return neghalflog2pi 
				+ hier.getPosterior().logNormalizer()
				- getPosterior().logNormalizer();
	}
	public void sample() {}
	
}
