package com.obsidiandynamics.zerolog.bench;

import static org.junit.Assert.*;

import org.pmw.tinylog.*;
import org.pmw.tinylog.writers.*;

public final class TinyLogBenchmark extends AbstractBenchmark {
  @Override
  public void setup() {
    Configurator.defaultConfig()
    .writer(new ConsoleWriter())
    .level(Level.INFO)
    .activate();
    assertEquals(Level.INFO, Logger.getLevel());
  }

  @Override
  protected void cycle(float f, double d, int i, long l) {
    Logger.trace("float: {}, double: {}, int: {}, long: {}", f, d, i, l);
  }
  
  public static void main(String[] args) {
    run(TinyLogBenchmark.class);
  }
}
