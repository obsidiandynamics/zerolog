<img src="https://raw.githubusercontent.com/wiki/obsidiandynamics/zerolog/images/zerolog-logo.png" width="90px" alt="logo"/> ZeroLog
===
[![Download](https://api.bintray.com/packages/obsidiandynamics/zerolog/zerolog-core/images/download.svg) ](https://bintray.com/obsidiandynamics/zerolog/zerolog-core/_latestVersion)
[![Build](https://travis-ci.org/obsidiandynamics/zerolog.svg?branch=master) ](https://travis-ci.org/obsidiandynamics/zerolog#)
[![codecov](https://codecov.io/gh/obsidiandynamics/zerolog/branch/master/graph/badge.svg)](https://codecov.io/gh/obsidiandynamics/zerolog)
===
Low-overhead logging façade for performance-sensitive applications.

# What is ZeroLog?
ZeroLog (abbreviated to _Zlg_) is a logging façade with two fundamental design objectives:

1. **Ultra-low overhead for suppressed logging.** In other words, the cost of calling a log method when logging for that level has been disabled is negligible.
2. **Uncompromised code coverage.** Suppression of logging should not impact statement and branch coverage metrics. A log entry is a statement like any other.

Collectively, these objectives make Zlg suitable for use in ultra-high performance, low-latency applications and in high-assurance environments.

# How fast is it?
A JMH benchmark conducted on an [i7-4770 Haswell](https://ark.intel.com/products/75122/Intel-Core-i7-4770-Processor-8M-Cache-up-to-3_90-GHz) CPU with logging suppressed compares the per-invocation penalties for Zlg with some of the major loggers.

Logger implementation       |Avg. time (ns)
----------------------------|--------------
JUL (java.util.logging)     |17.526        
Log4j 1.2.17                |14.094        
SLF4J 1.7.25 w/ Log4j 1.2.17|20.583        
TinyLog 1.3.4               |17.083        
ZeroLog                     |0.704

To replicate this benchmark on your machine, run `./gradlew launch -Dlauncher.class=AllBenchmarks`.

# Getting Started
## Dependencies
Gradle builds are hosted on JCenter. Just add the following snippet to your build file. Replace the version placeholder `x.y.z` in the snippet with the version shown on the Download badge at the top of this README.

```groovy
compile "com.obsidiandynamics.zerolog:zerolog-core:x.y.z"
compile "com.obsidiandynamics.zerolog:<binding>:x.y.z"
```

You need the `zerolog-core` module and, typically, a binding. The sole currently supported Zlg binding is `zerolog-slf4j17`, which should work with any logger that features an SLF4J 1.7.x binding. (This covers all major loggers.) For example, to use Zlg with Log4j 1.2.17, add the following to your `build.gradle` (replacing `x.y.z` as appropriate).

```groovy
compile "com.obsidiandynamics.zerolog:zerolog-core:x.y.z"
compile "com.obsidiandynamics.zerolog:zerolog-slf4j17:x.y.z"
runtime "org.slf4j:slf4j-api:1.7.25"
runtime "org.slf4j:slf4j-log4j12:1.7.25"
runtime "log4j:log4j:1.2.17"
```

**Note:** `zerolog-slf4j17` doesn't declare a specific `slf4j-api` version Maven dependency, allowing you to nominate _any_ binary-compatible SLF4J API in your project. The upshot is that you have to explicitly include the `slf4j-api` version in your build file. This can be relegated to the `runtime` configuration (unless you need to use SLF4J directly in your application, alongside Zlg).

## Logging
Getting a logger instance isn't too different from SLF4J. Typically, a named logger instance is first obtained from a factory and subsequently assigned to either an instance or a static field, as shown below.

```java
public final class SysOutLoggingSample {
  private static final Zlg zlg = Zlg.forClass(SysOutLoggingSample.class).get();
  
  public static void open(String address, int port, double timeoutSeconds) {
    zlg.i("Connecting to %s:%d [timeout: %.1f sec]").arg(address).arg(port).arg(timeoutSeconds).log();
    try {
      openSocket(address, port, timeoutSeconds);
    } catch (IOException e) {
      zlg.w("Error connecting to %s:%d").arg(address).arg(port).tag("I/O").threw(e).log();
    }
  }
}
```

Some important things to note:

* A logger is a `Zlg` instance, created for a specific class (`forClass()`) or an arbitrary name (`forName()`). By convention, we name the field `zlg`.
* Logging is invoked via a fluent call chain, starting with the log level (abbreviated to the first letter) specifying a mandatory format string, any optional arguments (primitives or object types), an optional tag, and an optional exception.
* Each chain _must_ end with a `log()` for the log entry to be printed.
* The format string is printf-style, unlike most other loggers that use the `{}` (stash) notation.

# Tags


# Log levels
Zlg log levels are reasonably well-aligned with SLF4J (and most other loggers). Zlg introduces a new log level — `LogLevel.CONF` — logically situated between `DEBUG` and `INFO`. Loosely borrowed from JUL (`java.util.logging`), `CONF` is intended for logging initialisation and configuration parameters, useful when offering a variety of configuration options to the user.

**Note:** `CONF` is canonically mapped to `INFO` in those loggers that don't support `CONF` directly.

The built-in log levels are, from lowest to highest: `TRACE`, `DEBUG`, `CONF`, `INFO`, `WARN`, `ERROR` and `OFF`. 

**Note:** `OFF` is not a legal log level, insofar as it cannot be used to output a log entry from the application code; it's use purely for configuration — being the highest of all levels, **`OFF` is used to disable logging altogether**.

# Configuration
## Bindings
Being a façade, Zlg delegates all log calls to an actual logger — an implementation of `LogService`. By default, Zlg comes pre-packaged with a very basic 'failsafe' `SysOutLogService` that prints entries to `System.out` in a fixed format. Example below.

```
21:23:16.814 INF [main]: Connecting to github.com:80 [timeout: 30.0 sec]
21:23:16.818 WRN [main] [I/O]: Error connecting to github.com:80
```

Zlg detects installed bindings using Java's [SPI](https://docs.oracle.com/javase/tutorial/ext/basics/spi.html) plugin mechanism. By simply including a binding on the classpath, Zlg will switch over to the new binding by default.

## Baseline configuration with `zlg.properties`
Like SLF4J, Zlg is largely hands-off when it comes to logger configuration management, leaving the configuration specifics to the bound logger implementation. Configuration Log4j 1.2, for example, would be done through `log4j.properties` — Zlg remains completely agnostic of this.

Zlg supports a _baseline_ configuration, by reading an optional `zlg.properties` file from the classpath. A baseline configuration comprises a list of optional properties. For example, it specifies the base log level (below which all logging is disabled) and can override the default binding. The following is an example of `zlg.properties`.

```
zlg.base.level=CONF
zlg.log.service=com.obsidiandynamics.zerolog.SysOutLogService
```

The `zlg.base.level` property specifies the minimum enabled log level (irrespective of what may be allowed by the bound logger). The default value is `CONF`, meaning that **unless the baseline is altered, `TRACE` and `DEBUG` entries will be ignored**. The choice of `CONF` as the default level is closest to a typical production configuration.

While `zlg.properties` is optional, it is strongly recommended that `zlg.properties` is present during development with a single `zlg.base.level` entry. Practically, this could be a file in `src/main/resources` or `src/test/resources` with an appropriate VCS ignore rule, so that changes to `zlg.properties` aren't committed back to your VCS. This allows developers to enable/disable debugging for local development without stuffing around with the logger configuration or accidentally committing back the change. Excluding it from VCS means that it will be absent from your CI build or a clean checkout build; this is normally quite acceptable, as the default configuration is geared towards production use.

## Changing the location of `zlg.properties`
The default location of `zlg.properties` can be overridden by setting the `zlg.default.config.uri` system property. The default URI is `cp://zlg.properties`, `cp://` denoting 'classpath'. Alternatively, a file system location can be specified with the `file://` scheme.

When overriding the default file location, ideally the `zlg.default.config.uri` property is passed in as a `-D...` JVM argument, ensuring that the logging subsystem is bootstrapped correctly before initial use.

## In-line configuration



