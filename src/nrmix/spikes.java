package nrmix;

import java.io.*;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import static utilities.DataIO.readDoubleMatrix;

/**
 * Runs both the marginalized and conditional sampler on the nrmix.galaxy data set.
 * Marginalized sampler uses 3 new clusters.
 * @author ywteh
 */
public class spikes {
	public static void main(String[] cmd_args) throws IOException {
    Double[][] data = readDoubleMatrix("/Users/ywteh/Research/npbayes/inference/nrmsampler/code/spike/spike6x2000.data");
    RealVector[] Data = new RealVector[data.length];
    for (int i=0; i<data.length; i++) {
      Data[i] = new ArrayRealVector(data[i]);
    }
    RealVector[] Pred = new RealVector[0];
    
		int numBurnin = 1000;
		int numSample = 5000;
		int numThinning = 10;
		int numNewClusters = 1;
		int numPrint = 10;
		double alphaShape = 1;
		double alphaInvScale = 1;
		double sigmaAlpha = 1;
		double sigmaBeta = 2;
		double tauShape = 1e9;
		double tauInvScale = 1e9;

    int numdim = Data[0].getDimension();
    double meanRelScale = 1.0;
    double precisionDegFreedom = numdim+3.0;
    double invScaleDegFreedom = numdim-0.6;
    double precisionScale = 50.0;

    /*
    nrmixmv.run("neal8",Data,Pred,"output.nrmix.spikes",
        numBurnin, numSample, numThinning, numNewClusters, numPrint,
        alphaShape, alphaInvScale, sigmaAlpha, sigmaBeta, tauShape, tauInvScale,
        true,
        meanRelScale,  
        precisionDegFreedom, 
        invScaleDegFreedom, 
        precisionScale);
      
      */
		//nrmix.nrmix.run(true,false,"slice",data,"testlogacidslice");
	}	

}
