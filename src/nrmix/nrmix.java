package nrmix;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import mcmc.collectors.Filer;
import mcmc.Sampler;
//import xfamily.NormalOld.*;
import java.io.*;
import java.util.ArrayList;
import static java.lang.Math.sqrt;
import nrmi.NGGP;
import utilities.DataIO;
import xfamily.*;

import mcmc.collectors.*;
import mixture.*;
import static utilities.DataIO.readdoubleMatrix;
import utilities.Factory;
import static utilities.Generator.generator;
import xfamily.MVNormal.*;
import static utilities.utilities.double2RealVector;
import xfamily.Normal.NormalConjugateFactoryMarginalized;
import xfamily.Normal.NormalConjugateFactorySampled;
import xfamily.Normal.NormalGamma;
import xfamily.Normal.NormalGammaIndependent;
import xfamily.Normal.NormalNonConjugateFactorySampled;

public class nrmix {
  static void usage() {
    System.out.println("usage: nrmix.nrmix neal8/reuse/slice conjugate? sampled? "
            + "          dataFile predFile useMeanVar? outputFilename "
            + "          numBurnin numSample numThinning numEmptyClusters "
            + "optional: alphaShape alphaInvScale sigmaAlpha sigmaBeta tauShape tauInvScale "
            + "          meanRelScale precisionDegFreedom invScaleDegFreedom precisionScale");
    System.out.println("Output files:");
    System.out.println("output_filename.parameters: numClusters alpha sigma tau numNewClusters");
    System.out.println("output_filename.invscale: precisionInvScale");
    System.out.println("output_filename.logpred: log predictive probabilities at data in pred_file");
    System.out.println("output_filename.assignments: assignment of data items to clusters");
    System.out.println("output_filename.clusters: clusters");
  }
  
  static boolean getBoolean(String s) {
    String l = s.toLowerCase();
    if (l.equals("true")||l.equals("t")||l.equals("1")) {
      return true;
    } else if (l.equals("false")||l.equals("f")||l.equals("0")) {
      return false;
    } else {
      throw new Error("Unknown boolean value "+s);
    }
  }
	public static void main(String[] args) throws IOException {
		long seed = 0;
		int numPrint = 10;

    if (seed!=0) generator.setSeed(seed);
		
		if (args.length != 21 && args.length != 11) {
      usage();
      throw new Error("Wrong number of arguments.");
		} else if (!args[0].equals("neal8")&&
               !args[0].equals("slice")&&
               !args[0].equals("reuse")
               ) {
      usage();
      throw new Error("Unknown algorithm "+args[0]+".");
    }
		String alg = args[0];
    boolean conjugate = getBoolean(args[1]);
    boolean sampled = getBoolean(args[2]);    
		String dataFile = args[3];
		String predFile = args[4];
    boolean useMeanVar = getBoolean(args[5]);
		String outputFilename = args[6];
		int numBurnin = Integer.parseInt(args[7]);
		int numSample = Integer.parseInt(args[8]);
		int numThinning = Integer.parseInt(args[9]);
		int numEmptyClusters = Integer.parseInt(args[10]);

		if (seed!=0) System.out.println("seed = "+seed);
		System.out.println("dataFile = "+dataFile);
		System.out.println("predFile = "+predFile);
		double[][] data = readdoubleMatrix(dataFile);
		double[][] pred = readdoubleMatrix(predFile);
    
    if (args.length==21) {
  	  double alphaShape = Double.parseDouble(args[11]);
  		double alphaInvScale = Double.parseDouble(args[12]);
  		double sigmaAlpha = Double.parseDouble(args[13]);
  		double sigmaBeta = Double.parseDouble(args[14]);
  		double tauShape = Double.parseDouble(args[15]);
  		double tauInvScale = Double.parseDouble(args[16]);
      
      double meanRelScale = Double.parseDouble(args[17]);
  		double precisionDegFreedom = Double.parseDouble(args[18]);
  		double invScaleDegFreedom = Double.parseDouble(args[19]);
  		double precisionScale = Double.parseDouble(args[20]);

  		run(alg,conjugate,sampled,data,pred,useMeanVar,outputFilename,
				numBurnin,numSample,numThinning,numEmptyClusters,numPrint,
				alphaShape,alphaInvScale,sigmaAlpha,sigmaBeta,tauShape,tauInvScale,
				meanRelScale,precisionDegFreedom,invScaleDegFreedom,precisionScale);
    } else {
      run(alg,conjugate,sampled,data,pred,useMeanVar,outputFilename,
				numBurnin,numSample,numThinning,numEmptyClusters);
    }
	}
	
	static public double[] run(
          String alg, boolean conjugate, boolean sampled, 
          Object data, Object pred, boolean useMeanVar,
          String outputFilename,
          int numBurnin,int numSample,int numThinning,int numEmptyClusters) throws IOException {
    System.out.println(data.getClass().getSimpleName());
    System.out.println(pred.getClass().getSimpleName());
    return null;
  }
	static public double[] run(
          String alg, boolean conjugate, boolean sampled, 
          double[][] data, double[][] pred, boolean useMeanVar,
          String outputFilename,
          int numBurnin,int numSample,int numThinning,int numEmptyClusters) throws IOException {
		
		int numPrint = 10;
		double alphaShape = 1;
		double alphaInvScale = 1;
		double sigmaAlpha = 1;
		double sigmaBeta = 2;
		double tauShape = 1e9;
		double tauInvScale = 1e9;

    int numdim = data[0].length;
    double meanRelScale = 1.0;
    double precisionDegFreedom = numdim+3.0;
    double invScaleDegFreedom = numdim-0.6;
    
    double precisionScale = 50.0;

    return run(alg, conjugate, sampled, data, pred, useMeanVar, outputFilename,
            numBurnin, numSample, numThinning, numEmptyClusters, numPrint,
            alphaShape, alphaInvScale, sigmaAlpha, sigmaBeta, tauShape, tauInvScale,
            meanRelScale, precisionDegFreedom, invScaleDegFreedom, precisionScale);

		
	}

  
	static public double[] run(String alg, boolean conjugate, boolean sampled,
      double[][] data, double[][] pred, boolean useMeanVar,
      String outputFilename,
			int numBurnin,int numSample,int numThinning,int numEmptyClusters,int numPrint,
			double alphaShape,double alphaInvScale,double sigmaAlpha,double sigmaBeta,double tauShape,double tauInvScale,
			double meanRelScale,
      double precisionDegFreedom, 
      double invScaleDegFreedom,
      double precisionScale) throws IOException {


    int numdata = data.length;
    int numdim = data[0].length;
    for (int i=1; i<numdata; i++) {
      if (data[i].length!=numdim) {
        throw new Error("Some data vectors have different dimensions.");
      }
    }
    if (pred==null) pred = new double[0][];
    for (int i=0; i<pred.length; i++) {
      if (pred[i].length!=numdim) {
        throw new Error("Some prediction vectors have different dimensions from those of data.");
      }
    }
    if (precisionDegFreedom <= numdim-1 || invScaleDegFreedom <= numdim-1) {
      throw new Error("Degree of freedom less than number of dimensions minus one.");
    }
    if (numdim>1 && (conjugate||!sampled)) {
      throw new Error("When number of dimensions is greater one, only non-conjugate model implemented");
    }
    if (!conjugate&&!sampled) {
      throw new Error("Cannot marginalize out cluster parameters for non-conjugate model");
    }

		NGGP nggp;
    XPrior prior;
    Factory factory;
    Mixture model;
    Object predvalues = null;
        
    double invScaleInvScale = 
        precisionScale*invScaleDegFreedom/(precisionDegFreedom-numdim-1.0);
    double[] dmin  = new double[numdim];
    double[] dmax  = new double[numdim];
    double[] dmean = new double[numdim];
    double[] dvar  = new double[numdim];
    for (int d=0; d<numdim; d++) {
      dmin[d] = Double.POSITIVE_INFINITY;
      dmax[d] = Double.NEGATIVE_INFINITY;
    	for (int i=0; i<data.length; i++) {
        double x = data[i][d];
        dmean[d] += x;
        dvar[d] += x*x;
  			if (x<dmin[d]) { dmin[d] = x; }
  			if (x>dmax[d]) { dmax[d] = x; }
      }
      dmean[d] /= (double)data.length;
      dvar[d] = dvar[d]/(double)(data.length-1) - dmean[d]*dmean[d];
		}
    RealVector meanMean = new ArrayRealVector(numdim);
    RealMatrix meanPrecision = new Array2DRowRealMatrix(numdim, numdim);
    RealMatrix precisionInvScaleInvScale = new Array2DRowRealMatrix(numdim, numdim);
    for (int d = 0; d < numdim; d++) {
      if (useMeanVar) {
        meanMean.setEntry(d, dmean[d]);
      } else {
        meanMean.setEntry(d, .5 * (dmin[d] + dmax[d]));
      }
      for (int e = 0; e < numdim; e++) {
        if (e == d) {
          double range;
          if (useMeanVar) {
            range = sqrt(dvar[d]);
          } else {
            range = .5 * (dmax[d] - dmin[d]);
          }
          meanPrecision.setEntry(d, d, 1.0 / range / range / meanRelScale);
          precisionInvScaleInvScale.setEntry(d, d, invScaleInvScale / range / range); 
        } else {
          meanPrecision.setEntry(d, e, 0.0);
          precisionInvScaleInvScale.setEntry(d, e, 0.0);
        }
      }
    }

    if (numdim>1) {
      prior = new MVNormalWishartIndependent(
              meanMean, meanPrecision,
              precisionDegFreedom,
              invScaleDegFreedom, precisionInvScaleInvScale);
      factory = new MVNormalNonConjugateFactorySampled();
      if (pred.length>0) {
        predvalues = double2RealVector(pred);       
      }
    } else { // numdim == 1
      double meanmean = meanMean.getEntry(0);
      double meanprecision = meanPrecision.getEntry(0, 0);
      double precisioninvscaleinvscale = precisionInvScaleInvScale.getEntry(0, 0);
      if (conjugate) {
        prior = new NormalGamma(
                meanmean,
                meanprecision,
                precisionDegFreedom,
                .5*invScaleDegFreedom,
                .5*precisioninvscaleinvscale);
        if (sampled) {
          factory = new NormalConjugateFactorySampled();
        } else {
          factory = new NormalConjugateFactoryMarginalized();
        }
      } else { // not conjugate
        prior = new NormalGammaIndependent(
                meanmean,
                meanprecision,
                .5*precisionDegFreedom,
                .5*invScaleDegFreedom,
                precisioninvscaleinvscale);
        factory = new NormalNonConjugateFactorySampled();
      }
      Double[] v = new Double[pred.length];
      for (int i=0; i<pred.length; i++) {
        v[i] = pred[i][0];
      }
      predvalues = v;
    }
 
    if (alg.equals("neal8")) {
      nggp = new NGGP(alphaShape,alphaInvScale,sigmaAlpha,sigmaBeta,tauShape,tauInvScale);
      model = new MixtureNeal8(nggp, prior, factory, numEmptyClusters);
    } else if (alg.equals("reuse")) {
      nggp = new NGGP(alphaShape,alphaInvScale,sigmaAlpha,sigmaBeta,tauShape,tauInvScale);
      model = new MixtureReuse(nggp, prior, factory, numEmptyClusters);
    } else if (alg.equals("slice")) {
      nggp = new NGGP(alphaShape,alphaInvScale,sigmaAlpha,sigmaBeta,tauShape,tauInvScale,
              1.0e-8,numEmptyClusters);
      model = new MixtureSlice(nggp, prior, factory);
    } else {
      throw new Error("Unknown algorithm.");
    }
    
		System.out.println("NRMIX "+alg);
    System.out.println("  components: "+
            (conjugate?("conjugate, "+
            (sampled?"sampled":"marginalized")):"non-conjugate sampled"));
		System.out.println("  outputFilename = "+outputFilename);
    System.out.println("  numDimension = "+numdim);
    System.out.println("  numData = "+data.length);
		System.out.println("  numBurnin = "+numBurnin);
		System.out.println("  numSample = "+numSample);
		System.out.println("  numThinning = "+numThinning);
		System.out.println("  numNewClusters = "+numEmptyClusters);
		System.out.println("  alphaShape = "+alphaShape);
		System.out.println("  alphaInvScale = "+alphaInvScale);
		System.out.println("  sigmaAlpha = "+sigmaAlpha);
		System.out.println("  sigmaBeta = "+sigmaBeta);
		System.out.println("  tauShape = "+tauShape);
		System.out.println("  tauInvScale = "+tauInvScale);
		System.out.println("  meanRelativeScale = "+meanRelScale);
		System.out.println("  precisionDegFreedom = "+precisionDegFreedom);
		System.out.println("  invScaleDegFreedom = "+invScaleDegFreedom);
		System.out.println("  precisionScale = "+precisionScale);
    System.out.println("  useMeanVar = "+useMeanVar);


		ArrayList<Collector> collectors = new ArrayList<Collector>();
    String[] parameters = {"numClusters","nggp_alpha","nggp_sigma","nggp_tau","numEmptyClusters"};
    collectors.add(new Filer(model,outputFilename+".parameters",parameters,null));
    String[] precisioninvscale = {"precisionInvScale"};
    collectors.add(new Filer(model,outputFilename+".invscale",precisioninvscale,null));
 	
    if (pred.length>0) {
      DataIO.writeDoubleMatrix(outputFilename+".predval", pred);
  		String[] logpred = {"logPredictives"};
  		Object[] predarg = {predvalues};
  		collectors.add(new Filer(model,outputFilename+".logpred",logpred,predarg));
    }

		collectors.add(new MixtureClusterFiler(model,outputFilename+".clusters",outputFilename+".assignments"));
		
    if (numdim>1) {
      RealVector[] datavec = double2RealVector(data);
      model.addData(datavec);
    } else {
      Double[] d = new Double[numdata];
      for (int i=0; i<numdata; i++) {
        d[i] = data[i][0];
      }
      model.addData(d);
    }
		Sampler sampler = new Sampler(model,collectors,System.out,
				numBurnin,numSample,numThinning,numPrint);
		double[] times =  sampler.run();

		PrintStream log = null;
		try {
			log = new PrintStream(outputFilename+".log");
      log.println("Run time = "+times[0]);
      log.println("Total time = "+times[1]);
      log.println("Num below minSlice ="+nggp.getNumBelowMinSlice());
      log.println("Num above maxClusters ="+nggp.getNumAboveMaxClusters());
	  	log.close();
		} catch(Error ee) {
			System.out.println("Unable to open "+outputFilename+".log: "+ee.getMessage());
			if (log!=null) {
        log.close();
      }
			throw ee;
		}
    double[] output = new double[4];
    output[0] = times[0];
    output[1] = times[1];
    output[2] = nggp.getNumBelowMinSlice();
    output[3] = nggp.getNumAboveMaxClusters();
    return output;    
	}
}
