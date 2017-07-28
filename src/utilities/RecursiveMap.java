/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;
import java.util.*;

/**
 *
 * @author ywteh
 */
public class RecursiveMap<E> {
  HashMap<E,E> map;

  public RecursiveMap() {
    map = new HashMap<E,E>();
  }
  public void clear() {
    map.clear();
  }

  public void put(E entry, E newentry) {
    map.put(entry,newentry);
  }

  public E get(E entry) {
    if (!map.containsKey(entry)) return entry;
    E newentry = get(map.get(entry));
    map.put(entry,newentry);
    return newentry;
  }

  @Override public final String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append(getClass().getSimpleName());
    buf.append(":\n");
    for ( E cur : map.keySet() ) {
      buf.append(cur);
      buf.append("  -->  ");
      buf.append(map.get(cur));
      buf.append("\n");
    }
    return buf.toString();
  }

  public void display() {
    System.out.print(this);
  }

}
