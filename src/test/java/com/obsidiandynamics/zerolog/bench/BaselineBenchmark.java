package com.obsidiandynamics.zerolog.bench;

public final class BaselineBenchmark extends AbstractBenchmark {
  @Override
  protected void cycle(float f, double d, int i, long l) {}
  
  public static void main(String[] args) {
    run(BaselineBenchmark.class);
  }
}
