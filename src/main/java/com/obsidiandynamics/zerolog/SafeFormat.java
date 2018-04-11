package com.obsidiandynamics.zerolog;

import java.util.*;

public final class SafeFormat {
  private SafeFormat() {}
  
  public static String format(String format, Object... args) {
    try {
      return String.format(format, args);
    } catch (Throwable e) {
      return "WARNING - could not format '" + format + "' with args " + Arrays.asList(args) + ": " + e;
    }
  }
}
