package com.obsidiandynamics.zerolog;

import java.util.function.*;

/**
 *  Utilities for lazily-invoked argument transformations.
 */
public final class Args {
  private Args() {}
  
  /**
   *  Forms a supplier of the given object reference.
   *  
   *  @param <T> Value type.
   *  @param value The value to supply.
   *  @return A {@link Supplier} of the given value.
   */
  public static <T> Supplier<T> ref(T value) {
    return () -> value;
  }
  
  /**
   *  Maps the value returned by the given supplier to any type via the given transform.
   *  
   *  @param <T> Value type.
   *  @param supplier The source of the value to transform.
   *  @param transform The transform function.
   *  @return A {@link Supplier} that will perform the transform when invoked.
   */
  public static <T> Supplier<?> map(Supplier<? extends T> supplier, Function<? super T, ?> transform) {
    return () -> transform.apply(supplier.get());
  }
}
