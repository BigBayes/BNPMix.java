package xfamily.Normal;

import xfamily.XFamily;
import static java.lang.Math.log;
import static java.lang.Math.PI;
import static utilities.Generator.generator;

public class Normal implements XFamily<Double> {
	static double halflog2pi = .5*log(2.0*PI);
    public double mean;
    public double precision;

    /**
     * @param mean
     * @param precision
     */
    public Normal(double mean, double precision) {
    	if (precision<0.0) throw new Error("Precision < 0.");
	    this.mean = mean;
	    this.precision = precision;
    }
    @Override public int numDataDim() { return 1; }
    @Override public int numParamDim() { return 2; }
    @Override public double logNormalizer() {
    	return halflog2pi -.5*log(precision);
    }
    @Override public double logProbability(Double datum) {
    	double diff = datum-mean;
    	return -.5*diff*diff*precision - logNormalizer();
    }
    @Override public Double drawSample() {
      return generator.nextGaussian(mean,1/precision);
    }
    @Override public Double[] getMeanParameter() {
      Double[] result = new Double[2];
      result[0] = mean*precision;
      result[1] = -.5*precision;
    	return result;
    }

	public enum Properties {
    mean,
    precision,
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
    case mean: return mean;
    case precision: return precision;
		default: throw new Error("Unknown property "+property);
		}
	}
  @Override public Object get(String property, Object arg) {
    return get(property);
  }

    @Override public String toString() {
    	return "N(m="+mean+",v="+(1.0/precision)+")";
    }
}

