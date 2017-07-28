/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nrmi;

import java.io.PrintStream;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author ywteh
 */
public class QGGPLogNormalOptionPack extends QGGPOptionPack {
  static String defaultLogTauMean = "0.0";
  static String defaultLogTauVar  = "1.0";
  double LogTauMean = 0.0;
  double LogTauVar  = 1.0;
  
  
  public QGGPLogNormalOptionPack() {
    super();
    addOption("LogTauMean", true, "Mean of logarithm of QGGP tau parameter");
    addOption("LogTauVar", true, "Variance of logarithm of QGGP tau parameter");
  }

  @Override
  public QGGPLogNormalOptionPack extract(CommandLine cmdline) throws ParseException {
    super.extract(cmdline);
    LogTauMean = Double.parseDouble(cmdline.getOptionValue("LogTauMean", defaultLogTauMean));
    LogTauVar  = Double.parseDouble(cmdline.getOptionValue("LogTauVar" , defaultLogTauVar ));
    return this;
  }
  
  public QGGPLogNormal getQRM() {
    return new QGGPLogNormal(getSigmaAlpha(),getSigmaBeta(),LogTauMean,LogTauVar);
  }
  @Override 
  public QGGPLogNormalOptionPack display(PrintStream out) {
    out.println("QGGPLogNormal options:");
    out.println("  SigmaAlpha = "+sigmaAlpha);
    out.println("  SigmaBeta  = "+sigmaBeta);
    out.println("  LogTauMean = "+LogTauMean);
    out.println("  LogTauVar  = "+LogTauVar);
    return this;
  }
}
