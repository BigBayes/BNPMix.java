package utilities;

/**
 * Interface for a factory class.  A factory constructs objects according to
 * molds.
 *
 * @author ywteh
 * @param <X> Type of objects.
 * @param <Y> Type of mold.
 */
public interface Factory<X,Y> {
  /**
   * Constructs an object according to mold y.
   */
	X construct(Y y);

  /**
   * Destructs an object x which was created by this factory according to mold y.
   */
	void destruct(Y y,X x);

}
