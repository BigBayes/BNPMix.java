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
public class galaxy_qggp {
	public static void main(String[] cmd_args) throws IOException, ParseException, Error, Exception {
    double mindata = Double.POSITIVE_INFINITY;
    double maxdata = Double.NEGATIVE_INFINITY;
    for (int i=0; i<data.length; i++) {
      data[i][0] = data[i][0]/1e4;
      if (mindata>data[i][0]) {
        mindata = data[i][0];
      }
      if (maxdata<data[i][0]) {
        maxdata = data[i][0];
      }
    }
    double[][] pred = new double[100][1];
    for (int i=0; i<100; i++) {
      pred[i][0] = mindata + (maxdata-mindata)/99*i;
    }

    MVNormalWishartIndependent prior = (new MVNormalWishartIndependentOptionPack())
        .display()
        .getMVNormalWishartIndependent(data);
    MVNormalNonConjugateFactory factory = new MVNormalNonConjugateFactorySampled();

    System.out.println(prior);
    System.out.println(factory);

    qmix qq = new qmix();
    String output = "/Users/ywteh/Research/npbayes/inference/nrmsampler/code/nrmix.nrmix/matlab/qmix.galaxy_qggp";

    QGGP qggp;
    Mixture mixmodel;
    Sampler sampler;
    
    if (true){    

    qggp = new QPYP_TU(2,4,1,1);
    mixmodel = new MixtureReuse(qggp,prior,factory,5);    
    sampler = new Sampler(mixmodel,10000,10000,10,10);
    System.out.println(qggp);
    System.out.println(mixmodel);    
    System.out.println(sampler);
		qq.run(data,pred,output+"_short_tu",
            sampler, qggp, prior, mixmodel);

    qggp = new QPYP_VZ(2,4,1,1);
    mixmodel = new MixtureReuse(qggp,prior,factory,5);    
    sampler = new Sampler(mixmodel,10000,10000,10,10);
    System.out.println(qggp);
    System.out.println(mixmodel);    
    System.out.println(sampler);
		qq.run(data,pred,output+"_short_vz",
            sampler, qggp, prior, mixmodel);

    qggp = new QPYP_marg(2,4,1,1);
    mixmodel = new MixtureReuse(qggp,prior,factory,5);    
    sampler = new Sampler(mixmodel,10000,10000,10,10);
    System.out.println(qggp);
    System.out.println(mixmodel);    
    System.out.println(sampler);
		qq.run(data,pred,output+"_short_marg",
            sampler, qggp, prior, mixmodel);
    
    qggp = new QGGPLogNormal(2,4,0,1);
    mixmodel = new MixtureReuse(qggp,prior,factory,5);    
    sampler = new Sampler(mixmodel,10000,10000,10,10);
    System.out.println(qggp);
    System.out.println(mixmodel);    
    System.out.println(sampler);
		qq.run(data,pred,output+"_short_lognormal",
            sampler, qggp, prior, mixmodel);
  }
    
    qggp = new QPYP_TU(2e9,4e9,1e9,1e9);
    mixmodel = new MixtureReuse(qggp,prior,factory,5);    
    sampler = new Sampler(mixmodel,10000,10000,10,10);
    System.out.println(qggp);
    System.out.println(mixmodel);    
    System.out.println(sampler);
		qq.run(data,pred,output+"_fixed_tu",
            sampler, qggp, prior, mixmodel);
    
    qggp = new QPYP_VZ(2e9,4e9,1e9,1e9);
    mixmodel = new MixtureReuse(qggp,prior,factory,5);    
    sampler = new Sampler(mixmodel,10000,10000,10,10);
    System.out.println(qggp);
    System.out.println(mixmodel);    
    System.out.println(sampler);
		qq.run(data,pred,output+"_fixed_vz",
            sampler, qggp, prior, mixmodel);

    qggp = new QPYP_marg(2e9,4e9,1e9,1e9);
    mixmodel = new MixtureReuse(qggp,prior,factory,5);    
    sampler = new Sampler(mixmodel,10000,10000,10,10);
    System.out.println(qggp);
    System.out.println(mixmodel);    
    System.out.println(sampler);
		qq.run(data,pred,output+"_fixed_marg",
            sampler, qggp, prior, mixmodel);
	}	
	
	public static double[][] data = {
    {9172.0},
		{9350.0},
		{9483.0},
		{9558.0},
		{9775.0},
		{10227.0},
		{10406.0},
		{16084.0},
		{16170.0},
		{18419.0},
		{18552.0},
		{18600.0},
		{18927.0},
		{19052.0},
		{19070.0},
		{19330.0},
		{19343.0},
		{19349.0},
		{19440.0},
		{19473.0},
		{19529.0},
		{19541.0},
		{19547.0},
		{19663.0},
		{19846.0},
		{19856.0},
		{19863.0},
		{19914.0},
		{19918.0},
		{19973.0},
		{19989.0},
		{20166.0},
		{20175.0},
		{20179.0},
		{20196.0},
		{20215.0},
		{20221.0},
		{20415.0},
		{20629.0},
		{20795.0},
		{20821.0},
		{20846.0},
		{20875.0},
		{20986.0},
		{21137.0},
		{21492.0},
		{21701.0},
		{21814.0},
		{21921.0},
		{21960.0},
		{22185.0},
		{22209.0},
		{22242.0},
		{22249.0},
		{22314.0},
		{22374.0},
		{22495.0},
		{22746.0},
		{22747.0},
		{22888.0},
		{22914.0},
		{23206.0},
		{23241.0},
		{23263.0},
		{23484.0},
		{23538.0},
		{23542.0},
		{23666.0},
		{23706.0},
		{23711.0},
		{24129.0},
		{24285.0},
		{24289.0},
		{24366.0},
		{24717.0},
		{24990.0},
		{25633.0},
		{26960.0},
		{26995.0},
		{32065.0},
		{32789.0},
		{34279.0}
	};
}
