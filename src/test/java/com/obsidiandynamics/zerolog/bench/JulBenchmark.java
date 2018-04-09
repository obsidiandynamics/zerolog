package com.obsidiandynamics.zerolog.bench;

import static org.junit.Assert.*;

import java.util.logging.*;

public final class JulBenchmark extends AbstractBenchmark {
  private Logger logger;
  
  @Override
  public void setup() {
    logger = Logger.getLogger(JulBenchmark.class.getName());
    logger.setLevel(Level.INFO);
    assertFalse(logger.isLoggable(Level.FINEST));
  }

  @Override
  protected void cycle(float f, double d, int i, long l) {
    logger.log(Level.FINEST, "float: {0}, double: {1}, int: {2}, long: {3}", new Object[] {f, d, i, l});
  }
  
  public static void main(String[] args) {
    run(JulBenchmark.class);
  }
}
