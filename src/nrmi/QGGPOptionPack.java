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
public abstract class QGGPOptionPack extends QRMOptionPack {
  static String defaultSigmaAlpha = "1.0";
  static String defaultSigmaBeta  = "2.0";
  double sigmaAlpha = 1.0,
         sigmaBeta  = 2.0;
  
  public QGGPOptionPack() {
    super();
    addOption("SigmaAlpha", true, "Alpha parameter of Beta prior for QGGP sigma parameter");
    addOption("SigmaBeta", true, "Beta parameter of Beta prior for QGGP sigma parameter");
  }
  
  @Override 
  public QGGPOptionPack extract(CommandLine cmdline) throws ParseException {
    sigmaAlpha = Double.parseDouble(cmdline.getOptionValue("SigmaAlpha", defaultSigmaAlpha));
    sigmaBeta  = Double.parseDouble(cmdline.getOptionValue("SigmaBeta" , defaultSigmaBeta ));
    return this;
  }
  public double getSigmaAlpha() {
    return sigmaAlpha;
  }
  public double getSigmaBeta() {
    return sigmaBeta;
  }
  @Override 
  public QGGPOptionPack display(PrintStream out) {
    out.println("QGGP options:");
    out.println("  SigmaAlpha = "+sigmaAlpha);
    out.println("  SigmaBeta  = "+sigmaBeta);
    return this;
  }
          
  
}
