package nrmi;

import java.util.*;

import utilities.SpecialFunctions;

import mcmc.kernel.Metropolis;
import mcmc.kernel.SliceFiniteInterval;
import mixture.Cluster;

import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import static java.lang.Math.max;
import static java.lang.Math.log1p;
import static utilities.Generator.generator;
import static utilities.SpecialFunctions.gamma;

public class NGGP extends NRMI {
	
	double alpha, sigma, tau;
	double Ashape, Ainvscale; 
	double Salpha, Sbeta;
	double Tshape, Tinvscale;
  double minSlice, maxClusters;
  static double default_minslice = 1.0e-7;
  static int default_maxclusters = 1000000;
  int numBelowMinSlice = 0;
  int numAboveMaxClusters = 0;
	
	public NGGP(double Ashape, double Ainvscale, 
			double Salpha, double Sbeta, 
			double Tshape, double Tinvscale) {
    this(Ashape,Ainvscale,Salpha,Sbeta,Tshape,Tinvscale,
            default_minslice,default_maxclusters);
  }
  
	public NGGP(double Ashape, double Ainvscale, 
			double Salpha, double Sbeta, 
			double Tshape, double Tinvscale,
      double minslice, double maxclusters) {
		this.Ashape = Ashape;
		this.Ainvscale = Ainvscale;
		this.Salpha = Salpha;
		this.Sbeta = Sbeta;
		this.Tshape = Tshape;
		this.Tinvscale = Tinvscale;
		alpha = (Ashape+1.0)/(Ainvscale+1.0);
		sigma = 0.1;
		tau = (Tshape+1.0)/(Tinvscale+1.0);
    this.minSlice = minslice;
    this.maxClusters = maxclusters;
	}
  public NGGP(double alpha, double sigma, double tau) {
    this(alpha,sigma,tau,
            default_minslice,default_maxclusters);
  }
  public NGGP(double alpha, double sigma, double tau,
          double minslice, double maxclusters) {
    this.alpha = alpha;
    this.sigma = sigma;
    this.tau   = tau;
    this.Ashape    = alpha*1e6;
    this.Ainvscale = 1e6;
    this.Salpha    = sigma*1e6;
    this.Sbeta     = (1-sigma)*1e6;
    this.Tshape    = tau*1e6;
    this.Tinvscale = 1e6;
    this.minSlice = minslice;
    this.maxClusters = maxclusters;
  }
	
	public double getAlpha() { return alpha; }
	public double getSigma() { return sigma; }
	public double getTau() { return tau; }
  public double getLogTau() { return log(tau); }
	@Override public double logLevy(double mass) {
		return logLevy(mass,alpha,sigma,tau,0.0);
	}
	@Override public double logLevy(double mass, double logu) {
		return logLevy(mass,alpha,sigma,tau,logu);
	}
	double logLevy(double mass, double alpha, double sigma, double tau, double logu) {
		return log(alpha)
				- SpecialFunctions.logGamma(1.0-sigma)
				- (1.0+sigma)*log(mass)
				- (exp(logu)+tau)*mass;
	}
	@Override public double laplace(double logu) {
		return laplace(alpha,sigma,tau,logu);
	}
	double laplace(double alpha, double sigma, double tau, double logu) {
		if (sigma<1e-16) return alpha*log1p(exp(logu)/tau);
		return alpha/sigma*(Math.pow(tau+exp(logu),sigma)-Math.pow(tau,sigma));
	}
	@Override public double logGamma(int num, double logu) {
		return logGamma(alpha,sigma,tau,num,logu);
	}
	double logGamma(double alpha, double sigma, double tau, int num, double logu) {
		return Math.log(alpha)
				- SpecialFunctions.logGamma(1.0-sigma)
				+ SpecialFunctions.logGamma(num-sigma)
				- (num-sigma)*Math.log(tau+exp(logu));
	}
	@Override public double logMeanMass(int num, double logu) {
		return log((double)num-sigma) - log(tau+exp(logu));
	}
	@Override public double logMeanTotalMass(int numclusters, double logu) {
		return log(alpha) + (sigma-1.0)*log(tau+exp(logu));
	}
	@Override public double drawLogMass(int num, double logu) {
		return log(generator.nextGamma(num-sigma)) - log(tau+exp(logu));
	}
	
	@Override public double meanNumClusters(int num, double u) {
		System.out.println("NGGP.meanNumClusters: dont know answer yet");
		return alpha*log(1+num/alpha);
	}
	

	@Override public <H extends Cluster> void sample(int numData, Collection<H> clusters) {
    super.sample(numData, clusters);
		sampleAlpha(clusters);
		sigmasampler.sample(clusters);
    tausampler.sample(clusters);
	}
	
	<C extends Cluster<?>> void sampleAlpha(Collection<C> clusters) {
		alpha = generator.nextGamma(
				Ashape + clusters.size(), 
				Ainvscale + ((sigma<1e-16) 
						? log1p(exp(logU)/tau) 
						: (pow(tau+exp(logU),sigma)-pow(tau,sigma))/sigma));
	}
	class SigmaSampler<C extends Cluster<?>> extends SliceFiniteInterval {
		Collection<C> clusters = null;
		public SigmaSampler() {
			super(0.0,1.0,generator);
		}
		public void sample(Collection<C> clusters) {
			this.clusters = clusters;
			sigma = sample(sigma);
		}
		@Override public double logDensity(double x) {
			double result = 
					(Salpha-1.0)*log(max(1e-16,x))
					+ (Sbeta-1.0)*log(max(1e-16,1.0-x))
					- laplace(alpha,x,tau,logU);
			for ( C cc : clusters ) {
				if (!cc.isEmpty())
					result += logGamma(alpha,x,tau,cc.number,logU);
			}
			return result;
		}		
	}
	SigmaSampler sigmasampler = new SigmaSampler();
	
	class TauSampler<H extends Cluster> extends Metropolis<Double> {
		Collection<H> clusters = null;
		@Override public Double propose(Double x) { return x*exp(.5*generator.nextGaussian()); }
		public void sample(Collection<H> clusters) {
			this.clusters = clusters;
			tau = sample(tau);
		}
		@Override public double logratio(Double Tnew, Double Told) {
			double result = 
					Tshape*log(Tnew/Told) - Tinvscale*(Tnew-Told)
					- laplace(alpha,sigma,Tnew,logU)
					+ laplace(alpha,sigma,Told,logU);
			for ( H cc : clusters ) {
				if (!cc.isEmpty())
					result += logGamma(alpha,sigma,Tnew,cc.number,logU)
							- logGamma(alpha,sigma,Told,cc.number,logU);
			}
			return result;
		}
	}
	TauSampler tausampler = new TauSampler();

	/* void sampleB() {
		int n = numData();
		int c = numClusters();
		double Z = tau * exp(.5*gen.nextGaussian());
		double Blogprob = (Tshape-1.0)*log(tau)-Tinvscale*tau;
		double Zlogprob = (Tshape-1.0)*log(   Z)-Tinvscale*   Z;
		if (sigma>1e-6) {
			Blogprob += -alpha/sigma*(pow(U/tau+1.0,sigma)-1.0) -sigma*c*log(tau)-(n-sigma*c)*log(tau+U);
			Zlogprob += -alpha/sigma*(pow(U/Z+1.0,sigma)-1.0) -sigma*c*log(Z)-(n-sigma*c)*log(Z+U);
		} else {
			Blogprob += (alpha-sigma*c)*log(tau)-(n-sigma*c)*log(tau+U);
			Zlogprob += (alpha-sigma*c)*log(Z)-(n-sigma*c)*log(Z+U);
		}
		if (gen.nextBoolean(exp(Zlogprob-Blogprob)))
			tau = Z;
	}*/

	double W(double s, double t) {
		return alpha/gamma(1-sigma)*pow(t,-1.0-sigma)*exp(-s*(exp(logU)+tau));
	}
	double logW(double s, double t) {
		return log(alpha) - SpecialFunctions.logGamma(1.0-sigma) - (1.0+sigma)*log(t) - s*(exp(logU)+tau);
	}
	double invWInt(double x, double t, double logu) {
		double a= log(x*(exp(logu)+tau));
		double b = logLevy(t,logu);
		if (a>=b) return Double.POSITIVE_INFINITY;
		if (b>a+33.0) return t+exp(a-b)/(exp(logu)+tau);
		else return t-log(1.0-exp(a-b))/(exp(logu)+tau);
	}
	@Override public double[] drawLogMasses(double slice) {
    //System.out.println("slice = "+minslice);
    //if (minslice<1e-8) {
    //  System.out.println("slice < 1e-8");
    //}
    slice = exp(slice);
    if (slice<minSlice) {
      slice = 1e-6;
      numBelowMinSlice += 1;
    }
		// sample empty clusters
		double s = slice;
		ArrayList<Double> masses = new ArrayList<Double>(100);
		while (true) {
			double e = generator.nextExponential(1.0);
			double news = invWInt(e,s,logU);
			if (news==Double.POSITIVE_INFINITY) {
        break;
      }
			if (generator.nextBoolean(exp(logLevy(news,logU)-logW(news,s)))) {
				masses.add(news);
			}
      if (masses.size()>=maxClusters) {
        //System.out.println("reduced. slice = "+minslice);
        numAboveMaxClusters += 1;
        masses.clear();
        slice *= 10.0;
        news = slice;
      }
      //System.out.println(news);
			s = news;
		}
		Collections.reverse(masses);
    //if (masses.size()>1000) {
    //  System.out.println(">1000 new clusters");
    //}
		double[] result = new double[masses.size()];
		for (int i=0; i<masses.size(); i++) {
      result[i] = log(masses.get(i));
    }
		return result;
	}

  public int getNumBelowMinSlice() {
    return numBelowMinSlice;
  }
  public int getNumAboveMaxClusters() {
    return numAboveMaxClusters;
  }
  public void resetCounts() {
    numAboveMaxClusters = 0;
    numBelowMinSlice = 0;
  }
	
	@Override public String toString() {
		return "NGGP(a="+alpha+",s="+sigma+",t="+tau+")";
	}

}
