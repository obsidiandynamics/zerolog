package com.obsidiandynamics.zerolog.bench;

import java.util.*;
import java.util.stream.*;

public final class AllBenchmarks {
  public static void main(String[] args) {
    final double baseline = AbstractBenchmark.run(BaselineBenchmark.class).getScore();
    System.out.format("Baseline: %,.3f ns\n", baseline);
    
    final Map<String, Class<? extends AbstractBenchmark>> benchmarks = new TreeMap<>();
    benchmarks.put("JUL (java.util.logging)", JulBenchmark.class);
    benchmarks.put("Log4j 1.2.17", Log4jBenchmark.class);
    benchmarks.put("SLF4J 1.7.25 w/ Log4j 1.2.17", Slf4jBenchmark.class);
    benchmarks.put("TinyLog 1.3.4", TinyLogBenchmark.class);
    benchmarks.put("ZeroLog", ZlgBenchmark.class);
    
    final int longestName = benchmarks.keySet().stream().map(key -> key.length()).max(Integer::compare).get().intValue();
    final String nameHeader = "Logger implementation";
    final String timeHeader = "Avg. time (ns)";
    
    final StringBuilder out = new StringBuilder()
    .append(String.format("%-" + longestName + "s", nameHeader))
    .append("|")
    .append(timeHeader)
    .append("\n");
    
    IntStream.range(0, longestName).forEach(i -> out.append("-"));
    out.append("|");
    IntStream.range(0, timeHeader.length()).forEach(i -> out.append("-"));
    out.append("\n");
    
    for (Map.Entry<String, Class<? extends AbstractBenchmark>> entry : benchmarks.entrySet()) {
      final double score = AbstractBenchmark.run(entry.getValue()).getScore() - baseline;
      out
      .append(String.format("%-" + longestName + "s", entry.getKey()))
      .append("|")
      .append(String.format("%-" + timeHeader.length() + "s", String.format("%,.3f", score * 1_000_000_000d)))
      .append("\n");
    }
    
    System.out.println("\n\n" + out);
  }
}
