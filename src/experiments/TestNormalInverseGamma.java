package experiments;

import xfamily.Normal.NormalConjugateHierarchyMarginalized;
import xfamily.Normal.NormalGamma;
import static java.lang.Math.exp;
import utilities.Generator;
import xfamily.*;

public class TestNormalInverseGamma {

	/**
	 * @param args
	 */
	static Generator gen = new Generator();
	static NormalConjugateHierarchyMarginalized nig;
	
	public static void display() {
		System.out.println(nig);
		predictive();
		System.out.println(" mean="+nig.getMean()+" samples=");
		for (int i=0; i<10; i++)
			System.out.println("    "+nig.getParameter());
	}
	public static void add(double x) { add(x,1); }
	public static void add(double x,int n) {
		System.out.println("======= adding "+x+" "+n+" times");
		for (int i=0;i<n;i++) nig.addDatum(x);
		display();
	}
	public static void remove(double x) { remove(x,1); }
	public static void remove(double x,int n) {
		System.out.println("======= removing "+x+" "+n+" times");
		for (int i=0;i<n;i++) nig.removeDatum(x);
		display();
	}
	public static void predictive() {
		double xmin = -1000.0;
		double xmax = 1000.0;
		double inc = .01;
		double mass = 0.0;
		double xmass = 0.0;
		double xxmass = 0.0;
		for (double x = xmin ; x < xmax ; x+=inc) {
			double m = exp(nig.logPredictive(x))*inc;
			mass += m;
			xmass += x*m;
			xxmass += x*x*m;
		}
		System.out.println("Total predictive mass = "+mass+"; predictive mean="+xmass
				+"; predictive precision="+(1.0/(xxmass-xmass*xmass)));
	}
	public static void main(String[] args) {
		nig = new NormalConjugateHierarchyMarginalized(new NormalGamma(0.0,0.01,2.0,2.0,1.0));
		display();
		add(0.0);
		remove(0.0);
		add(0.0,16);
		remove(0.0,16);
		add(10.0,16);
		remove(10.0,16);
		add(10.0,100);
		add(11.0,100);

	}

}
