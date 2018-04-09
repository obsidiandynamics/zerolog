<img src="https://raw.githubusercontent.com/wiki/obsidiandynamics/zerolog/images/zerolog-logo.png" width="90px" alt="logo"/> ZeroLog
===
[![Download](https://api.bintray.com/packages/obsidiandynamics/zerolog/zerolog-core/images/download.svg) ](https://bintray.com/obsidiandynamics/zerolog/zerolog-core/_latestVersion)
[![Build](https://travis-ci.org/obsidiandynamics/zerolog.svg?branch=master) ](https://travis-ci.org/obsidiandynamics/zerolog#)
[![codecov](https://codecov.io/gh/obsidiandynamics/zerolog/branch/master/graph/badge.svg)](https://codecov.io/gh/obsidiandynamics/zerolog)
===
Low-overhead logging façade for performance-sensitive applications.

# What is ZeroLog?
ZeroLog is a logging façade with two design goals:

1. Ultra-low overhead when the logger is suppressed. In other words, the cost of calling a log method when logging for that level has been disabled is negligible.
2. 

that is suitable for use in ultra-high performance, low-latency applications


# How fast is it?
A JMH benchmark conducted on an [i7-870 Lynnfield](https://ark.intel.com/products/41315/Intel-Core-i7-870-Processor-8M-Cache-2_93-GHz) CPU with logging suppressed reveals the per-invocation penalties for some of the major loggers.

Logger implementation       |Avg. time (ns)
----------------------------|--------------
JUL (java.util.logging)     |17.526        
Log4j 1.2.17                |14.094        
SLF4J 1.7.25 w/ Log4j 1.2.17|20.583        
TinyLog 1.3.4               |17.083        
ZeroLog                     |1.157
