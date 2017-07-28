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
public class QPYPOptionPack extends QGGPOptionPack {
  static String defaultThetaShape = "1.0";
  static String defaultThetaInvScale  = "1.0";
  double ThetaShape = 1.0;
  double ThetaInvScale = 1.0;
  static String defaultPYPSampler = "marg";
  String PYPSampler = "marg";
  
  
  public QPYPOptionPack() {
    super();
    addOption("ThetaShape", true, "Shape of PYP Theta parameter");
    addOption("ThetaInvScale", true, "Inverse scale of PYP Theta parameter");
    addOption("PYPSampler", true, "Sampler used for PYP");
  }

  @Override
  public QPYPOptionPack extract(CommandLine cmdline) throws ParseException {
    super.extract(cmdline);
    ThetaShape = Double.parseDouble(cmdline.getOptionValue("ThetaShape", defaultThetaShape));
    ThetaInvScale = Double.parseDouble(cmdline.getOptionValue("ThetaInvScale" , defaultThetaInvScale ));
    PYPSampler = cmdline.getOptionValue("PYPSampler", defaultPYPSampler);
    if (!PYPSampler.equalsIgnoreCase("marg")&
        !PYPSampler.equalsIgnoreCase("TU")&
        !PYPSampler.equalsIgnoreCase("VZ")) {
      throw new ParseException("PYP Sampler option unknown");
    }
    return this;      
  }
  
  public QPYP_TU getQRM() {
    if (PYPSampler.equalsIgnoreCase("marg")) {
      return new QPYP_marg(getSigmaAlpha(),getSigmaBeta(),ThetaShape,ThetaInvScale);
    } else if (PYPSampler.equalsIgnoreCase("TU")) {
      return new QPYP_TU(getSigmaAlpha(),getSigmaBeta(),ThetaShape,ThetaInvScale);
    } else if (PYPSampler.equalsIgnoreCase("VZ")) {
      return new QPYP_VZ(getSigmaAlpha(),getSigmaBeta(),ThetaShape,ThetaInvScale);
    } else {
      throw new Error("PYP Sampler unknown");
    }
  }
  @Override 
  public QPYPOptionPack display(PrintStream out) {
    out.println("QPYP options:");
    out.println("  Algorithm     = "+PYPSampler);
    out.println("  SigmaAlpha    = "+sigmaAlpha);
    out.println("  SigmaBeta     = "+sigmaBeta);
    out.println("  ThetaShape    = "+ThetaShape);
    out.println("  ThetaInvScale = "+ThetaInvScale);    
    return this;
  }
}
