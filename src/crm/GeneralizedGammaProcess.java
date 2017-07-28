package crm;

import utilities.Generator;
import java.util.*;

import utilities.SpecialFunctions;

import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import static java.lang.Math.log1p;
import static utilities.Generator.generator;

public class GeneralizedGammaProcess implements LevyProcess {
	
	double alpha, sigma, tau;
	
  public GeneralizedGammaProcess(double alpha, double sigma, double tau) {
    this.alpha = alpha;
    this.sigma = sigma;
    this.tau   = tau;
  }
	
	public double getAlpha() { return alpha; }
	public double getSigma() { return sigma; }
	public double getTau() { return tau; }
	@Override public double logLevy(double x) {
		return logLevy(x,alpha,sigma,tau);
	}
	double logLevy(double x, double alpha, double sigma, double tau) {
		return log(alpha)
				- SpecialFunctions.logGamma(1.0-sigma)
				- (1.0+sigma)*log(x)
				- (tau)*x;
	}
	@Override public double laplace(double u) {
		return laplace(u,alpha,sigma,tau);
	}
	double laplace(double u, double alpha, double sigma, double tau) {
		if (sigma<1e-16) return alpha*log1p(u/tau);
		return alpha/sigma*(Math.pow(tau+u,sigma)-Math.pow(tau,sigma));
	}

	@Override public double meanTotalJump() {
		return alpha*Math.pow(tau, sigma-1.0);
	}
  @Override public double gamma(double a, double b) {
    return exp(logGamma(a,b));
  }
	@Override public double logGamma(double a, double b) {
		return logGamma(a,b,alpha,sigma,tau);
	}
	double logGamma(double a, double b, double alpha, double sigma, double tau) {
		return Math.log(alpha)
				+ SpecialFunctions.logGamma(a-sigma)
				- SpecialFunctions.logGamma(1.0-sigma)
				- (a-sigma)*Math.log(tau+b);
	}
	@Override public double meanGamma(double a, double b) {
		return (a-sigma)/(tau+b);
	}
	@Override public double drawGamma(double a, double b) {
		return generator.nextGamma(a-sigma, tau+b);
	}
	@Override public double drawGamma(double a, double b, Generator gen) {
		return gen.nextGamma(a-sigma, tau+b);
	}
	
	double W(double s, double t) {
		return alpha/SpecialFunctions.gamma(1-sigma)*pow(t,-1.0-sigma)*exp(-s*(tau));
	}
	double invWInt(double x, double t) {
		double a= log(x*(tau));
		double b = logLevy(t,alpha,sigma,tau);
		if (a>=b) return Double.POSITIVE_INFINITY;
		if (b>a+33.0) return (t+exp(a-b))/(tau);
		else return t-log(1.0-exp(a-b))/(tau);
	}
	@Override public double[] drawJumps(double minslice) {
		// sample empty clusters
		double s = minslice;
		ArrayList<Double> masses = new ArrayList<Double>();
		while (true) {
			double e = generator.nextExponential(1.0);
			double news = invWInt(e,s);
			if (news==Double.POSITIVE_INFINITY) {
        break;
      }
			if (generator.nextBoolean(exp(logLevy(news))/W(news,s))) {
				masses.add(news);
			}
      System.out.println(news);
			s = news;
		}
		Collections.reverse(masses);
		double[] result = new double[masses.size()];
		for (int i=0; i<masses.size(); i++) 
			result[i] = masses.get(i);
		return result;
	}

  @Override public GeneralizedGammaProcess expTilt(double u) {
    return new GeneralizedGammaProcess(alpha,sigma,tau+u);
  }

	@Override public String toString() {
		return "NGGP(a="+alpha+",s="+sigma+",t="+tau+")";
	}

}
