package com.obsidiandynamics.zerolog.util;

public final class CallingClass extends SecurityManager {
  private static final CallingClass instance = new CallingClass();
  
  private CallingClass() {}
  
  public static Class<?> forDepth(int depth) {
    return instance.getClassContext()[depth];
  }
}
