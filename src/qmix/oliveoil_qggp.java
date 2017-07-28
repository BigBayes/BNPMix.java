package qmix;

import java.io.*;
import mcmc.Sampler;
import mixture.Mixture;
import mixture.MixtureReuse;
import nrmi.QGGP;
import nrmi.QGGPLogNormal;
import nrmi.QPYP_TU;
import nrmi.QPYP_VZ;
import nrmi.QPYP_marg;
import org.apache.commons.cli.ParseException;
import xfamily.MVNormal.MVNormalNonConjugateFactory;
import xfamily.MVNormal.MVNormalNonConjugateFactorySampled;
import xfamily.MVNormal.MVNormalWishartIndependent;
import xfamily.MVNormal.MVNormalWishartIndependentOptionPack;

/**
 * Runs both the marginalized and conditional sampler on the nrmix.galaxy data set.
 * Marginalized sampler uses 3 new clusters.
 * @author ywteh
 */
public class oliveoil_qggp {
	public static void main(String[] cmd_args) throws IOException, ParseException, Error, Exception {
    String dir = "/Users/ywteh/Research/npbayes/inference/nrmsampler/code/nrmix.nrmix/matlab/";
    double[][] data = utilities.DataIO.readdoubleMatrix(dir+"oliveoil/oliveoil6pca.txt");
    
    double[][] pred = new double[0][6];

    MVNormalWishartIndependent prior = (new MVNormalWishartIndependentOptionPack())
        .display()
        .getMVNormalWishartIndependent(data);
    MVNormalNonConjugateFactory factory = new MVNormalNonConjugateFactorySampled();

    System.out.println(prior);
    System.out.println(factory);

    qmix qq = new qmix();
    String output = dir+"statcomp/oliveoil";

    QGGP qggp = new QPYP_TU(2,4,1,1);
    Mixture mixmodel = new MixtureReuse(qggp,prior,factory,10);    
    Sampler sampler = new Sampler(mixmodel,10000,10000,100,10);
    System.out.println(qggp);
    System.out.println(mixmodel);    
    System.out.println(sampler);
		qq.run(data,pred,output+"_tu",
            sampler, qggp, prior, mixmodel);
    
    qggp = new QPYP_VZ(2,4,1,1);
    mixmodel = new MixtureReuse(qggp,prior,factory,10);    
    sampler = new Sampler(mixmodel,10000,10000,100,10);
    System.out.println(qggp);
    System.out.println(mixmodel);    
    System.out.println(sampler);
		qq.run(data,pred,output+"_vz",
            sampler, qggp, prior, mixmodel);

    qggp = new QPYP_marg(2,4,1,1);
    mixmodel = new MixtureReuse(qggp,prior,factory,10);    
    sampler = new Sampler(mixmodel,10000,10000,100,10);
    System.out.println(qggp);
    System.out.println(mixmodel);    
    System.out.println(sampler);
		qq.run(data,pred,output+"_marg",
            sampler, qggp, prior, mixmodel);
    
    qggp = new QGGPLogNormal(2,4,0,1);
    mixmodel = new MixtureReuse(qggp,prior,factory,5);    
    sampler = new Sampler(mixmodel,10000,10000,100,10);
    System.out.println(qggp);
    System.out.println(mixmodel);    
    System.out.println(sampler);
		qq.run(data,pred,output+"_lognormal",
            sampler, qggp, prior, mixmodel);
	}	
	

}
