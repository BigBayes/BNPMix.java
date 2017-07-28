/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package crm;

import utilities.Generator;


/**
 * A Levy process, with Levy intensity rho(x) over [0,infty) satisfying
 * int rho(x) dx = infty
 * int (1-e^-x) rho(x) dx < infty
 * @author ywteh
 */
public interface LevyProcess {

  /**
   * Logarithm of the LevyProcess density.
   * @param x > 0 Jump.
   * @return log rho(x)
   */
  double logLevy(double x);

  /**
   * Laplace transform of LevyProcess intensity.
   * @param u > 0 Parameter.
   * @return log E[e^{-u*T}] = log int_0^infty (1-e^{-u*x}) rho(x) dx
   */
  double laplace(double u);

  /**
   * Mean of the total jumps under a LevyProcess process.
   * @return E[T] = int_0^infty x rho(x) dx
   */
  double meanTotalJump();

  /**
   * Integral of a gamma likelihood.
   * @param a > 0 Shape parameter.
   * @param b >= 0 Inverse scale parameter.
   * @return int_0^infty x^{a} e^{-b*x} rho(x) dx
   */
  double gamma(double a, double b);

  /**
   * Log integral of a gamma likelihood.
   * @param a > 0 Shape parameter.
   * @param b >= 0 Inverse scale parameter.
   * @return log int_0^infty x^{a} e^{-b*x} rho(x) dx
   */
  double logGamma(double a, double b);

  /**
   * Mean of the gamma distribution with density proportional to
   * rho(x) times a gamma likelihood x^{a} e^{-b*x}
   * @param a > 0 Shape parameter.
   * @param b >= 0 Inverse scale parameter.
   * @return Drawn sample.
   */
  double meanGamma(double a, double b);

  /**
   * Draws a sample from the jump distribution with density proportional to
   * rho(x) times a gamma likelihood x^{a} e^{-b*x}
   * @param a > 0 Shape parameter.
   * @param b >= 0 Inverse scale parameter.
   * @return Drawn sample.
   */
  double drawGamma(double a, double b);

  /**
   * Draws a sample from the jump distribution with density proportional to
   * rho(x) times a gamma likelihood x^{a} e^{-b*x}
   * @param a > 0 Shape parameter.
   * @param b >= 0 Inverse scale parameter.
   * @return Drawn sample.
   */
  double drawGamma(double a, double b, Generator gen);

  /**
   * Draws a set of jumps from the LevyProcess process, restricting only to masses
   * above a threshold value.
   * The set of masses has a law given by a Poisson process, with rate rho(x).
   * @param threshold > 0 The threshold value.
   * @return Drawn set of jumps, sorted in decreasing order.
   */
  double[] drawJumps(double threshold);

  /**
   * Exponentially tilt the Levy process by multiplying rho(x) by exp(-ux)
   * @param u > 0 Tilting parameter.
   * @return The tilted Levy process.
   */
  LevyProcess expTilt(double u);
}
