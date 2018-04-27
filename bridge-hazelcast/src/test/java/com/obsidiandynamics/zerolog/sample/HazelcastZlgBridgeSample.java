package com.obsidiandynamics.zerolog.sample;

import com.hazelcast.core.*;
import com.hazelcast.logging.*;
import com.hazelcast.test.*;
import com.obsidiandynamics.zerolog.*;

public final class HazelcastZlgBridgeSample {
  public static void main(String[] args) {
    HazelcastZlgBridge.install();
    
    final HazelcastInstance instance = new TestHazelcastInstanceFactory().newHazelcastInstance();
    final ILogger logger = instance.getLoggingService().getLogger(HazelcastZlgBridgeSample.class);
    
    logger.info("Hello Hazelcast logging");
  }
}
