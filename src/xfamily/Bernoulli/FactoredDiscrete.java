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

/**
 *
 * @author ywteh
 */
public class FactoredDiscrete implements XFamily<Integer[]> {

    double[][] probs;

    /**
     * @param probs
     */
    public FactoredDiscrete(double[][] probs) {
      this.probs = new double[probs.length][];
      for (int i=0; i<probs.length; i++) {
        this.probs[i] = new double[probs[i].length];
        double t = 0.0;
        for (int j=0; j<probs[i].length; j++) {
          this.probs[i][j] = probs[i][j];
          if (probs[i][j]<0.0) throw new Error("Probability < 0.");
          t += probs[i][j];
        }
        if (!DoubleComparator.isEqual(t, 1.0))
          throw new Error("Probabilities do not sum to 1.0.");
      }
    }
    @Override public int numDataDim() { return probs.length; }
    @Override public int numParamDim() {
      int d=0;
      for (int i=0; i<probs.length; i++)
        d += probs[i].length-1;
      return d;
    }
    @Override public double logNormalizer() {
    	return 0.0;
    }
    @Override public double logProbability(Integer[] datum) {
      if (datum.length!=probs.length) throw new Error("Data and distribution dimension mismatch.");
      double lp = 0.0;
      for (int i=0; i<probs.length; i++) {
        int x = datum[i];
        if (x>=probs[i].length) throw new Error("Data value does not matched distribution.");
        lp += log(probs[i][x]);
      }
      return lp;
    }
    @Override public Integer[] drawSample() {
      Integer[] x = new Integer[probs.length];
      for (int i=0; i<probs.length; i++) {
        x[i] = generator.nextMultinomial(probs[i]);
      }
      return x;
    }
    @Override public Double[] getMeanParameter() {
      int d = 0;
      for (int i=0; i<probs.length; i++)
        d += probs[i].length-1;
    	Double[] result = new Double[d];
      int j=0;
      for (int i=0; i<probs.length; i++) {
        for (int k=1; k<probs[i].length; k++)
          result[j++] = probs[i][k];
      }
      return result;
    }
    @Override public String toString() {
    	return getClass().getSimpleName()+"(d="+probs.length+")";
    }

	public enum Properties {
    probs,
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
    case probs: return probs;
		default: throw new Error("Unknown property "+property);
		}
	}
  @Override public Object get(String property, Object arg) {
    return get(property);
  }

}

