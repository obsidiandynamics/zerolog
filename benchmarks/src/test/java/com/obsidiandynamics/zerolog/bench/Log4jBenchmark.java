package com.obsidiandynamics.zerolog.bench;

import static org.junit.Assert.*;

import org.apache.log4j.*;

public final class Log4jBenchmark extends AbstractBenchmark {
  private Logger logger;
  
  @Override
  public void setup() {
    logger = Logger.getLogger(Log4jBenchmark.class);
    assertFalse(logger.isTraceEnabled());
  }

  @Override
  protected void cycle(float f, double d, int i, long l) {
    LogMF.trace(logger, "float: {}, double: {}, int: {}, long: {}", f, d, i, l);
  }
  
  public static void main(String[] args) {
    run(Log4jBenchmark.class);
  }
}
