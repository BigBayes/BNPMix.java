package xfamily.Normal;

import xfamily.XHierarchy;
import xfamily.XPrior;
import mcmc.kernel.Metropolis;
import java.util.Collection;
import xfamily.XFamily;
import static java.lang.Math.PI;
import static java.lang.Math.log;
import static java.lang.Math.exp;
import static java.lang.Math.sqrt;
import static utilities.Generator.generator;
import static utilities.SpecialFunctions.logGamma;
import static utilities.DataIO.vlogln;

public class NormalGammaIndependent implements XPrior<Normal> {
	static double halfLogTwoPi = .5*log(2*PI);
	public double meanMean;
	public double meanPrecision;
	public double precisionShape;
	public double precisionInvScale;
	public double precisionInvScaleAlpha;
	public double precisionInvScaleBeta;
  private double meanStep;

	/**
	 * Hyperparameters of Normal-Gamma-Independent.
	 * @param meanMean
	 * @param meanPrecision
	 * @param precisionShape
	 * @param precisionInvScaleAlpha
	 * @param precisionInvScaleBeta
	 */
	public NormalGammaIndependent(
			double meanMean,
			double meanPrecision,
			double precisionShape,
			double precisionInvScaleAlpha,
      double precisionInvScaleBeta) {
    this(meanMean,meanPrecision,precisionShape,
            precisionInvScaleAlpha/precisionInvScaleBeta,
            precisionInvScaleAlpha,precisionInvScaleBeta);
  }

	/**
	 * Hyperparameters of Normal-Gamma-Independent.
	 * @param meanMean
	 * @param meanPrecision
	 * @param precisionShape
	 * @param precisionInvScale
	 * @param precisionInvScaleAlpha
	 * @param precisionInvScaleBeta
	 */
	public NormalGammaIndependent(
			double meanMean,
			double meanPrecision,
			double precisionShape,
      double precisionInvScale,
			double precisionInvScaleAlpha,
      double precisionInvScaleBeta) {
		if (meanPrecision<0.0) throw new Error("mean precision is negative.");
		if (precisionShape<0.0) throw new Error("shape is negative.");
		if (precisionInvScale<0.0) throw new Error("Inverse scale is negative.");
		if (precisionInvScaleAlpha<0.0) throw new Error("Inverse scale alpha parameter is negative.");
		if (precisionInvScaleBeta<0.0) throw new Error("Inverse scale beta parameter is negative.");
		this.meanMean = meanMean;
		this.meanPrecision = meanPrecision;
		this.precisionShape = precisionShape;
    this.precisionInvScale = precisionInvScale;
		this.precisionInvScaleAlpha = precisionInvScaleAlpha;
		this.precisionInvScaleBeta = precisionInvScaleBeta;
    this.meanStep = sqrt(1.0/meanPrecision);
	}
	public int numDataDim() { return 2; }
  public int numParamDim() { return 4; }
	@Override public double logNormalizer() {
		return 	halfLogTwoPi -.5*log(meanPrecision)
            - precisionShape*log(precisionInvScale)
            + logGamma(precisionShape);
	}
	@Override public double logProbability(Normal datum) {
		double diff = datum.mean-meanMean;
		return  -.5*meanPrecision*diff*diff
            +(precisionShape-1)*log(datum.precision)
            -precisionInvScale*datum.precision
						-logNormalizer();
	}
	@Override public Normal drawSample() {
		return new Normal(generator.nextGaussian(meanMean,1.0/meanPrecision),
                      generator.nextGamma(precisionShape,precisionInvScale));
	}
	@Override public Double[] getMeanParameter() {
    throw new Error("not done yet");
	}

  private Collection<? extends NormalNonConjugateHierarchy> data;
  public void sample(Collection<? extends XHierarchy> data) {
    this.data = (Collection<? extends NormalNonConjugateHierarchy>) data;
    //sampleMeanMean();
    //sampleMeanPrecision();
    //samplePrecisionShape();
    samplePrecisionInvScale();
  }
	class MeanMeanSampler extends Metropolis<Double> {
		@Override public Double propose(Double mean) {
      return mean + .1*meanStep*generator.nextGaussian();
    }
		@Override public double logratio(Double meannew, Double meanold) {
      meanMean = meanold;
			double result = 0.0;
      for ( NormalNonConjugateHierarchy d : data ) {
        result -= d.logJoint();
      }
      meanMean = meannew;
      for ( NormalNonConjugateHierarchy d : data ) {
        result += d.logJoint();
      }
			vlogln(1,"MeanMeansampler log p("+meannew+")-log p("+meanold+")="+result);
			return result;
		}
		void sample() {
			meanMean = sample(meanMean);
		}
	}
	private MeanMeanSampler meanmeansampler = new MeanMeanSampler();
	public void sampleMeanMean() {
		meanmeansampler.sample();
	}

	class MeanPrecisionSampler extends Metropolis<Double> {
		@Override public Double propose(Double meanPrecision) {
      return meanPrecision * exp(.1*generator.nextGaussian());
    }
		@Override public double logratio(Double mpnew, Double mpold) {
      meanPrecision = mpold;
			double result = 0.0;
      for ( NormalNonConjugateHierarchy d : data ) {
        result -= d.logJoint();
      }
      meanPrecision = mpnew;
      for ( NormalNonConjugateHierarchy d : data ) {
        result += d.logJoint();
      }
			vlogln(1,"MeanPrecisionsampler log p("+mpnew+")-log p("+mpold+")="+result);
			return result;
		}
		void sample() {
			meanPrecision = sample(meanPrecision);
		}
	}
	private MeanPrecisionSampler meanprecisionsampler = new MeanPrecisionSampler();
	public void sampleMeanPrecision() {
		meanprecisionsampler.sample();
	}

  class PrecisionShapeSampler extends Metropolis<Double> {
		@Override public Double propose(Double precisionShape) {
      return precisionShape * exp(.1*generator.nextGaussian());
    }
		@Override public double logratio(Double psnew, Double psold) {
      precisionShape = psold;
			double result = 0.0;
      for ( NormalNonConjugateHierarchy d : data ) {
        result -= d.logJoint();
      }
      precisionShape = psnew;
      for ( NormalNonConjugateHierarchy d : data ) {
        result += d.logJoint();
      }
			vlogln(1,"RelPrecisionsampler log p("+psnew+")-log p("+psold+")="+result);
			return result;
		}
		void sample() {
			precisionShape = sample(precisionShape);
		}
	}
	private PrecisionShapeSampler precisionshapesampler = new PrecisionShapeSampler();
	public void samplePrecisionShape() {
		precisionshapesampler.sample();
	}

	class PrecisionInvScaleSampler extends Metropolis<Double> {
		@Override public Double propose(Double precisionInvScale) {
      return precisionInvScale * exp(.5*generator.nextGaussian());
    }
		@Override public double logratio(Double isnew, Double isold) {
      precisionInvScale = isold;
			double result = precisionInvScaleAlpha*(log(isnew)-log(isold))
              - precisionInvScaleBeta*(isnew-isold);
      for ( NormalNonConjugateHierarchy d : data ) {
        result -= d.logJoint();
      }
      precisionInvScale = isnew;
      for ( NormalNonConjugateHierarchy d : data ) {
        result += d.logJoint();
      }
			vlogln(1,"InvScalesampler log p("+isnew+")-log p("+isold+")="+result);
			return result;
		}
		void sample() {
			precisionInvScale = sample(precisionInvScale);
		}
	}
	private PrecisionInvScaleSampler precisioninvscalesampler = new PrecisionInvScaleSampler();

	public void samplePrecisionInvScale() {
		precisioninvscalesampler.sample();
	}

	public enum Properties {
    meanMean,
    meanPrecision,
    precisionShape,
    precisionInvScale,
    precisionInvScaleAlpha,
    precisionInvScaleBeta,
		NOVALUE;
		public static Properties toValue(String str) {
			try {
				return valueOf(str);
			} catch (Exception ex) {
				return NOVALUE;
			}
		}
	}
	@Override public Object get(String property) {
		switch(Properties.toValue(property)) {
    case meanMean: return meanMean;
    case meanPrecision: return meanPrecision;
    case precisionShape: return precisionShape;
    case precisionInvScale: return precisionInvScale;
    case precisionInvScaleAlpha: return precisionInvScaleAlpha;
    case precisionInvScaleBeta: return precisionInvScaleBeta;
		default: throw new Error("Unknown property "+property);
		}
	}
  @Override public Object get(String property, Object arg) {
    return get(property);
  }


	@Override public String toString() {
		return "NGI(m="+meanMean+",r="+meanPrecision
            +",a="+precisionShape+",b="+precisionInvScale+")";
	}

  static boolean useMeanVar = true;
  static double meanRelScale = 1.0;
  static double precisionDegFreedomAdjustment = 3.0;
  static double invScaleDegFreedomAdjustment = 0.6;    
  static double precisionScale = 50.0;
  
  /**
   * Construct weakly informative, data dependent, prior based on recipe in 
   * Green & Richardson.
   * 
   * @param data Data matrix (first dimension is number of data items, second is dimensionality.
   * @return Weakly informative prior.
   * @throws Error if data format has problems.
   * @throws Exception if data has infinities or NaNs.
   */
  public static NormalGammaIndependent constructPrior(double[][] data) throws Error, Exception {
    if (data.length==0) {
      throw new Error("Empty data");
    }
    if (data[0].length!=1) {
      throw new Error("Data not 1 dimensional");
    }
    int dim = 1;
    double precisionDegFreedom = dim + precisionDegFreedomAdjustment;
    double invScaleDegFreedom = dim - invScaleDegFreedomAdjustment;    
    return constructPrior(data,useMeanVar,
            meanRelScale,precisionDegFreedom,
            invScaleDegFreedom,precisionScale);
  }


  /**
   * Construct weakly informative, data dependent, prior based on recipe in 
   * Favaro and Teh 2013.
   * 
   * @param data Data matrix (first dimension is number of data items, second is dimensionality.
   * @param useMeanVar Use empirical mean and variance of data, or range of data given by min and max.
   * @param meanRelScale
   * @param precisionDegFreedom
   * @param invScaleDegFreedom
   * @param precisionScale
   * @return Weakly informative prior.
   * @throws Error if data format has problems.
   * @throws Exception if data has infinities or NaNs.
   */
  public static NormalGammaIndependent constructPrior(double[][] data, boolean useMeanVar,
          double meanRelScale, 
          double precisionDegFreedom, 
          double invScaleDegFreedom,
          double precisionScale) throws Error, Exception {
    if (data.length==0) {
      throw new Error("Empty data");
    }
    if (data[0].length!=1) {
      throw new Error("Data not 1 dimensional");
    }
    int numdata = data.length;
    int dim = 1;
    for (int i=0; i<numdata; i++) {
      if (dim!=data[i].length)
        throw new Error("Data dimensions not consistent.");
    }

    double invScaleInvScale = 
        precisionScale*invScaleDegFreedom/(precisionDegFreedom-dim-1.0);

    double dmin  = Double.POSITIVE_INFINITY;
    double dmax  = Double.NEGATIVE_INFINITY;
    double dmean = 0.0;
    double dvar  = 0.0;
   	for (int i=0; i<data.length; i++) {
      double[] dataitem = data[i];
      double x = dataitem[0];
      if (Double.isInfinite(x)) throw new Exception("Infinity encountered in data");
      if (Double.isNaN(x)) throw new Exception("NaN encountered in data");
      dmean += x;
      dvar += x*x;
  		if (x<dmin) { dmin = x; }
  		if (x>dmax) { dmax = x; }
    }
    dmean /= (double)data.length;
    dvar = (dvar/(double)(data.length) - dmean*dmean) * (double)(data.length) / (double)(data.length-1);

    double meanMean;
    if (useMeanVar) {
      meanMean = dmean;
    } else {
      meanMean = .5 * (dmin + dmax);
    }
    double range;
    if (useMeanVar) {
      range = sqrt(dvar);
    } else {
      range = .5 * (dmax - dmin);
    }
    double meanPrecision = 1.0 / range / range / meanRelScale;
    double precisionInvScaleInvScale = invScaleInvScale / range / range; 
    
    return new NormalGammaIndependent(
              meanMean, meanPrecision,
              .5*precisionDegFreedom,
              .5*invScaleDegFreedom, 
              precisionInvScaleInvScale);
    
  }

}
