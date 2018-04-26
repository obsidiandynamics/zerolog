package com.obsidiandynamics.zerolog.sample;

import java.io.*;
import java.util.*;

import com.obsidiandynamics.zerolog.*;
import com.obsidiandynamics.zerolog.MockLogTarget.*;

public final class MockingSample {
  public static void doSomeMocking() {
    // sink for our mock logs
    final MockLogTarget target = new MockLogTarget();
    
    // pipe logs to a mock target
    final Zlg zlg = target.logger();
    
    // do some logging...
    zlg.t("Pi is %.2f", z -> z.arg(Math.PI).tag("math"));
    zlg.d("Euler's number is %.2f", z -> z.arg(Math.E).tag("math"));
    zlg.c("Avogadro constant is %.3e", z -> z.arg(6.02214086e23).tag("chemistry"));
    zlg.w("An I/O error has occurred", z -> z.threw(new FileNotFoundException()));
    
    // find entries tagged with 'math'
    final List<LogEntry> math = target.entries().tagged("math").list();
    System.out.println(math);
    
    // find entries at or above debug
    final List<LogEntry> debugAndAbove = target.entries().forLevelAndAbove(LogLevel.DEBUG).list();
    System.out.println(debugAndAbove);
    
    // find entries containing an IOException (or subclass thereof)
    final List<LogEntry> withException = target.entries().withThrowableType(IOException.class).list();
    System.out.println(withException);
    
    // count number of entries containing the substring 'is'
    System.out.println("Entries containing 'is': " + target.entries().containing("is").count());
    
    // assert that only one entry contained an exception
    target.entries().withThrowable().assertCount(1);
    
    // of all the tagged entries, assert that at most two weren't tagged 'chemistry'
    target.entries().tagged().not().tagged("chemistry").assertCountAtMost(2);
  }
  
  public static void main(String[] args) {
    doSomeMocking();
  }
}
