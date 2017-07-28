package utilities;

import static java.lang.Math.*;

/**
 *
 * @author ywteh
 */
public final class SpecialFunctions {

	/**
	 * 
	 * @param x Real input.
	 * @return Sigmoid function: 1/(1+exp(-x))
	 */
	public static double sigmoid(double x) { return 1/(1+exp(-x)); }

	/**
	 * 
	 * @param x Real input between 0 and 1.
	 * @return Inverse of the sigmoid function: log(x)-log(1-x)
	 */
	public static double logit(double x) { return log(x)-log1p(-x); }

	/**
	 * 
	 * @param x Real number.
	 * @param y Real number.
	 * @return log(exp(x)+exp(y))
	 */
	public static double logsumexp(double x, double y) {
	    if (x<y) return y+log1p(exp(x-y));
	    else return x+log1p(exp(y-x));
	  }
	
	/**
	 * 
	 * @param x Real number.
	 * @return log(1+exp(x))
	 */
	public static double log1exp(double x) {
	  if (x<-33.0) return 0.0;
	  if (x>+33.0) return x;
	  return log1p(exp(x));
	}
  
  
	/**
	 * Log Gamma function.
 	 * From numerical recipes.
	 * 
	 * @param x
	 * @return log Gamma(x)
	 */
	public static double logGamma(double x) {
		if ( x==0.0 ) return Double.POSITIVE_INFINITY;
		double tmp = x+5.5;
		tmp -= (x+0.5)*Math.log(tmp);
		double ser = 1.000000000190015
				+76.18009172947146 / (x+1) -86.50532032941677 / (x+2)
				+24.01409824083091 / (x+3) -1.231739572450155 / (x+4)
				+0.1208650973866179e-2 / (x+5) -0.5395239384953e-5 / (x+6);
		return Math.log(2.5066282746310005*ser/x)-tmp;
	}
	/**
	 * Gamma function.
	 * @param x
	 * @return Gamma(x)
	 */
	public static double gamma(double x) {
		return exp(logGamma(x));
	}

	/**
	 * logarithm of Kramp's symbol. 
	 * 
	 * @param c
	 * @param a
	 * @param b
	 * @return  [c]^{a}_{b} = (c)*(c+b)*...*(c+(a-1)*b)
	 */
	public static double logKramps(double c, double a, double b) {
		if (DoubleComparator.isEqual(a+c/b,0.0) ||
			DoubleComparator.isEqual(c/b,0.0) ||
			(a+c/b < 0.0) ||
			(c/b < 0.0)) {
			double result = 0.0;
			for(int i=0;i<a;i++) {
				result += Math.log(c + i*b);
			}
			return result;
		} else {
			return Math.log(b)*a + logGamma(a + c/b) - logGamma(c/b);
		}
	}


  public static double logChoose(double nn,double xx)
  {
    return logGamma(1.0+nn) - logGamma(1.0+nn-xx) - logGamma(1.0+xx) ;
  }

  public static double logBeta(double aa, double bb) {
    if ( aa<=0.0 || bb<=0.0 ) return Double.POSITIVE_INFINITY;
    return logGamma(aa) + logGamma(bb) - logGamma(aa+bb);
  }

  /**
   * Compute logarithm of unsigned Stirling number of first kind log(s(n,m)). 
   * Use Temme (Studies in Applied Mathematics 89:233-243 1993).
   * Note that accuracy is limited!
   * 
   * @param nn
   * @param mm
   * @return
   */
  public static double logStirling1(double nn, double mm) {

    // boundary conditions
    if (DoubleComparator.isEqual(nn,mm)) {
      return 0.0;
    } else if (DoubleComparator.isEqual(mm,1.0) && nn>1.0) {
      return logGamma(nn);
    } else if (mm < 1.0 || mm > nn) {
      return Double.NEGATIVE_INFINITY;
    }

    double n = nn-1;
    double m = mm-1;

    double t0 = m / (n-m);
    // Solve for x0 using Newton's method.
    double x0 = .5*log(n*m/(n-m));
    int converged = 0;
    for ( int ii = 1; ii <= 20; ii++ ) {
      double oldx = x0;
      x0 -= (exp(x0)*(digamma(exp(x0)+n+1)-digamma(exp(x0)+1))-m)/
           (exp(x0)*(digamma(exp(x0)+n+1)-digamma(exp(x0)+1)) +
            exp(2*x0)*(trigamma(exp(x0)+n+1)-trigamma(exp(x0)+1)));
      if (abs(oldx-x0)<1e-10) {
        converged = 1;
        break;
      }
    }
    if (converged == 0)
      System.out.print("logstirling1>solvex0: Newton steps did not converge.");
    x0 = exp(x0);

    return logGamma(x0+n+1) - logGamma(x0+1) - (m+1)*log(x0)
            - n*log(t0+1) + m*log(t0)
            + .5*log(m*(n-m)/(n*(trigamma(x0+n+1)-trigamma(x0+1)+m/x0/x0)))
            + logGamma(n+1) - logGamma(m+1) - logGamma(n-m+1);
  }


  /** The digamma function is the derivative of gammaln.
    * Translated from Tom Minka's lightspeed package.
    *
    * Reference:
    *  J Bernardo,
    *  Psi ( Digamma ) Function,
    *  Algorithm AS 103,
    *  Applied Statistics,
    *  Volume 25, Number 3, pages 315-317, 1976.
    *  From http://www.psc.edu/~burkardt/src/dirichlet/dirichlet.f
    *  (with modifications for negative numbers and extra precision)
    */
    public static double digamma(double x) {
      final double c = 12;
      final double d1 = -0.57721566490153286;
      final double d2 = 1.6449340668482264365; /* pi^2/6 */
      final double s = 1e-6;
      final double s3 = 1.0/12.0;
      final double s4 = 1.0/120.0;
      final double s5 = 1.0/252.0;
      final double s6 = 1.0/240.0;
      final double s7 = 1.0/132.0;
      //final double s8 = 691.0/32760.0;
      //final double s9 = 1.0/12.0;
      //final double s10 = 3617.0/8160.0;
      double result;

      /* Illegal arguments */
      if((x == Double.NEGATIVE_INFINITY) || x == Double.NaN )
        return Double.NaN;

     /* Singularities */
     if((x <= 0.0) && (floor(x) == x))
       return Double.NEGATIVE_INFINITY;

     /* Negative values */
     /* Use the reflection formula (Jeffrey 11.1.6):
      * digamma(-x) = digamma(x+1) + pi*cot(pi*x)
      *
      * This is related to the identity
      * digamma(-x) = digamma(x+1) - digamma(z) + digamma(1-z)
      * where z is the fractional part of x
      * For example:
      * digamma(-3.1) = 1/3.1 + 1/2.1 + 1/1.1 + 1/0.1 + digamma(1-0.1)
      *               = digamma(4.1) - digamma(0.1) + digamma(1-0.1)
      * Then we use
      * digamma(1-z) - digamma(z) = pi*cot(pi*z)
      */
     if(x < 0.0) 
       return digamma(1-x) + PI/tan(-PI*x);
  
     /* Use Taylor series if argument <= S */
     if(x <= s) return d1 - 1/x + d2*x;
     /* Reduce to digamma(X + N) where (X + N) >= C */
     result = 0.0;
     while(x < c) {
       result -= 1.0/x;
       x++;
     }
     /* Use de Moivre's expansion if argument >= C */
     /* This expansion can be computed in Maple via asympt(Psi(x),x) */
     if(x >= c) {
       double r = 1/x;
       result += log(x) - 0.5*r;
       r *= r;
       result -= r * (s3 - r * (s4 - r * (s5 - r * (s6 - r * s7))));
     }
     return result;
   }

   /** The trigamma function is the derivative of the digamma function.
     * Translated from Tom Minka's lightspeed package.
     *
     * Reference:

     * B Schneider,
     * Trigamma Function,
     * Algorithm AS 121,
     * Applied Statistics,
     * Volume 27, Number 1, page 97-99, 1978.
     *
     * From http://www.psc.edu/~burkardt/src/dirichlet/dirichlet.f
     * (with modification for negative arguments and extra precision)
     */
   public static double trigamma(double x) {
    final double small = 1e-4,
      large = 8.0,
      c = 1.6449340668482264365, /* pi^2/6 = Zeta(2) */
      c1 = -2.404113806319188570799476,  /* -2 Zeta(3) */
      b2 =  1.0/6.0,
      b4 = -1.0/30.0,
      b6 =  1.0/42.0,
      b8 = -1.0/30.0,
      b10 = 5.0/66.0;
    double result;
    /* Illegal arguments */
    if((x == Double.NEGATIVE_INFINITY) || Double.isNaN(x))
      return Double.NaN;

    /* Singularities */
    if((x <= 0.0) && (floor(x) == x))
      return Double.NEGATIVE_INFINITY;
  
    /* Negative values */
    /* Use the derivative of the digamma reflection formula:
     * -trigamma(-x) = trigamma(x+1) - (pi*csc(pi*x))^2
     */
    if(x < 0.0) {
      result = PI/sin(-PI*x);
      return -trigamma(1.0-x) + result*result;
    }
    /* Use Taylor series if argument <= small */
    if(x <= small) 
      return 1.0/(x*x) + c + c1*x;
    
    result = 0;
    /* Reduce to trigamma(x+n) where ( X + N ) >= B */
    while(x < large) {
      result += 1.0/(x*x);
      x++;
    }
    /* Apply asymptotic formula when X >= B */
    /* This expansion can be computed in Maple via asympt(Psi(1,x),x) */
    if(x >= large) {
      double r = 1/(x*x);
      result += 0.5*r + (1 + r*(b2 + r*(b4 + r*(b6 + r*(b8 + r*b10)))))/x;
    }
    return result;
  }


  private static boolean machEpsKnown = false;
  private static double machEps = 1.0;

  public static double getMachEps() {
    if (!machEpsKnown) {
      do {
        machEps /= 2.0;
      } while ((1.0+(machEps/2.0))!=1.0);
      machEpsKnown = true;
    }
    return machEps;
  }

  public static double harmonicNumber(int n) {
      assert n >= 0; // works for n=0 as well, equals 0.

      // TODO Use the log(n) + Gamma approximation for large n
      double h = 0;
      for (int i = 1; i <= n; i++)
          h += 1. / (double)i;

      return h;
  }
}
