/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mixture;

import java.io.PrintStream;
import nrmi.NRMI;
import nrmi.QGGP;
import org.apache.commons.cli.AlreadySelectedException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import utilities.OptionPack;
import utilities.Factory;
import xfamily.MVNormal.MVNormalNonConjugateFactorySampled;
import xfamily.XPrior;

/**
 *
 * @author ywteh
 */
public class MixtureOptionPack extends OptionPack {
  static String defaultNumEmptyClusters = "10";
  static String defaultMaxEmptyClusters = "100000";
  int NumEmptyClusters = 10;
  int MaxEmptyClusters = 100000;
  Option reuse;
  Option neal8;
  Option slice;
  Option marginalized;
  Option sampled;
  OptionGroup alg;
  OptionGroup marg;
  public MixtureOptionPack() {
    super();
    alg = new OptionGroup();
    alg.addOption(reuse = new Option("Reuse","Use the reuse algorithm"))
       .addOption(neal8 = new Option("Neal8","Use Neal's algorithm 8"))
       .addOption(slice = new Option("Slice","Use the slice algorithm"));
    alg.setRequired(true);
       
    marg = new OptionGroup();
    marg.addOption(marginalized = new Option("Marginalized","Marginalize out cluster parameters"))
        .addOption(sampled = new Option("Sampled","Sample cluster parameters"));
    marg.setRequired(true);
    addOptionGroup(alg);
    addOptionGroup(marg);
    addOption("NumEmptyClusters", true, "Number of empty clusters to maintain (reuse,neal8)");
    //options.addOption("MaxEmptyClusters", true, "Maximum number of empty clusters to introduce (slice)");
  }
  public MixtureOptionPack display(PrintStream out) {
    out.println("Mixture options:");
    out.println("  Algorithm        = "+alg.getSelected()+", "+marg.getSelected());
    out.println("  NumEmptyClusters = "+NumEmptyClusters);
    return this;
  }

  @Override
  public MixtureOptionPack extract(CommandLine cmdline) throws AlreadySelectedException {
    NumEmptyClusters = Integer.parseInt(cmdline.getOptionValue("NumEmptyClusters", defaultNumEmptyClusters));
    if (cmdline.hasOption("Reuse")) {
      alg.setSelected(reuse);
    } else if (cmdline.hasOption("Neal8")) {
      alg.setSelected(neal8);
    } else if (cmdline.hasOption("Slice")) {
      alg.setSelected(slice);
    } else {
      throw new Error("mixture algorithm option not set");
    }
    if (cmdline.hasOption("Marginalized")) {
      marg.setSelected(marginalized);
    } else if (cmdline.hasOption("Sampled")) {
      marg.setSelected(sampled);
    }
    
    //MaxEmptyClusters = Integer.parseInt(cmdline.getOptionValue("MaxEmptyClusters", defaultMaxEmptyClusters));
    return this;
  }
  
  public Mixture getMixture(NRMI qggp, XPrior prior, Factory factory) {
    // Ignore marginalizing out cluster parameters.
    if (alg.getSelected().equals("Reuse")) {
      return new MixtureReuse(qggp, prior, factory, NumEmptyClusters);
    } else if (alg.getSelected().equals("Neal8")) {
      return new MixtureNeal8(qggp, prior, factory, NumEmptyClusters);
    } else if (alg.getSelected().equals("Slice")) {
      return new MixtureSlice(qggp, prior, factory);
    } else {
      throw new Error("Unknown algorithm selected?!?");
    }
  }
}
