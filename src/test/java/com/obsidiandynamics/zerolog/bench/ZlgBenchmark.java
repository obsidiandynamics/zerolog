package com.obsidiandynamics.zerolog.bench;

import static org.junit.Assert.*;

import org.openjdk.jmh.annotations.*;

import com.obsidiandynamics.dyno.*;
import com.obsidiandynamics.zerolog.*;
import com.obsidiandynamics.zerolog.off.*;

import squidpony.squidmath.*;

public final class ZlgBenchmark implements BenchmarkTarget {
  private Zlg zlg;
  
  private final RandomnessSource random = new XoRoRNG();
  
  @Override
  public void setup() {
    zlg = Zlg.forClass(AbstractOffVolumeTest.class)
        .withConfigService(new LogConfig().withBaseLevel(LogLevel.CONF).get())
        .get();
    assertFalse(zlg.isEnabled(LogLevel.TRACE));
    assertTrue(zlg.t("msg").getClass().getSimpleName().equals("NopLevelChain"));
  }

  @Override
  public void cycle(Abyss abyss) {
    final long randomLong = random.nextLong();
    final double randomDouble = RandomFP.toDouble(randomLong);
    final float randomFloat = (float) randomDouble;
    final int randomInt = (int) (Integer.MAX_VALUE * randomFloat);
    zlg.t("float: %f, double: %f, int: %d, long: %d").arg(randomFloat).arg(randomDouble).arg(randomInt).arg(randomLong).log();
    abyss.consume(randomInt);
  }
  
  public static void main(String[] args) {
    new Dyno()
    .withBenchTime(5_000)
    .withTarget(ZlgBenchmark.class)
    .withDriver(new JmhDriver(opts -> opts
                              .mode(Mode.Throughput)
                              .measurementIterations(2)))
    .withWarmupFraction(0.25)
    .withOutput(System.out::println)
    .run();
  }
}
