/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmc;

import java.io.PrintStream;
import java.util.List;
import mcmc.collectors.Collector;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import utilities.OptionPack;

/**
 *
 * @author ywteh
 */
public class SamplerOptionPack extends OptionPack {
  static String defaultNumBurnin = "10000";
  static String defaultNumSample = "10000";
  static String defaultNumThinning = "10";
  static String defaultNumPrint = "10";
  int NumBurnin = 10000;
  int NumSample = 10000;
  int NumThinning  = 10;
  int NumPrint = 10;
  
  public SamplerOptionPack() {
    super();
    addOption("NumBurnin", true, "Number of MCMC burn-in iterations");
    addOption("NumSample", true, "Number of MCMC samples");
    addOption("NumThinning", true, "Number of MCMC iterations between samples");
    addOption("NumPrint", true, "Number of MCMC samples for each printed '.'");    
  }
  
  @Override 
  public SamplerOptionPack extract(CommandLine cmdline) {
    NumBurnin   = Integer.parseInt(cmdline.getOptionValue("NumBurnin"  , defaultNumBurnin  ));
    NumSample   = Integer.parseInt(cmdline.getOptionValue("NumSample"  , defaultNumSample  ));
    NumThinning = Integer.parseInt(cmdline.getOptionValue("NumThinning", defaultNumThinning));
    NumPrint    = Integer.parseInt(cmdline.getOptionValue("NumPrint"   , defaultNumPrint));
    return this;
  }
  public Sampler getSampler(Sampleable model, List<Collector> collectors, PrintStream out) {
    return new Sampler(model,collectors,out,NumBurnin,NumSample,NumThinning,NumPrint);
  }
  public Sampler getSampler(Sampleable model) {
    return new Sampler(model,NumBurnin,NumSample,NumThinning,NumPrint);
  }
  
  @Override 
  public SamplerOptionPack display(PrintStream out) {
    out.println("Sampler options:");
    out.println("  NumBurnin   = "+NumBurnin);
    out.println("  NumSample   = "+NumSample);
    out.println("  NumThinning = "+NumThinning);
    out.println("  NumPrint    = "+NumPrint);
    return this;
  }
}
