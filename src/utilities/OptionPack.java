/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.io.PrintStream;
import org.apache.commons.cli.*;

/**
 *
 * @author ywteh
 */
public abstract class OptionPack extends Options {
  
  public OptionPack() {
    super();
  }

  public abstract OptionPack extract(CommandLine cmdline) throws ParseException;
  
  public OptionPack parse(String[] args) throws ParseException {
		CommandLineParser parser = new PosixParser();    
    CommandLine cmdline = parser.parse(this, args, true);
    extract(cmdline);
    return this;
  }
  
  public abstract OptionPack display(PrintStream out);
  
  public OptionPack display() {
    return display(System.out);
  }
  
  
}
