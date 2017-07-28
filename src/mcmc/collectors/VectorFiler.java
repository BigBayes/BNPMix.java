/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mcmc.collectors;
import mcmc.Sampleable;
import java.text.Format;
import java.io.*;
/**
 *
 * @author ywteh
 */
public class VectorFiler<X> extends AbstractFiler {
  String property;
  Object arg;
  Format format;

  public VectorFiler(Sampleable client_, PrintStream stream,
          String property_, Object arg_) {
    super(client_,stream);
    property = property_;
    arg = arg_;
  }
  public VectorFiler(Sampleable client_, String filename,
          String property_, Object arg_) throws IOException {
    super(client_,filename);
    property = property_;
    arg = arg_;
  }
  public VectorFiler(Sampleable client_, PrintStream stream,
          String property_) {
    this(client_,stream,property_,null);
  }
  public VectorFiler(Sampleable client_, String filename,
          String property_) throws IOException {
    this(client_,filename,property_,null);
  }
  public void setFormat(Format f) {
    format = f;
  }

  public void collect() {
    X[] sample;
    if (arg==null)
      sample = (X[])client.get(property,null);
    else
      sample = (X[])client.get(property,arg);
    for ( int ii = 0 ; ii < sample.length ; ii ++ ) {
      if (format==null)
        output.print(sample[ii].toString()+"\t");
      else
        output.print(format.format(sample[ii])+"\t");
    }
    output.println();
    output.flush();
  }

  public void close() {
    output.close();
  }
}
