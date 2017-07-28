package xfamily.Normal;
import xfamily.XHierarchySampled;
import static java.lang.Math.log;

public class NormalConjugateHierarchySampled
	extends NormalConjugateHierarchy
	implements XHierarchySampled<Double,Normal,NormalGamma>{
	
	Normal param;
	
	public NormalConjugateHierarchySampled(NormalGamma prior) {
		super(prior);
		param = prior.drawSample();
	}
	public NormalConjugateHierarchySampled(NormalConjugateHierarchy hier) {
		super(hier);
		param = getPosterior().drawSample();
	}

	@Override public Normal getParameter() {
		return param;
	}
	@Override public Double drawDatum() {
		return param.drawSample();
	}

	@Override public double logJoint() {
		double relPrecision = prior.meanRelPrecision + number;
		double degFreedom = prior.precisionDegFreedom + number;
		
		return  number*neghalflog2pi 
				- prior.logNormalizer()
				+.5*(degFreedom-1.0)*log(param.precision)
				-.5*param.precision*(
						relPrecision*param.mean*param.mean
						+ prior.meanRelPrecision*prior.meanMean*prior.meanMean
						-2.0*param.mean*(sumX + prior.meanRelPrecision * prior.meanMean)
						+ (sumXX+prior.precisionInvScale));
	}
	@Override public double logPredictive(Double datum) {
		return param.logProbability(datum);
	}
	
	@Override public void sample() {
    if (number==0) {
      param = prior.drawSample();
    } else {
  		param = getPosterior().drawSample();
    }
	}

  @Override public String toString() {
		NormalGamma p = getPosterior();
		return getClass().getSimpleName()+"(n="+number+
				",m="+p.meanMean+
				",v="+p.precisionDegFreedom+
				",s="+p.precisionInvScale+
				",r="+p.meanRelPrecision+
				");("+sumX+","+sumXX+");"+param;
	}
}
