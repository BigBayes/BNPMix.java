package experiments;

import xfamily.Normal.*;
import static java.lang.Math.exp;

import java.text.DecimalFormat;

import nrmi.NGGP;

import mixture.*;

import xfamily.*;

public class TestNRMIX_Normal_Neal8 {
	static DecimalFormat df = new DecimalFormat("##.#");	
	static MixtureNeal8 mix;
	static void display() {
		mix.displayInnards();
		predictive();
	}
	static void predictive() {
		double xmin = -100.0;
		double xmax = 100.0;
		double inc = .01;
		double mass = 0.0;
		double xmass = 0.0;
		double xxmass = 0.0;
		for (double x = xmin ; x < xmax ; x+=inc) {
			double prob = exp(mix.logPredictive(x));
			double m = prob*inc;
			if (m>1e-3) System.out.println(x+": "+m);
			mass += m;
			xmass += x*m;
			xxmass += x*x*m;
		}
		System.out.println();
		System.out.println("Total predictive mass = "+mass+"; predictive mean="+xmass
				+"; predictive precision="+(1.0/(xxmass-xmass*xmass)));
	}
	static void add(Double datum) {
		System.out.println("===========Adding "+datum);
		mix.add(datum);		
	}
	static void remove(Double datum) {
		System.out.println("===========Removing "+datum);
		mix.remove(datum);		
	}
	static void sample() {
		System.out.println("===========Sampling ");
		mix.sampleAssignments();		
	}
	public static void main(String[] args) {
		mix = new MixtureNeal8(
				new NGGP(1.0, 1.0, 1.0, 1.0, 1.0, 1.0),
				new NormalGamma(0.0,0.01,2.0,2.0,1.0),
				new NormalConjugateFactorySampled(),5);
		display();
		add(-2.0);
		display();
		add(-2.0);
		add(-2.0);
		add(-2.0);
		display();
		add(2.0);
		add(2.0);
		add(2.0);
		add(2.0);
		display();
		for (int i=0; i<30; i++) {
			sample();
		}		
		display();
	}
}
