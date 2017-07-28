package utilities;

import java.util.Comparator;
import static java.lang.Math.*;
import static utilities.SpecialFunctions.*;

public final class DoubleComparator implements Comparator<Double> {
  private final static DoubleComparator singleton;
  static {
    singleton = new DoubleComparator();
  }

  public static DoubleComparator getInstance() {
    return singleton;
  }

  public static boolean isEqual(Double aa, Double bb) {
    return singleton.compare(aa, bb) == 0;
  }

  private double prec;

  private DoubleComparator() {
    prec = sqrt(getMachEps());
  }

  public static boolean isLessThan(Double aa, Double bb) {
    return singleton.compare(aa, bb) == -1;
  }

  @Override public int compare(Double aa, Double bb) {
    double norm = max(abs(aa), abs(bb));
    if (norm < prec || abs(aa-bb) < norm*prec) {
      return 0;
    } else if (aa < bb) {
      return -1;
    } else {
      return 1;
    }
  }
  public static int Compare(Double aa, Double bb) {
    return singleton.compare(aa, bb);
  }
}
