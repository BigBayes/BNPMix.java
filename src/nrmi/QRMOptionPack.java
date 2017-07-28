/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nrmi;

import org.apache.commons.cli.*;
import utilities.OptionPack;

/**
 *
 * @author ywteh
 */
public abstract class QRMOptionPack extends OptionPack {
  
  public QRMOptionPack() {
    super();
  }
  
  public abstract QGGP getQRM();
}
