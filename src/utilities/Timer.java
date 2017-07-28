/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 *
 * @author ywteh
 */
public class Timer {
  private static ArrayList<Timer> timers = new ArrayList<Timer>();
  private static DecimalFormat format = new DecimalFormat("0.0000");

  public static void reportStatus() {
    for (Timer timer : timers) {
      System.out.println(timer);
    }
  }
  public static Timer make(String name) {
    Timer timer = new Timer(name);
    timers.add(timer);
    return timer;
  }


  String name;
  long tics,prevtic;

  private Timer(String name) {
    this.name = name;
    this.tics = 0;
    this.prevtic = -1;
  }
  public void tic() {
    if (prevtic>=0) throw new Error("Toc wasn't called after previous tic");
    prevtic = System.currentTimeMillis();
  }
  public void toc() {
    if (prevtic<0) throw new Error("Tic wasn't called before toc");
    tics += System.currentTimeMillis() - prevtic;
    prevtic = -1;
  }
  public String toString() {
    double time = (((double)tics)/1000.0);
    return this.getClass().getSimpleName()+" "+name+": "+format.format(time);
  }
}
