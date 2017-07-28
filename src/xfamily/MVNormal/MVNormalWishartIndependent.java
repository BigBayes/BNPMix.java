package xfamily.MVNormal;

import LinearAlgebra.Cholesky;
import static java.lang.Math.PI;
import static java.lang.Math.log;
import java.util.Collection;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import static utilities.Generator.generator;
import static utilities.SpecialFunctions.logGamma;
import static java.lang.Math.sqrt;
import xfamily.XHierarchy;
import xfamily.XPrior;

public class MVNormalWishartIndependent implements XPrior<MVNormal> {
  private int numdim;
	private RealVector meanMean;
  private RealMatrix meanPrecision;
	private double precisionDegFreedom;
	private RealMatrix precisionInvScale;
  private double precisionInvScaleDegFreedom;
  private RealMatrix precisionInvScaleInvScale;
  private double constant;

  private Cholesky meanPrecisionChol = null;
  private Cholesky precisionInvScaleChol = null;
  private Cholesky precisionInvScaleInvScaleChol = null;

	/**
	 * Hyperparameters of MVNormal-Gamma-Independent.
	 * @param meanMean
	 * @param meanPrecision
	 * @param precisionDegFreedom
	 * @param precisionInvScale
	 */
	public MVNormalWishartIndependent(
			RealVector meanMean,
			RealMatrix meanPrecision,
			double precisionDegFreedom,
      double precisionInvScaleDegFreedom,
      RealMatrix precisionInvScaleInvScale) {

		this.meanMean = meanMean;
		this.meanPrecision = meanPrecision;
		this.precisionDegFreedom = precisionDegFreedom;
    this.precisionInvScaleDegFreedom = precisionInvScaleDegFreedom;
    this.precisionInvScaleInvScale = precisionInvScaleInvScale;

    meanPrecisionChol = new Cholesky(meanPrecision);
    precisionInvScaleInvScaleChol = new Cholesky(precisionInvScaleInvScale);
    precisionInvScale = precisionInvScaleInvScaleChol
            .getSolver()
            .getInverse()
            .scalarMultiply(precisionInvScaleDegFreedom);
    precisionInvScaleChol = new Cholesky(precisionInvScale);
     
    
    numdim = meanMean.getDimension();
    assert precisionDegFreedom > numdim-1;
    assert precisionInvScaleDegFreedom > numdim-1;
    assert meanPrecision.getRowDimension()==numdim;
    assert precisionInvScaleInvScale.getRowDimension()==numdim;
    constant = .5*(precisionDegFreedom+1.0)*numdim*log(2.0)
               +.25*numdim*(numdim+1)*log(PI)
               -.5*meanPrecisionChol.getLogDeterminant();
    for (int i=1; i<=numdim; i++) {
      constant += logGamma(.5*(precisionDegFreedom+1-i));
    }
	}
  
  
  static boolean useMeanVar = true;
  static double meanRelScale = 1.0;
  static double precisionDegFreedomAdjustment = 3.0;    // has to be > -1
  static double invScaleDegFreedomAdjustment = -0.6;    // has to be > -1 
  static double precisionScale = 50.0;
  
  /**
   * Construct weakly informative, data dependent, prior based on recipe in Favaro and Teh 2013.
   * 
   * @param data Data matrix (first dimension is number of data items, second is dimensionality.
   * @return Weakly informative prior.
   * @throws Error if data format has problems.
   * @throws Exception if data has infinities or NaNs.
   */
  public static MVNormalWishartIndependent constructPrior(double[][] data) throws Error, Exception {
    if (data.length==0) {
      throw new Error("Empty data");
    }
    int dim = data[0].length;
    double precisionDegFreedom = dim + precisionDegFreedomAdjustment;
    double invScaleDegFreedom = dim + invScaleDegFreedomAdjustment;    
    return constructPrior(data,useMeanVar,
            meanRelScale,precisionDegFreedom,
            invScaleDegFreedom,precisionScale);
  }


  /**
   * Construct weakly informative, data dependent, prior based on recipe in Favaro and Teh 2013.
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
  public static MVNormalWishartIndependent constructPrior(double[][] data, boolean useMeanVar,
          double meanRelScale, 
          double precisionDegFreedomAdjustment, 
          double invScaleDegFreedomAdjustment,
          double precisionScale) throws Error, Exception {
    if (data.length==0) {
      throw new Error("Empty data");
    }
    int numdata = data.length;
    int dim = data[0].length;
    for (int i=0; i<numdata; i++) {
      if (dim!=data[i].length)
        throw new Error("Data dimensions not consistent.");
    }
    double precisionDegFreedom = dim + precisionDegFreedomAdjustment;
    double invScaleDegFreedom = dim + invScaleDegFreedomAdjustment;    
    if (precisionDegFreedom<dim-1) {
      throw new Error("PrecisionDegFreedom out of domain");
    }
    if (invScaleDegFreedom<dim-1) {
      throw new Error("invScaleDegFreedom out of domain");
    }

    double invScaleInvScale = 
        precisionScale*invScaleDegFreedom/(precisionDegFreedom-dim-1.0);

    double[] dmin  = new double[dim];
    double[] dmax  = new double[dim];
    double[] dmean = new double[dim];
    double[] dvar  = new double[dim];
    for (int d=0; d<dim; d++) {
      dmin[d]  = Double.POSITIVE_INFINITY;
      dmax[d]  = Double.NEGATIVE_INFINITY;
      dmean[d] = 0.0;
      dvar[d]  = 0.0;
    }
   	for (int i=0; i<data.length; i++) {
      double[] dataitem = data[i];
      for (int d=0; d<dim; d++) {
        double x = dataitem[d];
        if (Double.isInfinite(x)) {
          throw new Exception("Infinity encountered in data");
        }
        if (Double.isNaN(x)) {
          throw new Exception("NaN encountered in data");
        }
        dmean[d] += x;
        dvar[d] += x*x;
  			if (x<dmin[d]) { dmin[d] = x; }
  			if (x>dmax[d]) { dmax[d] = x; }
      }
    }
    for (int d=0; d<dim; d++) {
      dmean[d] /= (double)data.length;
      dvar[d] = (dvar[d]/(double)(data.length) - dmean[d]*dmean[d]) * (double)(data.length) / (double)(data.length-1);
		}
    RealVector meanMean = new ArrayRealVector(dim);
    RealMatrix meanPrecision = new Array2DRowRealMatrix(dim, dim);
    RealMatrix precisionInvScaleInvScale = new Array2DRowRealMatrix(dim, dim);
    for (int d = 0; d < dim; d++) {
      if (useMeanVar) {
        meanMean.setEntry(d, dmean[d]);
      } else {
        meanMean.setEntry(d, .5 * (dmin[d] + dmax[d]));
      }
      double range;
      if (useMeanVar) {
        range = sqrt(dvar[d]);
      } else {
        range = .5 * (dmax[d] - dmin[d]);
      }
      meanPrecision.setEntry(d, d, 1.0 / range / range / meanRelScale);
      precisionInvScaleInvScale.setEntry(d, d, invScaleInvScale / range / range); 
      for (int e = 0; e < d; e++) {
        meanPrecision.setEntry(d, e, 0.0);
        meanPrecision.setEntry(e, d, 0.0);
        precisionInvScaleInvScale.setEntry(d, e, 0.0);
        precisionInvScaleInvScale.setEntry(e, d, 0.0);
      }
    }
    
    return new MVNormalWishartIndependent(
              meanMean, meanPrecision,
              precisionDegFreedom,
              invScaleDegFreedom, precisionInvScaleInvScale);
    
  }
  
  
  
  
	public int numDataDim() {
    int d = meanMean.getDimension();
    return d*(d+1)/2;
  }
  public int numParamDim() { 
    int d = meanMean.getDimension();
    return d*d+1;
  }
	@Override public double logNormalizer() {
		return constant 
           -.5*precisionDegFreedom*precisionInvScaleChol.getLogDeterminant();

	}
	@Override public double logProbability(MVNormal datum) {
    int d = meanMean.getDimension();
		RealVector diff = datum.getMean().subtract(meanMean);
		return  -.5*diff.dotProduct(meanPrecision.operate(diff))
            -.5*traceDot(datum.getPrecision())
            +.5*(precisionDegFreedom-d-1)*datum.getPrecisionLogDeterminant()
						-logNormalizer();
	}
  double traceDot(RealMatrix A) {
    int d = meanMean.getDimension();
    double result = 0.0;
    for (int i=0; i<d; i++) {
      for (int j=0; j<d; j++) {
        result += A.getEntry(i, j)*precisionInvScale.getEntry(i, j);
      }
    }
    return result;
  }
	@Override public MVNormal drawSample() {
		return new MVNormal(
      generator.nextMVNormalMeanPrecision(meanMean,getMeanPrecisionCholesky()),
      generator.nextWishart(precisionDegFreedom,getPrecisionInvScaleCholesky()));
	}
  
  public RealVector getMeanMean() {
    return meanMean;
  }
  public RealMatrix getMeanPrecision() {
    return meanPrecision;
  }
  public double getPrecisionDegFreedom() {
    return precisionDegFreedom;
  }
  
  public RealMatrix getPrecisionInvScale() {
    return precisionInvScale;
  }
  public void setPrecisionInvScale(RealMatrix precisionInvScale) {
    this.precisionInvScale = precisionInvScale;
    precisionInvScaleChol = new Cholesky(precisionInvScale);
  }
  
  Cholesky getMeanPrecisionCholesky() {
    if (meanPrecisionChol==null)
      meanPrecisionChol = new Cholesky(meanPrecision);
    return meanPrecisionChol;
  }
  Cholesky getPrecisionInvScaleCholesky() {
    if (precisionInvScaleChol==null)
      precisionInvScaleChol = new Cholesky(precisionInvScale);
    return precisionInvScaleChol;
  }
	@Override public Double[] getMeanParameter() {
    throw new Error("not done yet");
	}

  public void sample(Collection<? extends XHierarchy> data) {
    double df;
    RealMatrix is = precisionInvScaleInvScale;
    df = precisionInvScaleDegFreedom + precisionDegFreedom*data.size();
    for (XHierarchy d : data) {
      MVNormalNonConjugateHierarchySampled h = (MVNormalNonConjugateHierarchySampled)d;
      is = is.add(h.param.getPrecision());
    }
    setPrecisionInvScale(generator.nextWishart(df, new Cholesky(is)));
  }
  public int getNumDimension() {
    return meanMean.getDimension();
  }

	public enum Properties {
    meanMean,
    meanPrecision,
    precisionDegFreedom,
    precisionInvScale,
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
    case precisionDegFreedom: return precisionDegFreedom;
    case precisionInvScale: return precisionInvScale;
		default: throw new Error("Unknown property "+property);
		}
	}
  @Override public Object get(String property, Object arg) {
    return get(property);
  }


	@Override public String toString() {
		return "NWI(m="+meanMean+",p="+meanPrecision
            +",df="+precisionDegFreedom+",is="+precisionInvScale+")";
	}
}
