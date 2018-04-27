package com.obsidiandynamics.zerolog.bench;

import static org.junit.Assert.*;

import java.util.logging.*;

public final class JulLambdaBenchmark extends AbstractBenchmark {
  private Logger logger;
  
  @Override
  public void setup() {
    logger = Logger.getLogger(JulLambdaBenchmark.class.getName());
    logger.setLevel(Level.INFO);
    assertFalse(logger.isLoggable(Level.FINEST));
  }

  @Override
  protected void cycle(float f, double d, int i, long l) {
    logger.log(Level.FINEST, () -> String.format("float: %f, double: %f, int: %d, long: %d", f, d, i, l));
  }
  
  public static void main(String[] args) {
    run(JulLambdaBenchmark.class);
  }
}
