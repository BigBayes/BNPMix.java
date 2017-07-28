package qmix;

import java.io.PrintStream;
import org.apache.commons.cli.CommandLine;
import utilities.OptionPack;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ywteh
 */
public class IOOptionPack extends OptionPack {
  String predfile = null;
  boolean hasseed = false;
  long seed = 0;
  public IOOptionPack() {
    super();
    addOption("PredFile",true,"File for values used for prediction");
    addOption("seed",true,"Random number seed");
  }
  
  public IOOptionPack extract(CommandLine cmdline) {
    if (cmdline.hasOption("PredFile")) {
      predfile = cmdline.getOptionValue("PredFile");
    }
    if (cmdline.hasOption("seed")) {
      hasseed = true;
      seed = Long.parseLong(cmdline.getOptionValue("seed"));
    }
    return this;
  }
  
  public String getPredFile() {
    return predfile;
  }
  public boolean hasSeed() {
    return hasseed;
  }
  public long getSeed() {
    return seed;
  }
  public IOOptionPack display(PrintStream out) {
    out.println("IO options:");
    out.println("  PredFile = "+predfile);
    out.println("  Seed = "+seed);
    return this;
  }
}
