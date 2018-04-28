package com.obsidiandynamics.zerolog.sample;

import java.util.logging.*;

import com.hazelcast.core.*;
import com.hazelcast.logging.*;
import com.hazelcast.test.*;
import com.obsidiandynamics.zerolog.*;

public final class HazelcastZlgBridgeSample {
  public static void main(String[] args) {
    // declutter the logs
    java.util.logging.Logger.getLogger("").setLevel(Level.WARNING);
    final TestHazelcastInstanceFactory instanceFactory = new TestHazelcastInstanceFactory();
    
    // log without the bridge -- goes to JUL be default
    {
      final HazelcastInstance instanceBefore = instanceFactory.newHazelcastInstance();
      final ILogger loggerBefore = instanceBefore.getLoggingService().getLogger(HazelcastZlgBridgeSample.class);
      loggerBefore.warning("Logs to JUL by default");
      instanceBefore.shutdown();
    }
    
    HazelcastZlgBridge.install();

    // log with the bridge -- will end up in Zlg
    {
      final HazelcastInstance instance = instanceFactory.newHazelcastInstance();
      final ILogger logger = instance.getLoggingService().getLogger(HazelcastZlgBridgeSample.class);
      logger.warning("Hello Hazelcast logging");
      instance.shutdown();
    }

    HazelcastZlgBridge.uninstall();
    
    // after resetting, logs will go back to JUL
    {
      final HazelcastInstance instanceAfter = instanceFactory.newHazelcastInstance();
      final ILogger loggerAfter = instanceAfter.getLoggingService().getLogger(HazelcastZlgBridgeSample.class);
      loggerAfter.warning("Restored logging to JUL");
      instanceAfter.shutdown();
    }
  }
}
