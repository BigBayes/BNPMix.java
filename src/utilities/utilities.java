/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

/**
 *
 * @author ywteh
 */
public class utilities {

  static public Double[] double2Double(double[] x) {
    Double[] y = new Double[x.length];
    for (int i=0; i<x.length; i++) {
      y[i] = x[i];
    }
    return y;
  }
  static public RealVector double2RealVector(double[] x) {
    RealVector y = new ArrayRealVector(x.length);
    for (int i=0; i<x.length; i++) {
      y.setEntry(i, x[i]);
    }
    return y;
  }
  
  static public RealVector[] double2RealVector(double[][] x) {
    RealVector[] y = new RealVector[x.length];
    for (int i=0; i<x.length; i++) {
      y[i] = double2RealVector(x[i]);
    }
    return y;
  }
  
}
