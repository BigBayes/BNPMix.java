/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mcmc.collectors;

import mcmc.Sampleable;

/**
 *
 * @author ywteh
 */
public class SystemOutPrinter extends Filer {
  public SystemOutPrinter(Sampleable client_, String[] properties_, Object[] args_) {
    super(client_,System.out,properties_,args_);
  }
  public SystemOutPrinter(Sampleable client_, String[] properties_) {
    super(client_,System.out,properties_,null);
  }

}
