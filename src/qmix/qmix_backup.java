package qmix;

import java.io.*;
import static java.lang.Math.sqrt;
import java.util.ArrayList;
import mcmc.Sampler;
import mcmc.collectors.*;
import mixture.*;
import nrmi.QGGP;
import nrmi.QGGPConstant;
import nrmi.QPYP_TU;
import nrmi.QPYP_VZ;
import nrmi.QPYP_marg;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import utilities.DataIO;
import static utilities.DataIO.getBoolean;
import static utilities.DataIO.readdoubleMatrix;
import utilities.Factory;
import static utilities.Generator.generator;
import static utilities.utilities.double2RealVector;
import xfamily.*;
import xfamily.MVNormal.*;
import xfamily.Normal.NormalConjugateFactoryMarginalized;
import xfamily.Normal.NormalConjugateFactorySampled;
import xfamily.Normal.NormalGamma;
import xfamily.Normal.NormalGammaIndependent;
import xfamily.Normal.NormalNonConjugateFactorySampled;

public class qmix_backup {
  
  static void usage() {
    System.out.println("usage: qmix.qmix neal8/reuse/slice conjugate? sampled? "
            + "          dataFile predFile useMeanVar? outputFilename "
            + "          numBurnin numSample numThinning numEmptyClusters "
            + "optional: sigmaAlpha sigmaBeta thetaShape thetaInvScale "
            + "          meanRelScale precisionDegFreedom invScaleDegFreedom precisionScale");
    System.out.println("Output files:");
    System.out.println("output_filename.parameters: numClusters sigma theta tau U numNewClusters");
    System.out.println("output_filename.invscale: precisionInvScale");
    System.out.println("output_filename.logpred: log predictive probabilities at data in pred_file");
    System.out.println("output_filename.assignments: assignment of data items to clusters");
    System.out.println("output_filename.clusters: clusters");
  }
  
	public static void main(String[] args) throws IOException, Error, Exception {
		long seed = 0;

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
    
		int numPrint = 10;

    if (args.length==19) {
  		double sigmaAlpha = Double.parseDouble(args[11]);
  		double sigmaBeta = Double.parseDouble(args[12]);
  		double thetaShape = Double.parseDouble(args[13]);
  		double thetaInvScale = Double.parseDouble(args[14]);
      
      double meanRelScale = Double.parseDouble(args[15]);
  		double precisionDegFreedom = Double.parseDouble(args[16]);
  		double invScaleDegFreedom = Double.parseDouble(args[17]);
  		double precisionScale = Double.parseDouble(args[18]);

  		run(alg,conjugate,sampled,data,pred,outputFilename,
				numBurnin,numSample,numThinning,numEmptyClusters,numPrint,
				sigmaAlpha,sigmaBeta,thetaShape,thetaInvScale,
				useMeanVar,meanRelScale,precisionDegFreedom,invScaleDegFreedom,precisionScale);
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
          double[][] data, double[][] pred,
          String outputFilename,
          int numBurnin,int numSample,int numThinning) throws IOException, Error, Exception {
    String alg = "reuse";
    boolean conjugate = false;
    boolean sampled = true;
    int numEmptyClusters = 10;
    return run(alg,conjugate,sampled,
            data,pred,outputFilename,
            numBurnin,numSample,numThinning,numEmptyClusters);
  }
	static public double[] run(
          String alg, boolean conjugate, boolean sampled, 
          double[][] data, double[][] pred,
          String outputFilename,
          int numBurnin,int numSample,int numThinning,int numEmptyClusters) throws IOException, Error, Exception {
		
		int numPrint = 10;
		double sigmaAlpha = 1;
		double sigmaBeta = 2;
		double thetaShape = 1e9;
		double thetaInvScale = 1e9;

    boolean useMeanVar = true;
    int numdim = data[0].length;
    double meanRelScale = 1.0;
    double precisionDegFreedom = numdim+3.0;
    double invScaleDegFreedom = numdim-0.6;
    
    double precisionScale = 50.0;

    return run(alg, conjugate, sampled, data, pred, outputFilename,
            numBurnin, numSample, numThinning, numEmptyClusters, numPrint,
            sigmaAlpha, sigmaBeta, thetaShape, thetaInvScale,
            useMeanVar, meanRelScale, precisionDegFreedom, invScaleDegFreedom, precisionScale);
		
	}

  
	static public double[] run(String alg, boolean conjugate, boolean sampled,
      double[][] data, double[][] pred, 
      String outputFilename,
			int numBurnin,int numSample,int numThinning,int numEmptyClusters,int numPrint,
			double sigmaAlpha,double sigmaBeta,double thetaShape,double thetaInvScale,
			boolean useMeanVar,
      double meanRelScale,
      double precisionDegFreedom, 
      double invScaleDegFreedom,
      double precisionScale) throws IOException, Error, Exception {


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

    XPrior prior;
    Factory factory;
    Mixture model;
    Object predvalues = null;
        
    if (numdim>1) {
      prior = MVNormalWishartIndependent.constructPrior(data, 
              useMeanVar, 
              meanRelScale, 
              precisionDegFreedom, 
              invScaleDegFreedom, 
              precisionScale);
      factory = new MVNormalNonConjugateFactorySampled();
      if (pred.length>0) {
        predvalues = double2RealVector(pred);       
      }
    } else { // numdim == 1
      if (conjugate) {
        prior = NormalGamma.constructPrior(data, 
                useMeanVar, 
                meanRelScale, 
                precisionDegFreedom, 
                invScaleDegFreedom, 
                precisionScale);
        if (sampled) {
          factory = new NormalConjugateFactorySampled();
        } else {
          factory = new NormalConjugateFactoryMarginalized();
        }
      } else { // not conjugate
        prior = NormalGammaIndependent.constructPrior(data, 
                useMeanVar, 
                meanRelScale, 
                precisionDegFreedom, 
                invScaleDegFreedom, 
                precisionScale);
        factory = new NormalNonConjugateFactorySampled();
      }
      Double[] v = new Double[pred.length];
      for (int i=0; i<pred.length; i++) {
        v[i] = pred[i][0];
      }
      predvalues = v;
    }
 
    QGGP qggp = new QGGPConstant(sigmaAlpha,sigmaBeta,0.0);
    
    String outputFilename1 = outputFilename+"-1";
    if (alg.equals("neal8")) {
      model = new MixtureNeal8(qggp, prior, factory, numEmptyClusters);
    } else if (alg.equals("reuse")) {
      model = new MixtureReuse(qggp, prior, factory, numEmptyClusters);
    } else if (alg.equals("slice")) {
      model = new MixtureSlice(qggp, prior, factory);
    } else {
      throw new Error("Unknown algorithm.");
    }
    
		System.out.println("PYMIX "+alg);
    System.out.println("  components: "+
            (conjugate?("conjugate, "+
            (sampled?"sampled":"marginalized")):"non-conjugate sampled"));
		System.out.println("  outputFilename = "+outputFilename1);
    System.out.println("  numDimension = "+numdim);
    System.out.println("  numData = "+data.length);
		System.out.println("  numBurnin = "+numBurnin);
		System.out.println("  numSample = "+numSample);
		System.out.println("  numThinning = "+numThinning);
		System.out.println("  numNewClusters = "+numEmptyClusters);
		System.out.println("  sigmaAlpha = "+sigmaAlpha);
		System.out.println("  sigmaBeta = "+sigmaBeta);
		System.out.println("  thetaShape = "+thetaShape);
		System.out.println("  thetaInvScale = "+thetaInvScale);
		System.out.println("  meanRelativeScale = "+meanRelScale);
		System.out.println("  precisionDegFreedom = "+precisionDegFreedom);
		System.out.println("  invScaleDegFreedom = "+invScaleDegFreedom);
		System.out.println("  precisionScale = "+precisionScale);


		ArrayList<Collector> collectors = new ArrayList<Collector>();
    String[] parameters = {"numClusters","qggp_sigma","qpyp_theta","qggp_logtau","logU","numEmptyClusters"};
    collectors.add(new Filer(model,outputFilename1+".parameters",parameters,null));
    String[] precisioninvscale = {"precisionInvScale"};
    collectors.add(new Filer(model,outputFilename1+".invscale",precisioninvscale,null));
 	
    if (pred.length>0) {
      DataIO.writeDoubleMatrix(outputFilename1+".predval", pred);
  		String[] logpred = {"logPredictives"};
  		Object[] predarg = {predvalues};
  		collectors.add(new Filer(model,outputFilename1+".logpred",logpred,predarg));
    }

		collectors.add(new MixtureClusterFiler(model,outputFilename1+".clusters",outputFilename1+".assignments"));
		
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
			log = new PrintStream(outputFilename1+".log");
      log.println("Run time = "+times[0]);
      log.println("Total time = "+times[1]);
      log.println("Num below minSlice ="+qggp.getNumBelowMinSlice());
      log.println("Num above maxClusters ="+qggp.getNumAboveMaxClusters());
	  	log.close();
		} catch(Error ee) {
			System.out.println("Unable to open "+outputFilename1+".log: "+ee.getMessage());
			if (log!=null) {
        log.close();
      }
			throw ee;
		}

    return null;
  }


}