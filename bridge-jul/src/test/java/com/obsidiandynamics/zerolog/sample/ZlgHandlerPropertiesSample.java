package com.obsidiandynamics.zerolog.sample;

import java.util.logging.*;

public final class ZlgHandlerPropertiesSample {
  public static void main(String[] args) {
    // configure ZlgHandler through a properties file
    System.setProperty("java.util.logging.config.file", "src/test/resources/jul.properties");
    
    final Logger logger = Logger.getLogger(ZlgHandlerPropertiesSample.class.getName());
    
    // logging will now end up with Zlg
    logger.log(Level.INFO, "Pi is {0} ≈ {1}/{2}", new Object[] {Math.PI, 22, 7});
  }
}
