package nrmix;

import mcmc.collectors.Filer;
import mcmc.Sampler;
import xfamily.Normal.NormalGamma;
import xfamily.Normal.NormalConjugateFactorySampled;
import xfamily.Normal.NormalConjugateFactoryMarginalized;
import xfamily.Normal.NormalConjugateHierarchy;
import xfamily.Normal.Normal;
import xfamily.Normal.NormalGammaIndependent;
import xfamily.Normal.NormalNonConjugateFactorySampled;
import xfamily.Normal.NormalNonConjugateHierarchy;
//import xfamily.NormalOld.*;
import java.io.*;
import java.util.ArrayList;
import static java.lang.Math.sqrt;
import nrmi.NGGP;
import utilities.DataIO;
import xfamily.*;
import static utilities.utilities.double2Double;

import mcmc.collectors.*;
import mixture.*;
import static java.lang.Math.max;
import static utilities.DataIO.readDoubleArray;
import static utilities.Generator.generator;

public class nrmix1d {
  static void usage() {
			System.out.println("usage: nrmix.nrmix neal8ns/slicens dataFile predFile outputFilename "+
						"numBurnin numSample numThinning numNewClusters "+
						"alphaShape alphaInvScale sigmaAlpha sigmaBeta tauShape tauInvScale "+
						"meanMean meanRelPrecision precisionDegFreedom precisionInvScaleAlpha precisionInvScaleBeta");
			System.out.println("    or nrmix.nrmix neal8cs/neal8cm/slicecs/slicecm dataFile predFile outputFilename "+
						"numBurnin numSample numThinning numNewClusters "+
						"alphaShape alphaInvScale sigmaAlpha sigmaBeta tauShape tauInvScale "+
						"meanMean meanPrecision precisionShape precisionInvScaleAlpha precisionInvScaleBeta");
      System.out.println("where algns = algorithm with non-conjugate independent prior");
      System.out.println("      algcs = algorithm with conjugate prior, sampled parameters");
      System.out.println("      algcm = algorithm with conjugate prior, marginalized parameters");
			System.out.println("Output files:");
			System.out.println("output_filename.parameters: numClusters alpha sigma tau numNewClusters mean relPrecision degFreedom invScale, or");
			System.out.println("output_filename.parameters: numClusters alpha sigma tau numNewClusters meanMean meanPrecision precisionShape precisoinInvScale");
			System.out.println("output_filename.logpred: log predictive probabilities at data in pred_file");
			System.out.println("output_filename.assignments: assignment of data items to clusters");
			System.out.println("output_filename.clusters: clusters");
  }
	public static void main(String[] args) throws IOException {
		long seed = 0;

		if (seed!=0) generator.setSeed(seed);
		
		if (args.length != 19) {
			System.out.println("ERROR: too few arguments.");
      usage();
      throw new Error("too few arguments.");
		} else if (!args[0].equals("neal8ns")&&
               !args[0].equals("neal8cs")&&
               !args[0].equals("neal8cm")&&
               !args[0].equals("slicens")&&
               !args[0].equals("slicecs")&&
               !args[0].equals("slicecm")
               ) {
			System.out.println("ERROR: unknown algorithm.");
      usage();
      throw new Error("Unknown algorithm.");
    }
		String alg = args[0];
    boolean conjugate = alg.charAt(5)=='c';
    boolean marginalized = conjugate && (alg.charAt(6)=='m');
    alg = alg.substring(0, 5);
		String dataFile = args[1];
		String predFile = args[2];
		String outputFilename = args[3];
		int numPrint = 10;
		int numBurnin = Integer.parseInt(args[4]);
		int numSample = Integer.parseInt(args[5]);
		int numThinning = Integer.parseInt(args[6]);
		int numNewClusters = Integer.parseInt(args[7]);
		double alphaShape = Double.parseDouble(args[8]);
		double alphaInvScale = Double.parseDouble(args[9]);
		double sigmaAlpha = Double.parseDouble(args[10]);
		double sigmaBeta = Double.parseDouble(args[11]);
		double tauShape = Double.parseDouble(args[12]);
		double tauInvScale = Double.parseDouble(args[13]);
		double param1 = Double.parseDouble(args[14]);
		double param2 = Double.parseDouble(args[15]);
		double param3 = Double.parseDouble(args[16]);
		double param4 = Double.parseDouble(args[17]);
    double param5 = Double.parseDouble(args[18]);
		
		if (seed!=0) System.out.println("seed = "+seed);
		System.out.println("dataFile = "+dataFile);
		System.out.println("predFile = "+predFile);

		Double[] Data = readDoubleArray(dataFile);
		Double[] Pred = readDoubleArray(predFile);

		run(conjugate,marginalized,alg,Data,Pred,outputFilename,
				numBurnin,numSample,numThinning,numNewClusters,numPrint,
				alphaShape,alphaInvScale,sigmaAlpha,sigmaBeta,tauShape,tauInvScale,
				param1,param2,param3,param4,param5);
	}
	
	static public double[] run(boolean conjugate, boolean marginalized, String alg,
      double[] data,String outputFilename) throws IOException {
    return run(conjugate,marginalized,alg,double2Double(data),outputFilename);
  }

  static public double[] run(boolean conjugate, boolean marginalized,
          String alg, Double[] data, String outputFile) throws IOException {
		// calculate mean and standard deviation
		double dmin = Double.POSITIVE_INFINITY;
		double dmax = Double.NEGATIVE_INFINITY;
		for (int i=0; i<data.length; i++) {
			if (data[i]<dmin) dmin = data[i];
			if (data[i]>dmax) dmax = data[i];
		}
    double midpoint = .5*(dmax+dmin);
		double range = dmax-midpoint;
		
		int numpred = 100;
		double rangepred = ((double)(numpred-1))/2.0;
		Double[] pred = new Double[numpred];
		for ( int i=0; i<numpred; i++) 
			pred[i] = midpoint + range*1.25*(((double)i)-rangepred)/rangepred;
		
		int numBurnin = 1000;
		int numSample = 1000;
		int numThinning = 10;
		int numNewClusters = 5;
		int numPrint = 10;
		double alphaShape = 2;
		double alphaInvScale = 2;
		double sigmaAlpha = 1;
		double sigmaBeta = 2;
		double tauShape = 2;
		double tauInvScale = 2;

    if (conjugate) {
      double meanMean = midpoint;
      double meanRelPrecision = 1.0/range/range;
      double precisionDegFreedom = 4.0;
      double precisionInvScaleAlpha = .2;
      double precisionInvScaleBeta = 5.0/range/range;

      return run(true,marginalized,alg,data,pred,outputFile,
        numBurnin, numSample, numThinning, numNewClusters, numPrint,
        alphaShape, alphaInvScale, sigmaAlpha, sigmaBeta, tauShape, tauInvScale,
        meanMean, meanRelPrecision,
        precisionDegFreedom, precisionInvScaleAlpha, precisionInvScaleBeta);
    } else {
      double meanMean = midpoint;
      double meanPrecision = 1.0/range/range;
      double precisionShape = 2.0;
      double precisionInvScaleAlpha = 0.2;
      double precisionInvScaleBeta  = 10.0/range/range;

      return run(false,false,alg,data,pred,outputFile,
        numBurnin, numSample, numThinning, numNewClusters, numPrint,
        alphaShape, alphaInvScale, sigmaAlpha, sigmaBeta, tauShape, tauInvScale,
        meanMean, meanPrecision, 
        precisionShape, precisionInvScaleAlpha, precisionInvScaleBeta);
    }
		
	}

	static public double[] run(boolean conjugate, boolean marginalized, String alg,
      double[] data, double[] pred,
      String outputFilename,
			int numBurnin,int numSample,int numThinning,int numNewClusters,int numPrint,
			double alphaShape,double alphaInvScale,double sigmaAlpha,double sigmaBeta,double tauShape,double tauInvScale,
			double param1,double param2,double param3,double param4,double param5) throws IOException {
    return run(conjugate,marginalized,alg,double2Double(data),double2Double(pred),outputFilename,
            numBurnin,numSample,numThinning,numNewClusters,numPrint,
            alphaShape,alphaInvScale, sigmaAlpha, sigmaBeta, tauShape, tauInvScale,
            param1,param2,param3,param4,param5);
  }
  
	static public double[] run(boolean conjugate, boolean marginalized, String alg,
      Double[] data, Double[] pred,
      String outputFilename,
			int numBurnin,int numSample,int numThinning,int numNewClusters,int numPrint,
			double alphaShape,double alphaInvScale,double sigmaAlpha,double sigmaBeta,double tauShape,double tauInvScale,
			double param1,double param2,double param3,double param4,double param5) throws IOException {

		System.out.println("NRMIX "+alg+","+(conjugate?"conjugate":"non-conjugate")+":");
		System.out.println("  outputFilename = "+outputFilename);
    System.out.println("  numData = "+data.length);
		System.out.println("  numBurnin = "+numBurnin);
		System.out.println("  numSample = "+numSample);
		System.out.println("  numThinning = "+numThinning);
		System.out.println("  numNewClusters = "+numNewClusters);
		System.out.println("  alphaShape = "+alphaShape);
		System.out.println("  alphaInvScale = "+alphaInvScale);
		System.out.println("  sigmaAlpha = "+sigmaAlpha);
		System.out.println("  sigmaBeta = "+sigmaBeta);
		System.out.println("  tauShape = "+tauShape);
		System.out.println("  tauInvScale = "+tauInvScale);
		System.out.println((conjugate?"  mean = ":"  meanMean = ")+param1);
		System.out.println((conjugate?"  relPrecision = ":"  meanPrecision = ")+param2);
		System.out.println((conjugate?"  degFreedom = ":"  precisionShape = ")+param3);
		System.out.println((conjugate?"  invScaleAlpha = ":"  precisionInvScaleAlpha = ")+param4);
		System.out.println((conjugate?"  invScaleBeta = ":"  precisionInvScaleBeta = ")+param5);

		NGGP nggp = new NGGP(alphaShape,alphaInvScale,sigmaAlpha,sigmaBeta,tauShape,tauInvScale);
    Mixture model;
    if (conjugate && marginalized) {
      NormalGamma prior = new NormalGamma(param1,param2,param3,param4,param5);
      //NormalGamma prior = new NormalGamma(param1,param3,param4/param5,param2);
      NormalConjugateFactoryMarginalized factory = new NormalConjugateFactoryMarginalized();
      if (alg.equals("neal8"))
        model = new MixtureNeal8<Double,Normal,NormalGamma,NormalConjugateHierarchy>(
					nggp,prior,factory,numNewClusters);
      else
        model = new MixtureSlice<Double,Normal,NormalGamma,NormalConjugateHierarchy>(
					nggp,prior,factory);
    } else if (conjugate && !marginalized) {
      NormalGamma prior = new NormalGamma(param1,param2,param3,param4,param5);
      //NormalGamma prior = new NormalGamma(param1,param3,param4/param5,param2);
      NormalConjugateFactorySampled factory = new NormalConjugateFactorySampled();
      if (alg.equals("neal8"))
        model = new MixtureNeal8<Double,Normal,NormalGamma,NormalConjugateHierarchy>(
					nggp,prior,factory,numNewClusters);
      else
        model = new MixtureSlice<Double,Normal,NormalGamma,NormalConjugateHierarchy>(
					nggp,prior,factory);
    } else if (!conjugate && !marginalized) {
      NormalGammaIndependent prior = new NormalGammaIndependent(param1,param2,param3,param4,param5);
      NormalNonConjugateFactorySampled factory = new NormalNonConjugateFactorySampled();
      if (alg.equals("neal8"))
        model = new MixtureNeal8<Double,Normal,NormalGammaIndependent,NormalNonConjugateHierarchy>(
					nggp,prior,factory,numNewClusters);
      else
        model = new MixtureSlice<Double,Normal,NormalGammaIndependent,NormalNonConjugateHierarchy>(
					nggp,prior,factory);
    } else {
      throw new Error("Parameters cannot be marginalized under non-conjugate prior.");
    }
    
		model.addData(data);


		ArrayList<Collector> collectors = new ArrayList<Collector>();
    if (conjugate) {
  		String[] parameters = {"numClusters","alpha","sigma","tau","numEmptyClusters",
                             "precisionInvScale"};
    	collectors.add(new Filer(model,outputFilename+".parameters",parameters,null));
    } else {
  		String[] parameters = {"numClusters","alpha","sigma","tau","numEmptyClusters",
                             "precisionInvScale"};
    	collectors.add(new Filer(model,outputFilename+".parameters",parameters,null));
    }
	
		DataIO.writeDoubleArray(outputFilename+".predval", pred);
		String[] logpred = {"logPredictives"};
		Object[] predarray = {pred};
		collectors.add(new Filer(model,outputFilename+".logpred",logpred,predarray));

		collectors.add(new MixtureClusterFiler(model,outputFilename+".clusters",outputFilename+".assignments"));
		
		Sampler sampler = new Sampler(model,collectors,System.out,
				numBurnin,numSample,numThinning,numPrint);
		double[] result =  sampler.run();
    System.out.println("Number of times number of masses have to be reduced = "+nggp.getNumAboveMaxClusters());
    System.out.println("Number of times minslice < 1e-7 = "+nggp.getNumBelowMinSlice());
    return result;
	}
}
