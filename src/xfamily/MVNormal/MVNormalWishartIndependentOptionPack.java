/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xfamily.MVNormal;

import java.io.PrintStream;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import utilities.OptionPack;

/**
 *
 * @author ywteh
 */
public class MVNormalWishartIndependentOptionPack extends OptionPack {
  static String defaultUseMeanVar = "false";
  static String defaultMeanRelScale = "1.0";
  static String defaultPrecisionDegFreedomAdjustment = "3.0";
  static String defaultInvScaleDegFreedomAdjustment = "-0.6";
  static String defaultPrecisionScale = "50.0";
  
  boolean UseMeanVar = false;
  double MeanRelScale = 1.0;
  double PrecisionDegFreedomAdjustment = 3.0;    // has to be > -1
  double InvScaleDegFreedomAdjustment = -0.6;    // has to be > -1 
  double PrecisionScale = 50.0;

  public MVNormalWishartIndependentOptionPack() {
    super();
    addOption("UseMeanVar", true, 
            "Use Mean/Variance or Range of data to specific weakly informative MVNormalWishratIndependent prior?");
    addOption("MeanRelScale", true, 
            "Relative Scale Hyperparameter for MVNormalWishartIndependent prior");
    addOption("PrecisionDegFreedomAdjustment", true, 
            "Adjustment Hyperparameter for the Precision Degree of Freedom parameter of MVNormalWishartIndependent prior");
    addOption("InvScaleDegFreedomAdjustment", true,
            "Adjustment Hyperparameter for the Inverse Scale Degree of Freedom parameter of MVNormalWishartIndependent prior");
    addOption("PrecisionScale", true,
            "Scale Hyperparameter for the Precision parameter of MVNormalWishartIndependent prior");
  }
  
  public MVNormalWishartIndependentOptionPack extract(CommandLine cmdline) {
    UseMeanVar = Boolean.parseBoolean(cmdline.getOptionValue("UseMeanVar",defaultUseMeanVar));
    MeanRelScale = Double.parseDouble(cmdline.getOptionValue("MeanRelScale", defaultMeanRelScale));
    PrecisionDegFreedomAdjustment = Double.parseDouble(cmdline.getOptionValue("PrecisionDegFreedomAdjustment" , defaultPrecisionDegFreedomAdjustment));
    InvScaleDegFreedomAdjustment = Double.parseDouble(cmdline.getOptionValue("InvScaleDegFreedomAdjustment", defaultInvScaleDegFreedomAdjustment));
    PrecisionScale = Double.parseDouble(cmdline.getOptionValue("PrecisionScale", defaultPrecisionScale));
    return this;
  }
  public boolean getUseMeanVar() {
    return UseMeanVar;
  }
  public double getMeanRelScale() {
    return MeanRelScale;
  }
  public double getPrecisionDegFreedomAdjustment() {
    return PrecisionDegFreedomAdjustment;
  }
  public double getInvScaleDegFreedomAdjustment() {
    return InvScaleDegFreedomAdjustment;
  }
  public double getPrecisionScale() {
    return PrecisionScale;
  }
  
  public MVNormalWishartIndependent getMVNormalWishartIndependent(double[][] data) throws Error, Exception {
    return MVNormalWishartIndependent.constructPrior(data, 
            UseMeanVar, 
            MeanRelScale, 
            PrecisionDegFreedomAdjustment, 
            InvScaleDegFreedomAdjustment,
            PrecisionScale);
  }
  
  public MVNormalWishartIndependentOptionPack display() {
    return display(System.out);
  }
  public MVNormalWishartIndependentOptionPack display(PrintStream out) {
    out.println("MVNormalWishartIndependent options:");
    out.println("  MeanRelScale                  = "+MeanRelScale);
    out.println("  PrecisionDegFreedomAdjustment = "+PrecisionDegFreedomAdjustment);
    out.println("  InvScaleDegFreedomAdjustment  = "+InvScaleDegFreedomAdjustment);
    out.println("  PrecisionScale                = "+PrecisionScale);
    return this;
  }
}
