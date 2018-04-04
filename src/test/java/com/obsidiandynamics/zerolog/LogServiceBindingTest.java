package com.obsidiandynamics.zerolog;

import static org.junit.Assert.*;

import org.junit.*;

public final class LogServiceBindingTest {
  @Test
  public void testComparator() {
    final LogServiceBinding b0 = new LogServiceBinding() {
      @Override
      public byte getPriority() {
        return 0;
      }
      
      @Override
      public LogService getLogService() {
        return null;
      }
    };
    
    final LogServiceBinding b1 = new LogServiceBinding() {
      @Override
      public byte getPriority() {
        return 1;
      }
      
      @Override
      public LogService getLogService() {
        return null;
      }
    };
    
    assertEquals(1, LogServiceBinding.byPriorityDecreasing(b0, b1));
  }
}
