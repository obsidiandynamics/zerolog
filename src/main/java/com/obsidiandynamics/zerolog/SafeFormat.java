package com.obsidiandynamics.zerolog;

import java.util.*;

public final class SafeFormat {
  private SafeFormat() {}
  
  public static String format(String format, int argc, Object[] argv) {
    try {
      return String.format(format, argv);
    } catch (Throwable e) {
      final Object[] args = new Object[argc];
      System.arraycopy(argv, 0, args, 0, argc);
      return "WARNING - could not format '" + format + "' with args " + Arrays.asList(args) + ":\n" + e;
    }
  }
}
