package com.obsidiandynamics.zerolog.sample;

import java.lang.invoke.*;
import java.util.*;

import com.obsidiandynamics.zerolog.*;

public final class LazySample {
  private static final Zlg zlg = Zlg.forClass(MethodHandles.lookup().lookupClass()).get();
  
  public static void logWithSupplier() {
    final List<Integer> numbers = Arrays.asList(0, 1, 2, 3);
    final int index = 4;
    
    if (index >= numbers.size()) {
      zlg.t("index must be 0 < index < %d").arg(numbers::size).log();
    }
  }
  
  public static void main(String[] args) {
    logWithSupplier();
  }
}
