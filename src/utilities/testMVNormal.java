package utilities;


import LinearAlgebra.Cholesky;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import static utilities.Generator.generator;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ywteh
 */
public class testMVNormal {
  public static void main(String[] args) {
    double[][] p = {{5.0, 2.0}, {2.0, 5.0}};
    RealMatrix precision = new Array2DRowRealMatrix(p);
    Cholesky chol = new Cholesky(precision);
    double[] m = {0.0,0.0};
    RealVector mean = new ArrayRealVector(m);
    int n = 10000;
    int df = 4;
    
    RealVector sumx = new ArrayRealVector(2);
    RealMatrix sumxx = new Array2DRowRealMatrix(2,2);
    for (int i=0; i<n; i++) {
      RealVector x = generator.nextMVNormalMeanPrecision(mean, chol);
      sumx = sumx.add(x);
      sumxx = sumxx.add(x.outerProduct(x));
    }
    sumx = sumx.mapMultiply(1.0/(double)n);
    sumxx = sumxx.scalarMultiply(1.0/(double)n).subtract(sumx.outerProduct(sumx));
    RealMatrix Id = new Array2DRowRealMatrix(2,2);
    Id.setEntry(0, 0, 1.0);
    Id.setEntry(1, 1, 1.0);
    System.out.println("mean= "+sumx);
    System.out.println("cov= "+sumxx);
    System.out.println("prec= "+new Cholesky(sumxx).getSolver().solve(Id));
    
    sumxx = new Array2DRowRealMatrix(2,2);
    for (int i=0; i<n; i++) {
      RealMatrix x = generator.nextWishart(df, chol);
      sumxx = sumxx.add(x);
    }
    sumxx = sumxx.scalarMultiply(1.0/(double)n);
    System.out.println("mean= "+sumxx);
    System.out.println("pop mean= "+chol.getSolver().solve(Id).scalarMultiply(df));
  }
}
