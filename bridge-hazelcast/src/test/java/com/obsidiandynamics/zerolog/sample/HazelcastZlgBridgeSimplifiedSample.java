package com.obsidiandynamics.zerolog.sample;

import com.hazelcast.core.*;
import com.hazelcast.logging.*;
import com.hazelcast.test.*;
import com.obsidiandynamics.zerolog.*;

public final class HazelcastZlgBridgeSimplifiedSample {
  public static void main(String[] args) {
    // install the bridge; all logs will now be pumped to Zlg
    HazelcastZlgBridge.install();

    // create a Hazelcast instance and get a logger
    final HazelcastInstance instance = new TestHazelcastInstanceFactory().newHazelcastInstance();
    final ILogger logger = instance.getLoggingService().getLogger(HazelcastZlgBridgeSimplifiedSample.class);
    
    logger.warning("This is a drill, this is a drill");

    // clean up
    instance.shutdown();
    HazelcastZlgBridge.uninstall();
  }
}
