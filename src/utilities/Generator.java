package utilities;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import LinearAlgebra.Cholesky;
import java.util.*;
import static java.lang.Math.*;
import static utilities.SpecialFunctions.logsumexp;

/* Some more useful random number generators.
 *
 * double nextUniform(double aa, double bb);
 * double nextUniform(double bb);
 * int nextUniform(int ss);
 * int nextBernoulli(double pp);
 * int nextMultinomial(double[] pp);
 * double nextExponential()
 * double nextExponential(double rate)
 * double nextTruncatedExponential(double rate, double lower, double upper)
 * double nextGamma(double shape)
 * double nextGamma(double shape, double invscale)
 * double nextBeta(double aa, double bb)
 * double[] nextDirichlet(double[] aa)
 */

/**
 * Some useful random number generators.
 * @author ywteh
 */
public class Generator extends Random {
  /**
   * System-wide random number generator.
   */
  public static Generator generator = new Generator();
  public Generator() { 
    super();
    long seed = nextLong();
    //seed = 55555555555555555L;
    //seed = -3154027301510616377L;
    //System.out.println("SEED="+seed);
    setSeed(seed);
  }
  /**
   * Returns sample from U[a,b].
   */
  public double nextUniform(double a, double b) {
    return a+nextDouble()*(b-a);
  }
  /**
   * Returns sample from U[0,b].
   */
  public double nextUniform(double b) {
    return nextDouble()*b;
  }
  /**
   * Returns sample from U[0,1].
   */
  public double nextUniform() { return nextDouble(); }

  /**
   * Returns integer uniformly distributed between 0 and s-1.
   */
  public int nextUniform(int s) { /* between 0 and ss-1 */
    return super.nextInt(s);
  }

  /**
   * Returns an item picked uniformly from a collection.
   */
  public <T> T nextUniform(Collection<T> set) {
    int rr = nextUniform(set.size());
    for ( T element : set ) {
      rr--;
      if (rr==-1) return element;
    }
    return null;
  }

  private double KR_f(double x) {
    return exp(-.5*x*x)/sqrt(2*PI) -0.180025191068563*max(2.2160358671 - abs(x), 0.0);    
  }
  /**
   * Alternative N(0,1) random number generator. Hopefully faster.
   * @return 
   */
  public double nextGaussian2() {
    final double xi = 2.2160358671;
    double u = nextUniform();
    if (u<.884070402298758) {
      double v = nextUniform();
      return xi*(1.131131635444180 * u + v - 1.0);
    } else if (u>.973310954173898) {
      double v = nextUniform();
      double w = nextUniform();
      while (true) {
        double t = .5*xi*xi - log(w);
        if (v*v*t < 5*xi*xi) {
          return (u<.986655477086949) ? 2.0*t : -2.0*t;
        }
      }
    } else if (u>.958720824790463) {
      while (true) {
        double v = nextUniform();
        double w = nextUniform();
        double z = v-w;
        double t = xi -.630834801921960*min(v,w);
        if (max(v,w)<=.755591531667601 || .034240503750111*abs(z) <= KR_f(t)) {
          return (z<0) ? t : -t;
        }
      }
    } else if (u>.911312780288703) {
      while (true) {
        double v = nextUniform();
        double w = nextUniform();
        double z = v-w;
        double t = .479727404222441 + 1.105473661022070*min(v,w);
        if (max(v,w)<=.872834976671790 || .049264496373128*abs(z) <= KR_f (t)) {
          return (z<0) ? t : -t;
        }
      }
    } else {
      while (true) {
        double v = nextUniform();
        double w = nextUniform();
        double z = v-w;
        double t = .479727404222441 - .595507138015940*min(v, w);
        if (t>0 && (max(v,w)<=.805577924423817 || .053377549506886*abs(z) <= KR_f(t))) {
          return (z<0) ? t : -t;
        }
      }
    }
  }
  
  /**
   * Returns sample from N(mu,var).
   */
  public double nextGaussian(double mu, double var) {
    return nextGaussian()*sqrt(var) + mu;
  }
  public RealVector nextMVNormal(int numdim) {
    RealVector x = new ArrayRealVector(numdim);
    for (int i=0; i<numdim; i++)
      x.setEntry(i, nextGaussian());
    return x;
  }
  public RealVector nextMVNormalMeanCovariance(RealVector mu, Cholesky covariance) {
    RealVector x = nextMVNormal(mu.getDimension());
    return mu.add(covariance.getL().operate(x));
  }
  public RealVector nextMVNormalMeanPrecision(RealVector mu, Cholesky precision) {
    RealVector x = nextMVNormal(mu.getDimension());
    return mu.add(precision.solveLT(x));
  }
  public RealMatrix nextWishart(double df, Cholesky invscale) {
    int d = invscale.getL().getColumnDimension();
    Array2DRowRealMatrix A = new Array2DRowRealMatrix(d,d);
    ArrayRealVector v = new ArrayRealVector(d);
    for (int i=0; i<d; i++) {
      v.setEntry(i, sqrt(nextChiSquared(df-i)));
      for (int j=0; j<i; j++) {
        v.setEntry(j, 0.0);
      }
      for (int j=i+1; j<d; j++) {
        v.setEntry(j, nextGaussian());
      }
      A.setColumnVector(i, invscale.solveLT(v));
    }
    return A.multiply(A.transpose());
  }

  /**
   * Returns sample from Bernoulli(p).
   * @param p Mean of Bernoulli.
   */
  public int nextBernoulli(double p) {
    if ( nextDouble() < p ) return 1;
    return 0;
  }
  /**
   * Returns true with probability p, false otherwise.
   * @param p
   */
  public boolean nextBoolean(double pp) {
	  return nextDouble() < pp;
  }

  /**
   * Returns i with probability proportional to p[i].
   */
  public int nextMultinomial(double[] pp) {
    return nextMultinomial(pp,0,pp.length);
  }
  public int nextMultinomial(double[] pp, int start, int len) {
    double sum = 0.0;
    int end = start+len;
    int ii;
    for ( ii=start; ii<end; ii++ )
      sum += pp[ii];
    double rr = nextDouble()*sum;
    for ( ii=start; ii<end; ii++ ) {
      rr -= pp[ii];
      if (rr <= 0.0) break;
    }
    assert rr <= 0.0;
    return ii;
  }

  /**
   * Returns i with probability proportional to p.get(i).
   */
  public int nextMultinomial(List<Double> p) {
    double sum = 0.0;
    for (Double xx : p) {
      sum += xx;
    }
    double rr = nextDouble()*sum;
    int ii = 0;
    for (Double xx : p) {
      rr -= xx;
      if (rr <= 0.0)
        break;
      ii++;
    }
    return ii;
  }

  /**
   * Returns key with probability proportional to p.get(key)
   * @return
   */
  public <O> O nextMultinomial(Map<O,Double> p) {
    double sum = 0.0;
    for ( Double xx : p.values() )
      sum += xx;

    double rr = nextDouble()*sum;
    for ( O obj : p.keySet() ) {
      rr -= p.get(obj);
      if ( rr <= 0.0 ) return obj;
    }
    throw new Error("nextMultinomial(Map<O,Double>) unexpected error.");
  }

  /**
   * Returns sample from Exp(1).
   */
  public double nextExponential() { 
    return -log(nextDouble());
  }

  /**
   * Returns sample from Exp(rate) with given rate.
   */
  public double nextExponential(double rate) {
    return nextExponential()/rate;
  }


  /**
   * Returns sample from a truncated exponential over interval [lower,upper].
   * @param rate Rate of exponential (can be negative).
   */
  public double nextTruncatedExponential(double rate, double lower, double upper) {
    if ( upper == Double.POSITIVE_INFINITY  ) {
      return lower - log(nextDouble())/rate;
    } else if ( lower == Double.NEGATIVE_INFINITY ) {
      return upper - log(nextDouble())/rate;
    } else if ( rate != 0.0 ) {
      double diff = upper-lower;
      double rdiff = rate*diff;
      return lower + log1p(nextDouble()*expm1(rdiff))/rate;
    } else {
      return lower + nextDouble()*(upper-lower);
    }
  }


  /**
   * Returns sample from Gamma(shape,1).
   *
   * Algorithm from Devroye (1986).
   */
  public double nextGamma(double shape) {
    double bb, cc, dd;
    double uu, vv, ww, xx, yy, zz;

    if ( shape <= 0.0 ) {
      /* Not well defined, set to zero and skip. */
      return 0.0;
    } else if ( shape == 1.0 ) {
      /* Exponential */
      return nextExponential();
    } else if ( shape < 1.0 ) {
      /* Use Johnks generator */
      cc = 1.0 / shape;
      dd = 1.0 / (1.0-shape);
      while (true) {
        xx = pow(nextDouble(), cc);
        yy = xx + pow(nextDouble(), dd);
        if ( yy <= 1.0 ) {
          return - log(nextDouble()) * xx / yy;
        }
      }
    } else { /* shape > 1.0 */
      /* Use bests algorithm */
      bb = shape - 1.0;
      cc = 3.0 * shape - 0.75;
      while (true) {
        uu = nextDouble();
        vv = nextDouble();
        ww = uu * (1.0 - uu);
        yy = sqrt(cc / ww) * (uu - 0.5);
        xx = bb + yy;
        if (xx >= 0) {
          zz = 64.0 * ww * ww * ww * vv * vv;
          if ( ( zz <= (1.0 - 2.0 * yy * yy / xx) ) ||
               ( log(zz) <= 2.0 * (bb * log(xx / bb) - yy) ) ) {
            return xx;
          }
        }
      }
    }
  }
  public double nextLogGamma(double shape) {
    double bb, cc, dd;
    double uu, vv, ww, xx, yy, zz;

    if ( shape <= 0.0 ) {
      /* Not well defined, set to zero and skip. */
      return 0.0;
    } else if ( shape == 1.0 ) {
      /* Exponential */
      return log(nextExponential());
    } else if ( shape < 1.0 ) {
      /* Use Johnks generator */
      cc = 1.0 / shape;
      dd = 1.0 / (1.0-shape);
      while (true) {
        xx = cc * log(nextDouble());
        yy = logsumexp(xx,dd * log(nextDouble()));
        if ( yy <= 0.0 ) {
          return log(- log(nextDouble())) + xx - yy;
        }
      }
    } else { /* shape > 1.0 */
      /* Use bests algorithm */
      bb = shape - 1.0;
      cc = 3.0 * shape - 0.75;
      while (true) {
        uu = nextDouble();
        vv = nextDouble();
        ww = uu * (1.0 - uu);
        yy = sqrt(cc / ww) * (uu - 0.5);
        xx = bb + yy;
        if (xx >= 0) {
          zz = 64.0 * ww * ww * ww * vv * vv;
          if ( ( zz <= (1.0 - 2.0 * yy * yy / xx) ) ||
               ( log(zz) <= 2.0 * (bb * log(xx / bb) - yy) ) ) {
            return log(xx);
          }
        }
      }
    }
  }

  public double nextGamma2(double shape) {
    if (shape==1.0) {
      return nextExponential();
    }
    boolean small = shape<1.0;
    double a;
    if (small) {
      a = shape + 1.0;
    } else {
      a = shape;
    }
    double d = a - 1/3;
    double c = 1./sqrt(9*d);
    double x;
    double v;
    double u;
    do {
      do {
        x = this.nextGaussian2();
        v = 1+c*x;
      } while (v<=0.0);
      v = v*v*v;
      u = nextUniform();
    } while ((u>=1-.0331*(x*x)*(x*x)) && (log(u)>=.5*x*x+d*(1-v+log(v))));
    if (small) {
      return d*v*pow(nextUniform(),1.0/shape);
    } else {
      return d*v;
    }
  }
  public double nextLogGamma2(double shape) {
    if (shape==1.0) {
      return nextExponential();
    }
    boolean small = shape<1.0;
    double a;
    if (small) {
      a = shape + 1.0;
    } else {
      a = shape;
    }
    double d = a - 1/3;
    double c = 1./sqrt(9*d);
    double x;
    double v;
    double u;
    do {
      do {
        x = this.nextGaussian2();
        v = 1+c*x;
      } while (v<=0.0);
      v = v*v*v;
      u = nextUniform();
    } while ((u>=1-.0331*(x*x)*(x*x)) && (log(u)>=.5*x*x+d*(1-v+log(v))));
    if (small) {
      return log(d) + log(v) + log(nextUniform())/shape;
    } else {
      return log(d) + log(v);
    }
  }

  /**
   * Returns sample from Gamma(shape,invscale).
   * @param shape Shape parameter.
   * @param invscale Inverse scale parameter.
   */
  public double nextGamma(double shape, double invscale) {
    return nextGamma(shape)/invscale;
  }


  /**
   * Returns sample from ChiSquared(degFreedom).
   * @param degfreedom
   */
  public double nextChiSquared(double degfreedom) {
    return nextGamma(.5*degfreedom,.5)+1e-16;
  }
  /**
   * Returns sample from Beta(a,b).
   *
   * Algorithm just samples two Gamma's and normalize.
   */
  public double nextBeta(double a, double b) {
    if (a==0.0 && b==0.0) {
      return nextBernoulli(0.5);
    }
    a = nextGamma(a);
    b = nextGamma(b);
    return a/(a+b);
  }

  /**
   * Returns sample from Dirichlet(a).
   * @param a Array of Dirichlet parameters.
   * @return
   */
  public double[] nextDirichlet(double[] aa) {
    double[] gg = new double[aa.length];
    double sum = 0.0;
    for ( int ii = 0 ; ii < aa.length ; ii++ )
      sum += gg[ii] = nextGamma(aa[ii]);
    for ( int ii = 0 ; ii < aa.length ; ii++ )
      gg[ii] /= sum;
    return gg;
  }

  /**
   * Returns the table that a new customer sits at in a Chinese restaurant
   * process (CRP).  A CRP partition (seating arrangement) is represented by a
   * collection of tables, each of which is a collection of customers.
   * @param <T> Table type.
   * @param mass Mass parameter of CRP (>0).
   */
  public <T extends Collection> T nextCRPTable(double mass, Collection<T> partition) {
    return nextCRPTable(mass,0.0,partition);
  }
  /**
   * Returns the table that a new customer sits at in a two-parameter
   * Chinese restaurant process (CRP).
   * A CRP partition (seating arrangement) is represented by a collection of
   * tables, each of which is a collection of customers.
   *
   * @param <T> Table type.
   * @param mass Mass parameter of CRP (>-discount).
   * @param discount Discount parameter of CRP (0<=discount<1).
   */
  public <T extends Collection> T nextCRPTable(double mass, double discount,
          Collection<T> partition) {
    if (discount < 0.0 || discount>= 1.0 || mass < -discount)
      throw new Error("CRP parameters out of range.");
    double rr = nextUniform(mass + partition.size());
    for ( T table : partition ) {
      rr -= ((double)table.size()) - discount;
      if ( rr < 0.0 ) {
        return table;
      }
    }
    return null;
  }

  /**
   * Returns a sample from the distribution over the number of tables in a
   * one-parameter Chinese restaurant process (CRP) with numcustomer number of
   * customers.
   * @param mass Mass parameter (>0).
   * @param numcustomer Number of customers in restaurant.
   * @return
   */
  public int nextCRPNumTable(double mass, int numcustomer) {
    return nextCRPNumTable(mass,0.0,numcustomer);
  }

  /**
   * Returns a sample from the distribution over the number of tables in a
   * two-parameter Chinese restaurant process (CRP) with numcustomer number of
   * customers.
   * @param mass Mass parameter (>-discount).
   * @param discount Discount parameter (0<=discount<1).
   * @param numcustomer Number of customers in restaurant.
   * @return
   */
  public int nextCRPNumTable(double mass, double discount, int numcustomer) {
    if (numcustomer == 0) return 0;
    if (numcustomer < 0 || discount < 0.0 ||
         discount>= 1.0 || mass < -discount)
      throw new Error("CRP parameters out of range.");
    int numtable = 1;
    for ( int ii = 1 ; ii < numcustomer ; ii ++ ) {
      double prob = (mass+numtable*discount)/(mass+ii);
      numtable += nextBernoulli(prob);
    }
    return numtable;
  }

  
  public void nextShuffle(List list) {
      Collections.shuffle(list, this);
  }
  
  
  public static void main(String[] args) {
    // test wishart
    double[][] s = {{2.0,1.0,0.0},{1.0,2.0,1.0},{0.0,1.0,2.0}};
    RealMatrix S = new Array2DRowRealMatrix(s);
    Cholesky C = new Cholesky(S);
    double df = 2.4;
    RealMatrix sum = new Array2DRowRealMatrix(3,3);
    for (int i=0; i<100000; i++) {
      RealMatrix sample = generator.nextWishart(df, C);
      sum = sum.add(sample);
    }
    sum = sum.scalarMultiply(1.0/100000.0);
    System.out.println(sum.getRowVector(0));
    System.out.println(sum.getRowVector(1));
    System.out.println(sum.getRowVector(2));
  }
  
}

