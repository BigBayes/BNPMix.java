package xfamily.Normal;
import xfamily.XHierarchySampled;
import static java.lang.Math.log;
import static utilities.Generator.generator;

public class NormalNonConjugateHierarchySampled
	extends NormalNonConjugateHierarchy
	implements XHierarchySampled<Double,Normal,NormalGammaIndependent>{
	
	Normal param;
	
	public NormalNonConjugateHierarchySampled(NormalGammaIndependent prior) {
		super(prior);
		param = prior.drawSample();
	}
	public NormalNonConjugateHierarchySampled(NormalNonConjugateHierarchy hier) {
		super(hier);
    Normal p = hier.getParameter();
		param = new Normal(p.mean,p.precision);
	}

	@Override public Normal getParameter() {
		return param;
	}
	@Override public Double drawDatum() {
		return param.drawSample();
	}

	@Override public double logJoint() {
		return  number*(neghalflog2pi+.5*log(param.precision))
				-.5*param.precision*(sumXX-2.0*sumX*param.mean+number*param.mean*param.mean)
        +prior.logProbability(param);
	}
	@Override public double logPredictive(Double datum) {
		return param.logProbability(datum);
	}
	
	@Override public void sample() {
    if (number==0) {
      param = prior.drawSample();
    } else {
      double newPrecision = prior.meanPrecision + number*param.precision;
      double newMean = (prior.meanPrecision*prior.meanMean + param.precision*sumX)
                       /newPrecision;
      param.mean = generator.nextGaussian(newMean, 1.0/newPrecision);
      double newShape = prior.precisionShape +.5*number;
      double newInvScale = prior.precisionInvScale
              +.5*(sumXX-2.0*param.mean*sumX+number*param.mean*param.mean);
      param.precision = generator.nextGamma(newShape, newInvScale);
    }
	}

  @Override public String toString() {
		return getClass().getSimpleName()+"(h="+prior+
        ",p="+param+
        ",n="+number+
				",s="+sumX+
        ",s2="+sumXX+")";
	}
}
