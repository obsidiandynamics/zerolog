package com.obsidiandynamics.zerolog.sample;

import java.io.*;
import java.util.*;

import com.obsidiandynamics.zerolog.*;
import com.obsidiandynamics.zerolog.MockLogTarget.*;

public final class MockingSample {
  public static void doSomeMocking() {
    final MockLogTarget target = new MockLogTarget();
    final Zlg zlg = target.logger();
    
    // do some logging...
    zlg.t("Pi is %.2f").arg(Math.PI).tag("math").log();
    zlg.d("Euler's number is %.2f").arg(Math.E).tag("math").log();
    zlg.c("Avogadro constant is %.3e").arg(6.02214086e23).tag("chemistry").log();
    zlg.w("An I/O error has occurred").threw(new FileNotFoundException()).log();
    
    // find entries tagged with 'math'
    final List<Entry> math = target.entries().tagged("math").list();
    System.out.println(math);
    
    // find entries at or above debug
    final List<Entry> debugAndAbove = target.entries().forLevelAndAbove(LogLevel.DEBUG).list();
    System.out.println(debugAndAbove);
    
    // find entries containing an IOException (or subclass thereof)
    final List<Entry> withException = target.entries().withException(IOException.class).list();
    System.out.println(withException);
  }
  
  public static void main(String[] args) {
    doSomeMocking();
  }
}
