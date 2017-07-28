package utilities;

import java.util.*;
import java.io.*;

/**
 * Simple data input and output utilities.
 * @author ywteh
 */
public class DataIO {
	static int verbosity = 0;

	static PrintStream logger = System.out;
  public static void setLogger(PrintStream stream) { logger = stream; }
	public static void setVerbosity(int v) { verbosity = v; }
	public static void vlog(int level, String str) {
		if (level<=verbosity) {
      logger.print(str);
    }
	}
	public static void vlogln(int level, String str) {
		if (level<=verbosity) {
      logger.println(str);
    }
	}
	
  public static boolean getBoolean(String s) {
    String l = s.toLowerCase();
    if (l.equals("true")||l.equals("t")||l.equals("1")) {
      return true;
    } else if (l.equals("false")||l.equals("f")||l.equals("0")) {
      return false;
    } else {
      throw new Error("Unknown boolean value "+s);
    }
  }

  public static Double[] readDoubleArray(String filename) throws IOException {
    ArrayList<Double> data = readDoubleArrayList(filename);
    Double[] result = new Double[data.size()];
    for (int i=0; i<data.size(); i++) {
      result[i] = data.get(i);
    }
    return result;
	}
	public static ArrayList<Double> readDoubleArrayList(String filename) throws IOException {
		Scanner scan = null;
		try {
			scan = new Scanner(new BufferedReader(new FileReader(filename)));
			scan.useLocale(Locale.US);
		} catch(Error ee) {
			System.out.println("Unable to open "+filename+": "+ee.getMessage());
			if ( scan != null ) {
        scan.close();
      }
			throw ee;
		}
		ArrayList<Double> result = new ArrayList<Double>();
		try {
			while(true) {
        result.add(scan.nextDouble());
      }
		} catch (Exception e) {
			scan.close();
			return result;
		}
	}
	public static Double[][] readDoubleMatrix(String filename) throws IOException {
    ArrayList<ArrayList<Double>> data = readDoubleArrayMatrix(filename);
    Double[][] result = new Double[data.size()][];
    for (int i=0; i<data.size(); i++) {
      ArrayList<Double> d2 = data.get(i);
      result[i] = new Double[d2.size()];
      for (int j=0; j<d2.size(); j++) {
        result[i][j] = d2.get(j);
      }
    }
    return result;
	}
	public static double[][] readdoubleMatrix(String filename) throws IOException {
    ArrayList<ArrayList<Double>> data = readDoubleArrayMatrix(filename);
    double[][] result = new double[data.size()][];
    for (int i=0; i<data.size(); i++) {
      ArrayList<Double> d2 = data.get(i);
      result[i] = new double[d2.size()];
      for (int j=0; j<d2.size(); j++) {
        result[i][j] = d2.get(j);
      }
    }
    return result;
	}
	public static ArrayList<ArrayList<Double>> readDoubleArrayMatrix(String filename) throws IOException {
		Scanner scan = null;
		try {
			scan = new Scanner(new BufferedReader(new FileReader(filename)));
			scan.useLocale(Locale.US);
		} catch(Error ee) {
			System.out.println("Unable to open "+filename+": "+ee.getMessage());
			if ( scan != null ) {
        scan.close();
      }
			throw ee;
		}
		ArrayList<ArrayList<Double>> result = new ArrayList<ArrayList<Double>>();
		try {
			while(true) {
        String s = scan.nextLine();
        Scanner s2 = new Scanner(s);
        ArrayList<Double> r2 = new ArrayList<Double>();
        try {
          while(true) {
            r2.add(s2.nextDouble());
          }
        } catch (Exception e) {
          s2.close();
        }
        result.add(r2);
      }
		} catch (Exception e) {
			scan.close();
			return result;
		}
	}

	public static void writeDoubleArray(String filename, ArrayList<Double> data) throws IOException {
		PrintStream output = null;
		try {
			output = new PrintStream(filename);
		} catch(Error ee) {
			System.out.println("Unable to open "+filename+": "+ee.getMessage());
			if (output!=null) {
        output.close();
      }
			throw ee;
		}
		for ( Double dd : data ) {
      output.println(dd);
    }
		output.close();
	}

	public static void writeDoubleArray(String filename, Double[] data) throws IOException {
		PrintStream output = null;
		try {
			output = new PrintStream(filename);
		} catch(Error ee) {
			System.out.println("Unable to open "+filename+": "+ee.getMessage());
			if (output!=null) {
        output.close();
      }
			throw ee;
		}
		for ( double dd : data ) {
      output.println(dd);
    }
		output.close();
	}
	public static void writeDoubleMatrix(String filename, double[][] data) throws IOException {
		PrintStream output = null;
		try {
			output = new PrintStream(filename);
		} catch(Error ee) {
			System.out.println("Unable to open "+filename+": "+ee.getMessage());
			if (output!=null) {
        output.close();
      }
			throw ee;
		}
		for ( double[] dd : data ) {
      for ( double d : dd ) {
        output.print(d+" ");
      }
      output.println();
    }
		output.close();
	}
	public static void writeDoubleMatrix(String filename, Double[][] data) throws IOException {
		PrintStream output = null;
		try {
			output = new PrintStream(filename);
		} catch(Error ee) {
			System.out.println("Unable to open "+filename+": "+ee.getMessage());
			if (output!=null) {
        output.close();
      }
			throw ee;
		}
		for ( Double[] dd : data ) {
      for ( Double d : dd ) {
        output.print(d+" ");
      }
      output.println();
    }
		output.close();
	}

}
