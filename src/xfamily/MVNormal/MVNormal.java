package xfamily.MVNormal;

import LinearAlgebra.Cholesky;
import static java.lang.Math.PI;
import static java.lang.Math.log;
import org.apache.commons.math3.linear.*;
import static utilities.Generator.generator;
import xfamily.XFamily;

public class MVNormal implements XFamily<RealVector> {

  private static double halflog2pi = .5 * log(2.0 * PI);
  private RealVector mean;
  private RealMatrix precision;
  private int numdim;
  private double constant;
  private double logdet;

  /**
   * @param mean
   * @param precision
   */
  public MVNormal(RealVector mean, RealMatrix precision, double precisionLogDet) {
    this.mean = mean;
    this.precision = precision;
    numdim = mean.getDimension();
    if (numdim != precision.getColumnDimension()
            || numdim != precision.getRowDimension()) {
      throw new Error("MVNormal parameter dimensions do not match.");
    }
    constant = halflog2pi * numdim;
    logdet = precisionLogDet;
  }

  /**
   * @param mean
   * @param precision
   */
  public MVNormal(RealVector mean, RealMatrix precision) {
    this(mean, precision, new Cholesky(precision).getLogDeterminant());
  }

  @Override
  public int numDataDim() {
    return numdim;
  }

  @Override
  public int numParamDim() {
    return numdim * (numdim + 1) / 2;
  }

  @Override
  public double logNormalizer() {
    return constant - .5 * logdet;
  }

  @Override
  public double logProbability(RealVector datum) {
    RealVector vec = datum.subtract(mean);
    return -.5 * vec.dotProduct(precision.operate(vec)) - logNormalizer();
  }
  Cholesky chol = null;

  @Override
  public RealVector drawSample() {
    if (chol == null) {
      chol = new Cholesky(precision);
    }
    return generator.nextMVNormalMeanPrecision(mean, chol);
  }

  @Override
  public Double[] getMeanParameter() {
    throw new Error("not implemented");
  }

  double getPrecisionLogDeterminant() {
    return logdet;
  }

  double precisionDot(RealMatrix A) {
    int d = mean.getDimension();
    double result = 0.0;
    for (int i = 0; i < d; i++) {
      for (int j = 0; j < d; j++) {
        result += A.getEntry(i, j) * precision.getEntry(i, j);
      }
    }
    return result;
  }

  public RealVector getMean() {
    return mean;
  }
  public void setMean(RealVector mean) {
    this.mean = mean;
  }
  public RealMatrix getPrecision() {
    return precision;
  }
  public void setPrecision(RealMatrix precision) {
    this.precision = precision;
    logdet = new Cholesky(precision).getLogDeterminant();
  }

  public enum Properties {

    mean,
    precision,
    covariance,
    NOVALUE;

    public static Properties toValue(String str) {
      try {
        return valueOf(str);
      } catch (Exception ex) {
        return NOVALUE;
      }
    }
  }

  @Override
  public Object get(String property) {
    switch (Properties.toValue(property)) {
      case mean:
        return mean;
      case precision:
        return getPrecision();
      case covariance:
        throw new Error("not implemented yet");
      default:
        throw new Error("Unknown property " + property);
    }
  }

  @Override
  public Object get(String property, Object arg) {
    return get(property);
  }

  @Override
  public String toString() {
    return "N(m=" + mean + ",p=" + getPrecision() + ")";
  }
}
