package mcmc.collectors;

import java.io.*;
import mcmc.Sampleable;

public class MatrixCollector implements Collector {
    private Sampleable cc;
    private String property;
    private int index = 0;
    String prefix;

    public MatrixCollector(Sampleable cc, String property, String prefix) {
        this.cc = cc;
        this.property = property;
        this.prefix = prefix;

    }

    @Override public void collect() {
        String filename = prefix + "." + String.format("%04d", index++);
        PrintStream output = null;
        try {
          output = new PrintStream(filename);
        } catch (IOException ee) {
          System.out.println("Unable to open "+filename+": "+ee.getMessage());
          if ( output != null ) output.close();
        }
        double[][] matrix = (double[][])cc.get(property);
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                output.print(matrix[i][j]);
                if (j < matrix[i].length-1) {
                    output.print(" ");
                }
            }
            output.println();
        }

        
        output.close();
    }

    public void flush() {}
    public void finish() {}
}
