package com.obsidiandynamics.zerolog.bench;

import java.util.function.*;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.options.*;

import com.obsidiandynamics.dyno.*;
import com.obsidiandynamics.props.*;

public abstract class AbstractBenchmark implements BenchmarkTarget {
  @Override
  public final void cycle(Abyss abyss) {
    final long randomLong = 2718281828459045235L;
    final double randomDouble = 2.7182818284590452354d;
    final float randomFloat = 2.7182818284590f;
    final int randomInt = 271828182;
    cycle(randomFloat, randomDouble, randomInt, randomLong);
  }

  protected abstract void cycle(float f, double d, int i, long l);

  private static final String JVM_ARGS = 
      "-XX:+TieredCompilation -XX:+UseNUMA -XX:+UseCondCardMark -Xms2G -Xmx2G -Xss1M " +
      "-XX:+UseG1GC -XX:MaxGCPauseMillis=10000 -XX:InitiatingHeapOccupancyPercent=99 -verbose:gc";

  private enum Profile {
    LITE
    (() -> new Dyno()
     .withBenchmarkTime(10)
     .withDriver(new JmhDriver(opts -> opts
                               .mode(Mode.AverageTime)
                               .forks(0)
                               .measurementIterations(1)))
     .withWarmupFraction(0)),

    STANDARD
    (() -> new Dyno()
     .withBenchmarkTime(30_000)
     .withDriver(new JmhDriver(opts -> opts
                               .mode(Mode.AverageTime)
                               .forks(3)
                               .verbosity(VerboseMode.EXTRA)
                               .jvmArgsAppend(JVM_ARGS.split(" "))
                               .measurementIterations(1)))
     .withWarmupFraction(0.25)),

    JUMBO
    (() -> new Dyno()
     .withBenchmarkTime(60_000)
     .withDriver(new JmhDriver(opts -> opts
                               .mode(Mode.AverageTime)
                               .forks(5)
                               .verbosity(VerboseMode.EXTRA)
                               .jvmArgsAppend(JVM_ARGS.split(" "))
                               .measurementIterations(1)))
     .withWarmupFraction(0.25));

    final Supplier<Dyno> dynoSupplier;
    Profile(Supplier<Dyno> dynoSupplier) { this.dynoSupplier = dynoSupplier; }
  }

  private static final Profile defaultProfile = Props.get("zlg.bench.profile", Profile::valueOf, Profile.STANDARD);

  static BenchmarkResult run(Class<? extends AbstractBenchmark> target) {
    return defaultProfile.dynoSupplier.get()
        .withTarget(target)
        .withOutput(AbstractBenchmark::printResult)
        .run();
  }

  private static void printResult(BenchmarkResult result) {
    System.out.format("Average call time: %,.3f ns\n", toNanos(result));
  }

  static double toNanos(BenchmarkResult result) {
    return result.getScore() * 1_000_000_000d;
  }
}
