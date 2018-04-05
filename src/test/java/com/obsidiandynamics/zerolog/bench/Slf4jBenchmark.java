package com.obsidiandynamics.zerolog.bench;

import static org.junit.Assert.*;

import org.slf4j.*;

import com.obsidiandynamics.zerolog.off.*;

public final class Slf4jBenchmark extends AbstractBenchmark {
  private Logger logger;
  
  @Override
  public void setup() {
    logger = LoggerFactory.getLogger(AbstractOffVolumeTest.class);
    assertFalse(logger.isTraceEnabled());
  }

  @Override
  protected void cycle(float f, double d, int i, long l) {
    logger.trace("float: {}, double: {}, int: {}, long: {}", f, d, i, l);
  }
  
  public static void main(String[] args) {
    run(Slf4jBenchmark.class);
  }
}
