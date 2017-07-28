/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package xfamily.Bernoulli;
import utilities.DoubleComparator;
import xfamily.XFamily;
import static java.lang.Math.log;
import static java.lang.Math.PI;
import static utilities.Generator.generator;
import static utilities.SpecialFunctions.logGamma;

/**
 *
 * @author ywteh
 */
public class FactoredDirichlet implements XFamily<FactoredDiscrete> {

    double[][] alpha;

    /**
     * @param alpha
     */
    public FactoredDirichlet(double[][] alpha) {
      this.alpha = new double[alpha.length][];
      for (int i=0; i<alpha.length; i++) {
        this.alpha[i] = new double[alpha[i].length];
        for (int j=0; j<alpha[i].length; j++) {
          this.alpha[i][j] = alpha[i][j];
          if (alpha[i][j]<0.0) throw new Error("Probability < 0.");
        }
      }
    }
    @Override public int numDataDim() { return alpha.length; }
    @Override public int numParamDim() {
      int d=0;
      for (int i=0; i<alpha.length; i++)
        d += alpha[i].length;
      return d;
    }
    @Override public double logNormalizer() {
    	double logz = 0.0;
      for (int i=0; i<alpha.length; i++) {
        double sumalpha = 0.0;
        for (int j=0; j<alpha[i].length; j++) {
          sumalpha += alpha[i][j];
          logz -= logGamma(alpha[i][j]);
        }
        logz += logGamma(sumalpha);
      }
      return logz;
    }
    @Override public double logProbability(FactoredDiscrete datum) {
      if (true) throw new Error("not done yet");

      return 0.0;
    }
    @Override public FactoredDiscrete drawSample() {
      if (true) throw new Error("not done yet");
      return null;
    }
    @Override public Double[] getMeanParameter() {
      if (true) throw new Error("not done yet");
      int d = 0;
      for (int i=0; i<alpha.length; i++)
        d += alpha[i].length-1;
    	Double[] result = new Double[d];
      int j=0;
      for (int i=0; i<alpha.length; i++) {
        for (int k=1; k<alpha[i].length; k++)
          result[j++] = alpha[i][k];
      }
      return result;
    }
    @Override public String toString() {
    	return getClass().getSimpleName()+"(d="+alpha.length+")";
    }
	public enum Properties {
    alpha,
		NOVALUE;
		public static Properties toValue(String str) {
			try {
				return valueOf(str);
			} catch (Exception ex) {
				return NOVALUE;
			}
		}
	}
	@Override public Object get(String property) {
		switch(Properties.toValue(property)) {
    case alpha: return alpha;
		default: throw new Error("Unknown property "+property);
		}
	}
  @Override public Object get(String property, Object arg) {
    return get(property);
  }
}

