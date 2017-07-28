/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mcmc;

/**
 *
 * @author ywteh
 */
public interface Collectable {
	/**
	 * Collects some statistics based on the current state of the sampler.
	 *
	 * @param property The name of the statistic to be collected.
	 * @return The statistic.
	 */
	public Object get(String property);

	/**
	 * Collects some statistics based on the current state of the sampler.
	 *
	 * @param property The name of the statistic to be collected.
	 * @param arg An argument denoting the statistic to be collected.
	 * @return The statistic.
	 */
	public Object get(String property, Object arg);

}
