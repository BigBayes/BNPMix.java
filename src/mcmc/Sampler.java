package mcmc;

import mcmc.Sampleable;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;


import java.util.List;
import mcmc.collectors.Collector;

import utilities.Generator;

public class Sampler {
	Sampleable model;
	int numBurnIn;
	int numSample;
	int numThinning;
	int numPrint;
	Generator gen;
	List<Collector> collectors;
	PrintStream out;
	public double totaltime, runtime;
	
	public Sampler(Sampleable model,
			int numBurnIn, int numSample, int numThinning, int numPrint) {
    this(model,null,null,numBurnIn,numSample,numThinning,numPrint);
  }
	public Sampler(Sampleable model, List<Collector> collectors, PrintStream out,
			int numBurnIn, int numSample, int numThinning, int numPrint) {
		if (numBurnIn<0) throw new Error("Number of burn-in iterations < 0.");
		if (numSample<0) throw new Error("Number of samples < 0.");
		if (numThinning<0) throw new Error("Number of thinning iterations < 0.");
		this.model = model;
		this.collectors = collectors;
		this.out = out;
		this.numBurnIn = numBurnIn;
		this.numSample = numSample;
		this.numThinning = numThinning;
		this.numPrint = numPrint;
		gen = new Generator();
	}
  
  public void setCollectors(List<Collector> collectors) {
    this.collectors = collectors;
  }
  public void setPrintStream(PrintStream out) {
    this.out = out;
  }
	
	public void collect() {
		for ( Collector collector : collectors ) 
			collector.collect();
	}

	public void sample(int numIteration) {
		for (int i=1; i<=numIteration; i++) 
			model.sample();
	}
	
	/**
	 * Runs an MCMC chain, by initializing the chain, burn-in, collect samples, then finalize.
	 * 
	 * @param out	PrintStream to produce intermediate progress output every numPrint samples.
	 * 				Null if this is unneeded.
	 * @param numPrint	Number of samples in between progress output. 
	 */
	void print(String str) {
		if (out!=null) out.print(str);
	}
	void println(String str) {
		if (out!=null) out.println(str);
	}
	void println() {
		if (out!=null) out.println();
	}
	void flush() {
		if (out!=null) out.flush();
    for ( Collector cc : collectors )
      cc.flush();
	}
	public double[] run() {
		long starttime = System.currentTimeMillis();
		print("  Burn-in: ");
		model.initializeSampler();
		long prevtime = System.currentTimeMillis();
		for (int i=1; i<=numBurnIn; i++) {
			model.sample();
			if (i%(numThinning*numPrint) == 0) print(".");
		}
		long curtime = System.currentTimeMillis();
		long lruntime = curtime - prevtime;
		println();
		flush();
		print("  Sampling: ");
		for (int i=1; i<=numSample; i++) {
			prevtime = System.currentTimeMillis();
			sample(numThinning);
			curtime = System.currentTimeMillis();
			lruntime += curtime - prevtime;
			if (i%numPrint == 0) print(".");
			collect();
			flush();
		}
		println();
		model.finishSampler();
		for ( Collector collector : collectors ) 
			collector.finish();
		totaltime = ((double)(System.currentTimeMillis()-starttime))/1000.0;
		runtime = ((double)lruntime)/1000.0;
		println("Done: "+
			" Run time = "+runtime+
			" Total time = "+totaltime);
    flush();
    double[] timeresult = new double[2];
    timeresult[0] = runtime;
    timeresult[1] = totaltime;
    return timeresult;
	}
}
