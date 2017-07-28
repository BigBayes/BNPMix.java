/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package xfamily;

import java.util.Collection;

/**
 *
 * @author ywteh
 */
public interface XPrior<Parameter> extends XFamily<Parameter> {

  public void sample(Collection<? extends XHierarchy> data);

}
