/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.io.PrintStream;
import java.util.ArrayList;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 *
 * @author ywteh
 */
public class OptionPacks extends ArrayList<OptionPack> {
  public OptionPacks() {
    super();
  }
  
  public CommandLine parse(String[] args) throws ParseException {
		CommandLineParser parser = new PosixParser();
    Options opts = new Options();
    for ( OptionPack pack : this ) {
      for ( Object obj : pack.getOptions() ) {
        Option opt = (Option) obj;
        opts.addOption(opt);
      }
    }
		CommandLine cmd = parser.parse(opts, args, true);
    for ( OptionPack pack : this ) {
      pack.extract(cmd);
    }
    return cmd;
  }
  public void display(String header) {
    display(header,System.out);
  }
  public void display(String header, PrintStream out) {
    out.println(header);
    for ( OptionPack pack : this ) {
      pack.display(out);
    }
  }
  
  public void printHelp(String header) {
    HelpFormatter formatter = new HelpFormatter();
    System.out.println(header);
    System.out.println();
    formatter.setSyntaxPrefix("Option pack ");
    for ( OptionPack pack : this ) {
      formatter.printHelp( pack.getClass().getSimpleName(), pack );
    }
  }
}
