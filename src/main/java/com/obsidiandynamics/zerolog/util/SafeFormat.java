package com.obsidiandynamics.zerolog.util;

import java.util.*;

public final class SafeFormat {
  private SafeFormat() {}
  
  public static String format(String format, int argc, Object[] argv) {
    try {
      return String.format(format, argv);
    } catch (Throwable e) {
      return "WARNING - could not format '" + format + "' with args " + Arrays.asList(copyArgs(argc, argv)) + ":\n" + e;
    }
  }
  
  public static Object[] copyArgs(int argc, Object[] argv) {
    final Object[] args = new Object[argc];
    System.arraycopy(argv, 0, args, 0, argc);
    return args;
  }
}
