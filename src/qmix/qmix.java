package qmix;

import java.io.*;
import java.util.ArrayList;
import mcmc.Sampler;
import mcmc.SamplerOptionPack;
import mcmc.collectors.*;
import mixture.*;
import nrmi.QGGP;
import nrmi.QGGPConstantOptionPack;
import nrmi.QGGPOptionPack;
import nrmi.QPYP_TU;
import org.apache.commons.cli.*;
import org.apache.commons.math3.linear.RealVector;
import utilities.DataIO;
import static utilities.DataIO.readdoubleMatrix;
import utilities.Factory;
import static utilities.Generator.generator;
import utilities.OptionPacks;
import static utilities.utilities.double2RealVector;
import xfamily.MVNormal.*;

/**
 * A Q class mixture model script consists of specifications for:
 * - a Q class nonparametric prior.
 * - a hierarchical model for cluster parameters and prior.
 * - a MCMC algorithm description.
 * - data file name and output file name.
 * 
 * @author ywteh
 */
public class qmix {

  OptionPacks packs;
  QGGPOptionPack qrmpack;
  MVNormalWishartIndependentOptionPack priorpack;
  MixtureOptionPack mixturepack;
  SamplerOptionPack samplerpack;
  IOOptionPack iopack;

  public qmix() {
    packs = new OptionPacks();
    qrmpack = new QGGPConstantOptionPack();
    priorpack = new MVNormalWishartIndependentOptionPack();
    mixturepack = new MixtureOptionPack();
    samplerpack = new SamplerOptionPack();
    iopack = new IOOptionPack();
    packs.add(qrmpack);
    packs.add(priorpack);
    packs.add(mixturepack);
    packs.add(samplerpack);
    packs.add(iopack);

  
  }
  
  public void printHelp() {
    packs.printHelp("qmix.qmix dataFile outputDirectory [options]");
  }
  
  public double[] run(String[] args) throws ParseException, IOException, Error, Exception {
    CommandLine cmdline = packs.parse(args);
    String[] xargs = cmdline.getArgs();
    packs.display("qmix.qmix options:");
    
    if (xargs.length!=2) {
      throw new Error("Number of arguments incorrect");
    }
		String dataFile = xargs[0];
		String outputFile = xargs[1];
    double[][] data = readdoubleMatrix(dataFile);

    if (iopack.hasSeed()) {
      long seed = iopack.getSeed();
      generator.setSeed(seed);
    }
    String predFile = iopack.getPredFile();
    double[][] pred = null;
    if (predFile!=null) {
      pred = readdoubleMatrix(predFile);
      if (pred[0].length!=data[0].length) {
        throw new Error("Data vector length does not equal prediction vector length");
      }
    }    
    
    return run(data,pred,outputFile);
    
  }
  public double[] run(double[][] data, double[][]pred, String outputFile, String[] args) throws ParseException, IOException, Error, Exception {
    CommandLine cmdline = packs.parse(args);
    String[] xargs = cmdline.getArgs();
    packs.display("qmix.qmix options:");
    
    if (xargs.length!=0) {
      throw new Error("Number of arguments incorrect");
    }

    if (iopack.hasSeed()) {
      long seed = iopack.getSeed();
      generator.setSeed(seed);
    }
    return run(data,pred,outputFile);
  }
  
  public double[] run(double[][] data, double[][]pred, String outputFile) throws ParseException, IOException, Error, Exception {

    QGGP qggp = qrmpack.getQRM();    
    MVNormalWishartIndependent prior = priorpack.getMVNormalWishartIndependent(data);
    Factory factory = new MVNormalNonConjugateFactorySampled();
    Mixture mixmodel = mixturepack.getMixture(qggp, prior, factory);
    Sampler sampler = samplerpack.getSampler(mixmodel);
    
    return run(data,pred,outputFile,sampler,qggp,prior,mixmodel);
  }
  
  public double[] run(double[][] data, double[][]pred, String outputFile,
          Sampler sampler, QGGP qggp, MVNormalWishartIndependent prior, Mixture mixture) 
          throws IOException {
    
		ArrayList<Collector> collectors = new ArrayList<Collector>();

    String[] qggpparameters = {"numClusters","qggp_sigma","qggp_logtau","logU","numEmptyClusters"};
    String[] qpypparameters = {"numClusters","qggp_sigma","qpyp_theta","qggp_logtau","logU","numEmptyClusters"};
    String[] parameters;
    if (qggp instanceof QPYP_TU) {
      parameters = qpypparameters;
    } else {
      parameters = qggpparameters;
    }
    collectors.add(new Filer(mixture,outputFile+".parameters",parameters,null));
    String[] precisioninvscale = {"precisionInvScale"};
    collectors.add(new Filer(mixture,outputFile+".invscale",precisioninvscale,null));
		collectors.add(new MixtureClusterFiler(mixture,outputFile+".clusters",outputFile+".assignments"));
    
 	
    int numdata = data.length;
    int numdim = data[0].length;
    for (int i=1; i<numdata; i++) {
      if (data[i].length!=numdim) {
        throw new Error("Some data vectors have different dimensions.");
      }
    }
    if (pred==null) {
      pred = new double[0][];
    }
    for (int i=0; i<pred.length; i++) {
      if (pred[i].length!=numdim) {
        throw new Error("Some prediction vectors have different dimensions from those of data.");
      }
    }
    
    RealVector[] datavec = double2RealVector(data);
    mixture.addData(datavec);
    if (pred!=null) {
      Object predvec = double2RealVector(pred);       
      DataIO.writeDoubleMatrix(outputFile+".predval", pred);
  		String[] logpred = {"logPredictives"};
  		Object[] predarg = {predvec};
  		collectors.add(new Filer(mixture,outputFile+".logpred",logpred,predarg));
    }

    sampler.setCollectors(collectors);
    sampler.setPrintStream(System.out);
    
		PrintStream log = null;
    double[] output = new double[4];
		try {
			log = new PrintStream(outputFile+".log");
  		double[] times =  sampler.run();
      log.println("Run time = "+times[0]);
      log.println("Total time = "+times[1]);
      log.println("Num below minSlice ="+qggp.getNumBelowMinSlice());
      log.println("Num above maxClusters ="+qggp.getNumAboveMaxClusters());
	  	log.close();
      output[0] = times[0];
      output[1] = times[1];
      output[2] = qggp.getNumBelowMinSlice();
      output[3] = qggp.getNumAboveMaxClusters();
      return output;
		} catch(Error ee) {
			System.out.println("Error: "+ee.getMessage());
			if (log!=null) {
        log.close();
      }
			throw ee;
		}
    
  }
  
	public static void main(String[] args) throws ParseException, IOException, Error, Exception {
    qmix qq = new qmix();
    qq.run(args);
  }

  
  
	
  


}