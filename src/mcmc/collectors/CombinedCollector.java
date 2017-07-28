/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mcmc.collectors;
import java.util.List;

public class CombinedCollector implements Collector {
    List<Collector> collectors;

    public CombinedCollector(List<Collector> collectors) {
        this.collectors = collectors;
    }

    @Override public void collect() {
        for (Collector collector : collectors)
            collector.collect();
    }
    @Override public void finish() {
        for (Collector collector : collectors)
            collector.finish();
    }
    @Override public void flush() {
        for (Collector collector : collectors)
            collector.flush();
    }
}
