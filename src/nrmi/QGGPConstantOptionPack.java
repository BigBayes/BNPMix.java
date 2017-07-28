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
public class QGGPConstantOptionPack extends QGGPOptionPack {
  static String defaultLogTau = "0.0";
  double LogTau = 0.0;
  
  public QGGPConstantOptionPack() {
    super();
    addOption("LogTau", true, "Logarithm of QGGP tau parameter");
  }
  
  @Override
  public QGGPConstantOptionPack extract(CommandLine cmdline) throws ParseException {
    super.extract(cmdline);
    LogTau = Double.parseDouble(cmdline.getOptionValue("LogTau", defaultLogTau));
    return this;
  }
  
  public QGGPConstant getQRM() {
    return new QGGPConstant(getSigmaAlpha(),getSigmaBeta(),LogTau);
  }
  
  @Override 
  public QGGPConstantOptionPack display(PrintStream out) {
    out.println("QGGPConstant options:");
    out.println("  SigmaAlpha = "+sigmaAlpha);
    out.println("  SigmaBeta  = "+sigmaBeta);
    out.println("  LogTau     = "+LogTau);
    return this;
  }
}
