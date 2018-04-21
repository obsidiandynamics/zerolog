package com.obsidiandynamics.zerolog.sample;

import java.lang.invoke.*;

import org.slf4j.*;

import com.obsidiandynamics.zerolog.*;

public final class Slf4jWrapperSample {
  public static void main(String[] args) {
    final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    final Zlg zlg = Zlg.forName("wrapper")
        .withConfigService(new LogConfig().withLogService(Slf4jWrapper.of(logger))).get();
    
    zlg.i("Logging to a wrapped SLF4J instance");
  }
}
