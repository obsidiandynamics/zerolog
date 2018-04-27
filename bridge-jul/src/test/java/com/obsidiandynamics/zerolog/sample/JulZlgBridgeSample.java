package com.obsidiandynamics.zerolog.sample;

import java.util.logging.*;

import com.obsidiandynamics.zerolog.*;

public final class JulZlgBridgeSample {
  public static void main(String[] args) {
    final Logger logger = Logger.getLogger(JulZlgBridgeSample.class.getName());
    
    // log without the bridge... will go to System.err
    logger.log(Level.INFO, "Hello");
    
    final Zlg zlg = Zlg.forDeclaringClass().get();
    
    // stash existing handlers and install a Zlg handler
    JulZlgBridge.stashAllHandlers();
    JulZlgBridge.install(zlg);
    
    // logging will now end up with Zlg
    logger.log(Level.INFO, "Pi is {0} ≈ {1}/{2}", new Object[] {Math.PI, 22, 7});
    
    // uninstall Zlg and restore the previously stashed loggers
    JulZlgBridge.uninstall();
    JulZlgBridge.unstashAllHandlers();

    // logging is now resumed to System.err
    logger.log(Level.INFO, "Goodbye");
  }
}
