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
import static java.lang.Math.expm1;
import mcmc.kernel.SliceStepOut;
import static utilities.Generator.generator;
import static utilities.SpecialFunctions.gamma;
import static utilities.SpecialFunctions.logsumexp;

public abstract class QGGP extends NRMI {
	
	double sigma, logtau;
	double Salpha, Sbeta;
  double minSlice, maxClusters;
  static double default_minslice = 1.0e-7;
  static int default_maxclusters = 1000000;
  int numBelowMinSlice = 0;
  int numAboveMaxClusters = 0;
	
  abstract double F(double sigma, double logtau);
  abstract double logF(double sigma, double logtau);
	
	public QGGP(double Salpha, double Sbeta) {
    this(Salpha,Sbeta,
            default_minslice,default_maxclusters);
  }
  
	public QGGP(double Salpha, double Sbeta, 
      double minslice, double maxclusters) {
		this.Salpha = Salpha;
		this.Sbeta = Sbeta;
		sigma = 0.3;
		logtau = 0.0;
    this.minSlice = minslice;
    this.maxClusters = maxclusters;
	}
  public QGGP(double sigma) {
    this(sigma,
            default_minslice,default_maxclusters);
  }
  public QGGP(double sigma,
          double minslice, double maxclusters) {
    this.sigma = sigma;
    this.logtau   = 0.0;
    this.Salpha    = sigma*1e6;
    this.Sbeta     = (1-sigma)*1e6;
    this.minSlice = minslice;
    this.maxClusters = maxclusters;
  }
  
	public double getSigma() { return sigma; }
	public double getTau() { return exp(logtau); }
  public double getLogTau() { return logtau; }
	@Override public double logLevy(double mass) {
		return logLevy(mass,sigma,logtau,Double.NEGATIVE_INFINITY);
	}
	@Override double logLevy(double mass, double logu) {
		return logLevy(mass,sigma,logtau,logu);
	}
	double logLevy(double mass, double sigma, double logtau, double logu) {
		return log(sigma)
				- SpecialFunctions.logGamma(1.0-sigma)
				- (1.0+sigma)*log(mass)
				- (exp(logu)+exp(logtau))*mass;
	}
	@Override public double laplace(double logu) {
		return laplace(sigma,logtau,logu);
	}
	double laplace(double sigma, double logtau, double logu) {
		//return exp(sigma*(logsumexp(0.0,logu-logtau)));
		if (sigma<1e-16) {
      return sigma*logsumexp(0.0,logu-logtau);
    } else {
      double S = sigma*logsumexp(0.0,logu-logtau);
  		return exp(sigma*logtau + S + log1p(-exp(-S)));
    }
//              (Math.pow(exp(logtau)+exp(logu),sigma)-Math.pow(exp(logtau),sigma));
	}
	@Override double logGamma(int num, double logu) {
		return logGamma(sigma,logtau,num,logu);
	}
	double logGamma(double sigma, double logtau, int num, double logu) {
		return Math.log(sigma)
				+ SpecialFunctions.logGamma(num-sigma)
				- SpecialFunctions.logGamma(1.0-sigma)
				- (num-sigma)*logsumexp(logtau,logu);
	}
	@Override double logMeanMass(int num, double logu) {
		return log((double)num-sigma) - logsumexp(logtau,logu);
	}
	@Override double logMeanTotalMass(int numclusters, double logu) {
		return log(sigma) + (sigma-1.0)*logsumexp(logtau,logu);
	}
	@Override double drawLogMass(int num, double logu) {
		return log(generator.nextGamma(num-sigma)) - logsumexp(logtau,logu);
	}
	
	@Override double meanNumClusters(int num, double logu) {
		System.out.println("NGGP.meanNumClusters: dont know answer yet");
		return sigma*log(1+num/sigma);
	}
	

	@Override public <H extends Cluster> void sample(int numData, Collection<H> clusters) {
    super.sample(numData, clusters);
		sigmasampler.sample(clusters);
    tauslicer.sample(clusters);
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
          logF(x,logtau)
					+ (Salpha-1.0)*log(max(1e-16,x))
					+ (Sbeta-1.0)*log(max(1e-16,1.0-x))
					- laplace(x,logtau,logU);
			for ( C cc : clusters ) {
				if (!cc.isEmpty())
					result += logGamma(x,logtau,cc.number,logU);
			}
			return result;
		}		
	}
	SigmaSampler sigmasampler = new SigmaSampler();
	
	class TauSlicer extends SliceStepOut {
		Collection<? extends Cluster> clusters;
    public TauSlicer() {
      super(2.0,20,generator);
    }
		<H extends Cluster> void sample(Collection<H> clusters) {
			this.clusters = clusters;
			logtau = sample(logtau);
		}
		@Override public double logDensity(double x) {
			double result = 
            logF(sigma,x) 
					- laplace(sigma,x,logU);
			for ( Cluster cc : clusters ) {
				if (!cc.isEmpty())
					result += logGamma(sigma,x,cc.number,logU);
			}
			return result;
		}
	}
	TauSlicer tauslicer = new TauSlicer();

	double W(double s, double t) {
		return sigma/gamma(1-sigma)*pow(t,-1.0-sigma)*exp(-s*(exp(logU)+exp(logtau)));
	}
	double logW(double s, double t) {
		return log(sigma) - SpecialFunctions.logGamma(1.0-sigma) 
            - (1.0+sigma)*log(t) - s*(exp(logU)+exp(logtau));
	}
	double invWInt(double x, double t) {
		double a= log(x*(exp(logU)+exp(logtau)));
		double b = logLevy(t,logU);
		if (a>=b) return Double.POSITIVE_INFINITY;
		if (b>a+33.0) return t+exp(a-b)/(exp(logU)+exp(logtau));
		else return t-log(1.0-exp(a-b))/(exp(logU)+exp(logtau));
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
			double news = invWInt(e,s);
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
		return this.getClass().getSimpleName()+"(s="+sigma+",lt="+logtau+")";
	}

}
