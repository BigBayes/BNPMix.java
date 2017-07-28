package xfamily.MVNormal;
import LinearAlgebra.Cholesky;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import xfamily.XHierarchySampled;
import static java.lang.Math.log;
import static utilities.Generator.generator;

public class MVNormalNonConjugateHierarchySampled
	extends MVNormalNonConjugateHierarchy
	implements XHierarchySampled<RealVector,MVNormal,MVNormalWishartIndependent>{
	
	MVNormal param;
	
	public MVNormalNonConjugateHierarchySampled(MVNormalWishartIndependent prior) {
		super(prior);
		param = prior.drawSample();
	}
	public MVNormalNonConjugateHierarchySampled(MVNormalNonConjugateHierarchy hier) {
		super(hier);
    MVNormal p = hier.getParameter();
		param = new MVNormal(p.getMean(),p.getPrecision(),p.getPrecisionLogDeterminant());
	}

	@Override public MVNormal getParameter() {
		return param;
	}
	@Override public RealVector drawDatum() {
		return param.drawSample();
	}

	@Override public double logJoint() {
		return
        number*(constant + .5*log(param.getPrecisionLogDeterminant()))
				-.5*(number*param.getMean().dotProduct(param.getPrecision().operate(param.getMean()))
            -2.0*param.getMean().dotProduct(param.getPrecision().operate(sumX))
            +param.precisionDot(sumXX))
        +prior.logProbability(param);
	}
	@Override public double logPredictive(RealVector datum) {
		return param.logProbability(datum);
	}
	
	@Override public void sample() {
    if (number==0) {
      param = prior.drawSample();
      return;
    }
    Cholesky newPrecision = new Cholesky(
            prior.getMeanPrecision().add(param.getPrecision().scalarMultiply(number)));
    RealVector newMean = 
            newPrecision.getSolver().solve(
            prior.getMeanPrecision().operate(prior.getMeanMean()).add(
            param.getPrecision().operate(sumX)));
    param.setMean(generator.nextMVNormalMeanPrecision(newMean, newPrecision));
    double newDegFreedom = prior.getPrecisionDegFreedom() + number;
    
//    System.out.println("hierarchysampled.sample: " + prior.precisionInvScale
//            .add(sumXX)
//            .add(param.mean.outerProduct(param.mean.mapMultiply(number)))
//            .subtract(param.mean.outerProduct(sumX))
//            .subtract(sumX.outerProduct(param.mean)));
    Cholesky newInvScale = new Cholesky(
            prior.getPrecisionInvScale()
            .add(sumXX)
            .add(param.getMean().outerProduct(param.getMean().mapMultiply(number)))
            .subtract(param.getMean().outerProduct(sumX))
            .subtract(sumX.outerProduct(param.getMean())));
		param.setPrecision(generator.nextWishart(newDegFreedom, newInvScale));
	}

  @Override public String toString() {
		return getClass().getSimpleName()+"(h="+prior+
        ",p="+param+
        ",n="+number+
				",s="+sumX+
        ",s2="+sumXX+")";
	}
}
