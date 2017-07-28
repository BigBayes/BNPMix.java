package qmix;

import java.io.IOException;
import nrmi.QGGPLogNormalOptionPack;
import org.apache.commons.cli.ParseException;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ywteh
 */
public class qmixlognormal extends qmix {
  
  public qmixlognormal() {
    super();
    qrmpack = new QGGPLogNormalOptionPack();
  }
  
	public static void main(String[] args) throws ParseException, IOException, Error, Exception {
    qmixlognormal qq = new qmixlognormal();
    qq.run(args);
  }
}
