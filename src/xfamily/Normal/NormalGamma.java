package xfamily.Normal;

import xfamily.XPrior;
import mcmc.kernel.Metropolis;
import xfamily.XHierarchy;
import java.util.Collection;
import xfamily.XFamily;
import static java.lang.Math.PI;
import static java.lang.Math.log;
import static java.lang.Math.exp;
import static java.lang.Math.min;
import static java.lang.Math.sqrt;
import static utilities.DataIO.vlogln;
import static utilities.Generator.generator;
import static utilities.SpecialFunctions.logGamma;

public class NormalGamma implements XPrior<Normal> {
	static double halflog2 = .5*log(2.0);
	static double halflogpi = .5*log(PI);
	public double meanMean;
  double relPrecision;
	public double meanRelPrecision;
	public double precisionDegFreedom;
  public double precisionInvScale;
	double precisionInvScaleAlpha;
  double precisionInvScaleBeta;
  private double meanStep;
	
	/**
	 * Hyperparameters of Normal-Gamma.
	 * @param number
	 * @param meanMean
	 * @param meanRelPrecision
	 * @param precisionDegFreedom
	 * @param precisionInvScaleAlpha
	 * @param precisionInvScaleBeta
	 */
	public NormalGamma(
			double meanMean,
			double relPrecision,
			double precisionDegFreedom,
			double precisionInvScaleAlpha,
      double precisionInvScaleBeta) {
    this(meanMean,
         relPrecision,
         relPrecision*precisionInvScaleAlpha/precisionInvScaleBeta*.5,
         precisionDegFreedom,
         precisionInvScaleAlpha/precisionInvScaleBeta,
         precisionInvScaleAlpha,
         precisionInvScaleBeta
        );
  }
	/**
	 * Hyperparameters of Normal-Gamma.
	 * @param meanMean
	 * @param meanRelPrecision
	 * @param precisionDegFreedom
   * @param precisionInvScale
	 * @param precisionInvScaleAlpha
	 * @param precisionInvScaleBeta
	 */
	NormalGamma(
			double meanMean,
      double relPrecision,
			double meanRelPrecision,
			double precisionDegFreedom,
      double precisionInvScale,
			double precisionInvScaleAlpha,
      double precisionInvScaleBeta) {

    if (relPrecision<=0.0) throw new Error("Precision is negative.");
    if (meanRelPrecision<=0.0) throw new Error("Precision is negative.");
		if (precisionDegFreedom<=0.0) throw new Error("Degree of freedom is negative.");
		if (precisionInvScale<=0.0) throw new Error("Inverse scale parameter is negative.");
		if (precisionInvScaleAlpha<=0.0) throw new Error("Inverse scale alpha parameter is negative.");
		if (precisionInvScaleBeta<=0.0) throw new Error("Inverse scale beta parameter is negative.");
		this.meanMean = meanMean;
    this.relPrecision = relPrecision;
		this.meanRelPrecision = meanRelPrecision;
		this.precisionDegFreedom = precisionDegFreedom;
    this.precisionInvScale = precisionInvScale;
		this.precisionInvScaleAlpha = precisionInvScaleAlpha;
    this.precisionInvScaleBeta = precisionInvScaleBeta;
    this.meanStep = sqrt(2.0/meanRelPrecision/precisionDegFreedom*precisionInvScale);
	}

  @Override public int numDataDim() { return 2; }
  @Override public int numParamDim() { return 4; }
	@Override public double logNormalizer() {
		return 	 halflog2*(precisionDegFreedom+1.0)
				+halflogpi
				-.5*log(meanRelPrecision)
				-.5*precisionDegFreedom*log(precisionInvScale)
				+logGamma(.5*precisionDegFreedom);
	}
	@Override public double logProbability(Normal datum) {
		double diff = datum.mean-meanMean;
		return   .5*(precisionDegFreedom-1.0)*log(datum.precision)
				-.5*datum.precision*(meanRelPrecision*diff*diff+precisionInvScale)
				-logNormalizer();
	}
	@Override public Normal drawSample() {
		double pp = generator.nextGamma(precisionDegFreedom/2.0, precisionInvScale/2.0);
		double mm = generator.nextGaussian(meanMean,1.0/meanRelPrecision/pp);
		return new Normal(mm,pp);
	}
	@Override public Double[] getMeanParameter() {
    Double[] result = new Double[4];
    throw new Error("not done yet");
	}

  private Collection<? extends NormalConjugateHierarchy> data;
  public void sample(Collection<? extends XHierarchy> data) {
    this.data = (Collection<? extends NormalConjugateHierarchy>)data;
    //sampleMean();
    //sampleDegFreedom(); // note that an uninformative improper prior does not work!
    //sampleRelPrecision();
    sampleInvScale();
  }
	class MeanSampler extends Metropolis<Double> {
		@Override public Double propose(Double mean) {
      return mean + .5*meanStep*generator.nextGaussian();
    }
		@Override public double logratio(Double meannew, Double meanold) {
      meanMean = meanold;
			double result = 0.0;
      for ( NormalConjugateHierarchy d : data ) {
        result -= d.logJoint();
      }
      meanMean = meannew;
      for ( NormalConjugateHierarchy d : data ) {
        result += d.logJoint();
      }
			vlogln(1,"Meansampler log p("+meannew+")-log p("+meanold+")="+result);
			return result;
		}
		void sample() {
			meanMean = sample(meanMean);
		}
	}
	private MeanSampler meansampler = new MeanSampler();
	private void sampleMean() { meansampler.sample(); }

	class DegFreedomSampler extends Metropolis<Double> {
		@Override public Double propose(Double degFreedom) {
      return degFreedom * exp(.5*generator.nextGaussian());
    }
		@Override public double logratio(Double dfnew, Double dfold) {
      precisionDegFreedom = dfold;
			double result = 0.0;
      for ( NormalConjugateHierarchy d : data ) {
        result -= d.logJoint();
      }
      precisionDegFreedom = dfnew;
      for ( NormalConjugateHierarchy d : data ) {
        result += d.logJoint();
      }
			vlogln(1,"DegFreedomsampler log p("+dfnew+")-log p("+dfold+")="+result);
			return result;
		}
		void sample() {
      //System.out.print("precisionDegFreedom: "+precisionDegFreedom);
			precisionDegFreedom = sample(precisionDegFreedom);
      //System.out.println(" -> "+precisionDegFreedom);
		}
	}
	private DegFreedomSampler degfreedomsampler = new DegFreedomSampler();
	private void sampleDegFreedom() { degfreedomsampler.sample(); }

  class RelPrecisionSampler extends Metropolis<Double> {
		@Override public Double propose(Double relPrecision) {
      return relPrecision * exp(.5*generator.nextGaussian());
    }
		@Override public double logratio(Double rpnew, Double rpold) {
      meanRelPrecision = rpold;
			double result = 0.0;
      for ( NormalConjugateHierarchy d : data ) {
        result -= d.logJoint();
      }
      meanRelPrecision = rpnew;
      for ( NormalConjugateHierarchy d : data ) {
        result += d.logJoint();
      }
			vlogln(1,"RelPrecisionsampler log p("+rpnew+")-log p("+rpold+")="+result);
			return result;
		}
		void sample() {
      //System.out.print("meanRelPrecision: "+meanRelPrecision);
			meanRelPrecision = sample(meanRelPrecision);
      //System.out.println(" -> "+meanRelPrecision);
		}
	}
	private RelPrecisionSampler relprecisionsampler = new RelPrecisionSampler();
	private void sampleRelPrecision() { relprecisionsampler.sample(); }

	class InvScaleSampler extends Metropolis<Double> {
		@Override public Double propose(Double precisionInvScale) {
      return precisionInvScale * exp(.5*generator.nextGaussian());
    }
		@Override public double logratio(Double isnew, Double isold) {
			double result = precisionInvScaleAlpha*(log(isnew)-log(isold))
              - precisionInvScaleBeta*(isnew-isold);
      double loglik = 0.0;
      meanRelPrecision = relPrecision*isold*.5;
      precisionInvScale = isold;
      for ( NormalConjugateHierarchy d : data ) {
        loglik -= d.logJoint();
      }
      meanRelPrecision = relPrecision*isnew*.5;
      precisionInvScale = isnew;
      for ( NormalConjugateHierarchy d : data ) {
        loglik += d.logJoint();
      }
      //System.out.println("loglik="+exp(loglik));
			vlogln(1,"InvScalesampler log p("+isnew+")-log p("+isold+")="+result);
			return result+loglik;
		}
		void sample() {
      //System.out.print("invScale: "+precisionInvScale);
			precisionInvScale = sample(precisionInvScale);
      meanRelPrecision = relPrecision*precisionInvScale*.5;
      //System.out.println(" -> "+precisionInvScale);
      //System.out.println("normalgamma invscale sampler: numclusters="+data.size());
		}
	}
	private InvScaleSampler invscalesampler = new InvScaleSampler();
	private void sampleInvScale() {
		invscalesampler.sample();
	}

	public enum Properties {
    meanMean,
    meanRelPrecision,
    precisionDegFreedom,
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
    case meanRelPrecision: return meanRelPrecision;
    case precisionDegFreedom: return precisionDegFreedom;
    case precisionInvScale: return precisionInvScale;
		default: throw new Error("Unknown property "+property);
		}
	}
  @Override public Object get(String property, Object arg) {
    return get(property);
  }

  @Override public String toString() {
		return "NG(m="+meanMean+",r="+meanRelPrecision+",v="+precisionDegFreedom+",s="+precisionInvScale+")";
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
  public static NormalGamma constructPrior(double[][] data) throws Error, Exception {
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
  public static NormalGamma constructPrior(double[][] data, boolean useMeanVar,
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
    
    return new NormalGamma(
              meanMean, meanPrecision,
              precisionDegFreedom,
              .5*invScaleDegFreedom, 
              .5*precisionInvScaleInvScale);
    
  }

}
