<img src="https://raw.githubusercontent.com/wiki/obsidiandynamics/zerolog/images/zerolog-logo.png" width="90px" alt="logo"/> Zerolog
===
Low-overhead logging façade for performance-sensitive applications.

[![Maven release](https://img.shields.io/maven-metadata/v.svg?color=blue&label=maven-central&metadataUrl=https%3A%2F%2Frepo1.maven.org%2Fmaven2%2Fcom%2Fobsidiandynamics%2Fzerolog%2Fzerolog-core%2Fmaven-metadata.xml)](https://mvnrepository.com/artifact/com.obsidiandynamics.zerolog)
[![Total alerts](https://img.shields.io/lgtm/alerts/g/obsidiandynamics/zerolog.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/obsidiandynamics/zerolog/alerts/)
[![Gradle build](https://github.com/obsidiandynamics/zerolog/actions/workflows/master.yml/badge.svg)](https://github.com/obsidiandynamics/zerolog/actions/workflows/master.yml)
[![codecov](https://codecov.io/gh/obsidiandynamics/zerolog/branch/master/graph/badge.svg)](https://codecov.io/gh/obsidiandynamics/zerolog)

# What is Zerolog?
Zerolog (abbreviated to Zlg) is a logging façade with two fundamental design objectives:

1. **Ultra-low overhead for suppressed logging.** In other words, the cost of calling a log method when logging for that level has been disabled is negligible.
2. **Uncompromised code coverage.** Suppression of logging should not impact statement and branch coverage metrics. A log entry is a statement like any other.

Collectively, these goals make Zlg suitable for use in ultra-high performance, low-latency applications, and high-assurance environments.

# How fast is it?
A JMH benchmark conducted on an [i7-4770 Haswell](https://ark.intel.com/products/75122/Intel-Core-i7-4770-Processor-8M-Cache-up-to-3_90-GHz) CPU with logging suppressed compares the per-invocation penalties for Zlg with some of the major loggers. Four primitives are passed to each logger for formatting, which is a fair representation of a typical log entry.

Logger implementation       |Avg. time (ns)
:---------------------------|-------------:
JUL (java.util.logging)     |12.356         
SLF4J 1.7.25 w/ Log4j 1.2.17|12.532        
TinyLog 1.3.4               |11.780        
Zlg                         |0.390  

The run was conducted on CentOS 7, running JDK 10. To replicate, run `./gradlew launch -Dlauncher.class=AllBenchmarks`.

# Getting Started
## Dependencies
Add the following snippet to your build file, replacing `x.y.z` with the version shown on the Download badge at the top of this README.

```groovy
api "com.obsidiandynamics.zerolog:zerolog-core:x.y.z"
api "com.obsidiandynamics.zerolog:<binding>:x.y.z"
```

You need the `zerolog-core` module and, typically, a binding module. The sole currently supported Zlg binding is `zerolog-slf4j17`, which should work with any logger that features an SLF4J 1.7.x binding. (This covers all major loggers.) For example, to use Zlg with Log4j 1.2.17, add the following to your `build.gradle` (replacing `x.y.z` as appropriate).

```groovy
compile "com.obsidiandynamics.zerolog:zerolog-core:x.y.z"
compile "com.obsidiandynamics.zerolog:zerolog-slf4j17:x.y.z"
runtime "org.slf4j:slf4j-api:1.7.25"
runtime "org.slf4j:slf4j-log4j12:1.7.25"
runtime "log4j:log4j:1.2.17"
```

**Note:** `zerolog-slf4j17` doesn't declare a specific `slf4j-api` version Maven dependency, allowing you to nominate _any_ binary-compatible SLF4J API version in your project. The upshot is that you have to explicitly include the `slf4j-api` version in your build file. This can be relegated to the `runtime` configuration (unless you need to use SLF4J directly in your application, alongside Zlg).

## Logging
Getting a logger instance isn't too different from SLF4J. Typically, a named logger instance is first obtained from a factory and subsequently assigned to either an instance or a static field, as shown below.

```java
public final class SysOutLoggingSample {
  private static final Zlg zlg = Zlg.forDeclaringClass().get();
  
  public static void open(String address, int port, double timeoutSeconds) {
    zlg.i("Hello world");
    zlg.i("Pi is %.2f", z -> z.arg(Math.PI));
    zlg.i("Connecting to %s:%d [timeout: %.1f sec]", z -> z.arg(address).arg(port).arg(timeoutSeconds));
    
    try {
      openSocket(address, port, timeoutSeconds);
    } catch (IOException e) {
      zlg.w("Error connecting to %s:%d", z -> z.arg(address).arg(port).tag("I/O").threw(e));
    }
  }
}
```

Some important things to note:

* A logger is a `Zlg` instance, created for a specific class (using `forClass()`) or an arbitrary name (using `forName()`). 
* Calling `forDeclaringClass()` is the shorthand equivalent of `forClass(TheDeclaringClass.class)`, where `TheDeclaringClass` is the name of the class which declares the logger.
* By convention we assign the logger to a field named `zlg`.
* Logging is invoked via a fluent chain, starting with the log level (abbreviated to the first letter) specifying a mandatory format string, followed by any optional arguments (primitives or object types), an optional tag, and an optional exception.
* The format string is printf-style, unlike most other loggers that use the `{}` (stash) notation.

# Lazy args
The chained args pattern works well when the values are _already_ available and can be fed to the logger as-is. If further work is needed to formulate the arguments, then log suppression will not prevent those expressions from being evaluated. For example, the following call will invoke the `size()` method on a `List` irrespective of whether logging is enabled or suppressed.

```java
final List<Integer> numbers = Arrays.asList(5, 6, 7, 8);
zlg.i("The list %s has %d elements", z -> z.arg(numbers).arg(numbers.size()).tag("list"));
```

## Suppliers
To avoid unnecessary argument evaluation, Zlg supports FP-style suppliers and transforms. The above example can be re-written, using a method reference to supply the primitive value via a getter method reference.

```java
zlg.i("The list %s has %d elements", z -> z.arg(numbers).arg(numbers::size).tag("list"));
```

By simply changing `list.size()` to `list::size` we avoid a potentially superfluous method call. Our recommendation is to always favour method references over lambda-style closures. This way _no new code is written_ and there is no impact on code coverage.

## Transforms
Often we won't have the luxury of invoking a single no-arg method on an object to obtain a nice, log-friendly representation. Zlg provides a convenient way of extracting a lazily-evaluated transform into a separate static method, taking a single argument — the object to transform. 

In the next example, we are searching for a person's name from a list of people. If the name isn't found, we'd like to log the list's contents, but not reveal people's surnames. The transform in question is a static `tokeniseSurnames()` function, taking a collection of `Name` objects. To append the transform, we use the `Args.map(Supplier, Function)` utility method, providing both the raw (untransformed) value reference and the transform method reference. The rest is Zlg's problem.

```java
private static final Zlg zlg = Zlg.forDeclaringClass().get();

public static final class Name {
  final String forename;
  final String surname;
  
  Name(String forename, String surname) {
    this.forename = forename;
    this.surname = surname;
  }
}

public static void logWithTransform() {
  final List<Name> hackers = Arrays.asList(new Name("Kevin", "Flynn"), 
                                           new Name("Thomas", "Anderson"), 
                                           new Name("Angela", "Bennett"));
  final String surnameToFind = "Smith";
  
  if (! hackers.stream().anyMatch(n -> n.surname.contains(surnameToFind))) {
    zlg.i("%s not found among %s", 
          z -> z.arg(surnameToFind).arg(Args.map(Args.ref(hackers), LazyLogSample::tokeniseSurnames)));
  }
}

private static List<String> tokeniseSurnames(Collection<Name> names) {
  return names.stream().map(n -> n.forename + " " + n.surname.replaceAll(".", "X")).collect(toList());
}
```

The value being transformed may itself be retrieved lazily. The example below prints the current time using a custom `DateFormat`; the `Date` object is conditionally instantiated.

```java
private static final Zlg zlg = Zlg.forDeclaringClass().get();

public static void logWithSupplierAndTransform() {
  zlg.i("The current time is %s", z -> z.arg(Args.map(Date::new, LazyLogSample::formatDate)));
}

private static String formatDate(Date date) {
  return new SimpleDateFormat("MMM dd HH:mm:ss").format(date);
}
```

One thing to note about transforms and suppliers: they are code like any other and should be unit tested accordingly. You might have a buggy transform and, due to its lazy evaluation, fail to pick up on it when testing code that contains the log instruction (if logging was suppressed). Because transforms and suppliers are simple, single-responsibility 'pure' functions, unit testing them should be straightforward.

# Tags
Zlg adds the concept of a _tag_ — an optional string value that can be used to decorate a log entry. A tag is equivalent to a marker in SLF4J, adding another dimension for slicing and dicing your log output.

# Log levels
Zlg log levels are reasonably well-aligned with SLF4J (and most other loggers). Zlg introduces a new log level — `LogLevel.CONF` — logically situated between `DEBUG` and `INFO`. Loosely borrowed from JUL (`java.util.logging`), `CONF` is intended for logging initialisation and configuration parameters, useful when your application offers a variety of configuration options to the user.

**Note:** `CONF` is canonically mapped to `INFO` in those loggers that don't support `CONF` directly.

The built-in log levels are, from lowest to highest: `TRACE`, `DEBUG`, `CONF`, `INFO`, `WARN`, `ERROR` and `OFF`. 

**Note:** `OFF` is not a legal log level, insofar as it cannot be used to output a log entry from the application code; it's used purely for configuration — being the highest of all levels, **`OFF` is used to disable logging altogether**.

# Configuration
## Bindings
Being a façade, Zlg delegates all log calls to an actual logger — an implementation of `LogService`. By default, Zlg comes pre-packaged with a very basic 'failsafe' `SysOutLogService` that prints entries to `System.out` in a fixed format. Example below.

```
21:18:11.771 INF [main] SysOutLoggingSample.open:20: Connecting to github.com:80 [timeout: 30.0 sec]
21:18:11.773 WRN [main] [I/O] SysOutLoggingSample.open:25: Error connecting to github.com:80
```

Zlg detects installed bindings using Java's [SPI](https://docs.oracle.com/javase/tutorial/ext/basics/spi.html) plugin mechanism. By simply including a binding on the classpath, Zlg will switch over to the new binding by default.

## Logger configuration
Like SLF4J, Zlg is largely hands-off when it comes to logger configuration management, leaving the configuration specifics to the bound logger implementation. Configuration of Log4j 1.2, for example, would be done through `log4j.properties`; Zlg remains completely agnostic of this.

## Baseline configuration with `zlg.properties`
Zlg supports a _baseline_ configuration, by reading an optional `zlg.properties` file from the classpath. A baseline configuration comprises a list of optional properties. For example, it specifies the base log level (below which all logging is disabled) and can override the default binding. The following is an example of `zlg.properties`.

```
zlg.base.level=CONF
zlg.log.service=com.obsidiandynamics.zerolog.SysOutLogService
```

The `zlg.base.level` property specifies the minimum enabled log level (irrespective of what may be allowed by the bound logger). The default value is `CONF`, meaning that **unless the baseline is altered, `TRACE` and `DEBUG` entries will be ignored**. The choice of `CONF` as the default level is closest to a typical production configuration.

While `zlg.properties` is optional, it is strongly recommended that `zlg.properties` is present during development, containing a single `zlg.base.level` entry. Practically, this could be a file in `src/main/resources` or `src/test/resources` with an appropriate VCS ignore rule, so that changes to `zlg.properties` aren't committed back to your VCS. This allows developers to enable/disable debugging for local development without stuffing around with the logger configuration or accidentally committing back the change. Excluding it from VCS means that it will be absent from your CI build or a clean checkout build; this is normally quite acceptable, as the default configuration is geared towards production use.

## Changing the location of `zlg.properties`
The default location of `zlg.properties` can be overridden by setting the `zlg.default.config.uri` system property. The default URI is `cp://zlg.properties`, `cp://` denoting 'classpath'. Alternatively, a file system location can be specified with the `file://` scheme.

When overriding the default file location, ideally the `zlg.default.config.uri` property should be passed in as a `-D...` JVM argument, ensuring that the logging subsystem is bootstrapped correctly before initial use.

## In-line configuration
In addition to `zlg.properties`, Zlg supports in-line configuration at the point when the logger is obtained:

```java
final Zlg zlg = Zlg
    .forDeclaringClass()
    .withConfigService(new LogConfig().withBaseLevel(LogLevel.TRACE))
    .get();
```

In-line configuration assumes priority, overriding any system-default values or values provided by `zlg.properties`. So, if you wanted to force a specific class to log to the console as a special case, while using an SLF4J binding for all other classes, you could just do this:

```java
final Zlg zlg = Zlg
    .forDeclaringClass()
    .withConfigService(new LogConfig()
                       .withBaseLevel(LogLevel.TRACE)
                       .withLogService(new SysOutLogService()))
    .get();
```


# FAQ
## Aren't there enough loggers already?
Zlg isn't a logger, it is a _logging façade_, acting as an interface between your application code and the logger implementation. It is up to the underlying logger to format and persist (or forward) the logs.

Zlg comes with a 'failsafe' logger — `SysOutLogService`; however, this is only a stop-gap measure until you install an appropriate log binding.

## Okay, aren't there enough façades already?
There is one _de facto_ façade — SLF4J. (Ignoring Apache Commons Logging, as it's effectively obsolete.) SLF4J is an excellent all-round library and one that serves an overwhelming majority of use cases. However, as the benchmarks reveal, the penalty of invoking SLF4J in suppressed mode is in the order of 10 ns, which can be substantial for highly performant code and tight loops. When working with sub-microsecond applications, suppressed logging accounts for more than 1% of the cost, which may be unacceptably high.

SLF4J supports guards, so overhead can be driven to a minimum with something like this:

```java
if (logger.isTraceEnabled()) logger.trace("float: {}, double: {}, int: {}, long: {}", f, d, i, l);
```

This presents a few problems, specifically it —

* Introduces a branching instruction, which affects code coverage.
* Leads to code duplication, as the log level must be specified twice.
* Is susceptible to copy-paste errors; we've all accidentally done this before: `if (logger.isDebugEnabled()) logger.warn("Something just happened")`.
* Imposes a double-checking penalty if logging is enabled.

Zlg was designed with one goal — materially reduce the cost of suppressed logging, without sacrificing code coverage or eroding maintainability. A Zlg statement doesn't repeat the level, doesn't introduce a branch statement and doesn't double-check if logging is enabled.

## Why is it so fast?
A better question to ask is why typical loggers and logging façades are slow. A typical SLF4J statement (other loggers are mostly in the same boat) looks like this:

```java
logger.trace("float: {}, double: {}, int: {}, long: {}", f, d, i, l);
```

There are three problems with this approach, each a substantial performance drain.

1. **Use of varargs to pass parameters.** Typically beyond two to four (depending on the level of generosity of the API designer) formatting arguments, loggers will offer a varargs-based API to accommodate arbitrary number of arguments. Varargs are just syntactic sugar; the performance impact is that of array allocation. Furthermore, any escape analysis done ahead of optimisation will conclude that the array could be used beyond the scope of the method call and thus stack allocation will not be possible — a full heap allocation will ensue.

2. **Boxing of primitive types.** Formatting arguments are either `Object` types or vararg arrays of `Object`. Passing primitives to the API will result in autoboxing. The autobox cache is typically quite minuscule; only a relatively small handful of primitives are interned. Even the interned primitives still require branching, offset arithmetic and an array lookup to resolve. (See `Integer.valueOf(int)` for how this is implemented in the JDK.)

3. **Garbage collection.** This is a further symptom of #1 and #2; both varargs and (non-interned) autoboxing ultimately allocate objects irrespective of whether logging is enabled or suppressed (unless accompanied by a 'guard' branch); the allocation rate is especially high when logging is done from tight loops. This negatively impacts application throughput and latency.

To solve #1 and #2 (#3 will follow) using a traditional SLF4J-style API would imply providing an appropriately-typed argument for each of Java's eight primitive types as well as `Object`, for variable arity. Given that there could be any number of inter-mixed primitives and object types, in any combination, then crafting a single interface with all possible combinations of relevant data types is an exponential function of the arity of the argument domain. With four arguments, there will be 6,561 distinct overloaded methods for each level. This would require some form of code generation to achieve, and would certainly kill any IDE auto-completion. With seven arguments, this number is around twenty million (across five log levels).

Zlg solves the above problems by accumulating primitives one-at-a-time into an instance of `Zlg.LogChain`. The `LogChain` API has an `arg()` method for each of Java's eight primitive types, as well as `Object`. By using the fluent chaining pattern, and dealing with one argument at a time, Zlg circumvents the arity problem with a tiny interface. When logging is suppressed, primitives are never boxed and arrays are never allocated — thus Zlg has a zero object allocation rate and zero impact on the GC. (This has been verified with GC profiling.)

As a further optimisation, the interface-driven, inverted dependency design employed by Zlg enables it to substitute its stateful accumulator log chain with a `NopLogChain` as soon as it concludes that logging has been disabled for the requested level. Because the `NopLogChain` implementation is a pre-instantiated singleton with no-op methods and no shared fields, it's subject to aggressive optimisation. Benchmarks conducted with a large number of formatting arguments indicate an incremental cost of around 0.03–0.05 ns per additional argument.

## Can I combine Zlg with SLF4J?
With the `zerolog-slf4j17` binding installed, Zlg acts as a lightweight layer above SLF4J; it does not prevent you from accessing SLF4J's `LoggerFactory` directly. Any code that is already logging with SLF4J's `Logger` may continue to do so unimpeded.

Bear in mind that when logging via Zlg, you may need to set a lower baseline level in `zlg.properties` to forward `TRACE` and `DEBUG` calls to SLF4J. By default, only `CONF` and above are forwarded to the underlying logger.

## Should I replace all uses of SLF4J with Zlg?
While Zlg wasn't intended as a general replacement for SLF4J, it's capable of this. (And there aren't any drawbacks that come to mind.)

For performance-intensive code, switching to Zlg should be a no-brainer. For existing or new code that logs at `INFO` level or above, the choice depends largely on your appetite for uniformity and the importance of maintaining a consistent logging style. 

In addition to performance gains, you'll get a few smaller benefits, such as printf-style formatting, as well as the ability to combine formatting arguments with a `Throwable` — something that's a little awkward with SLF4J. At the risk of sounding subjective, printf-style formatting is a big enough reason to convert all SLF4J calls to Zlg.

## Where should I use Zlg?
Performance-intensive, latency-sensitive code and tight loops with sub-microsecond cycle duration. Our benchmarking has indicated that for a microsecond-long operation, a single unguarded SLF4J statement consumes in excess of 1% of CPU time (on Haswell microarchitecture), with logging suppressed. In an equivalent scenario, Zlg will stay under 0.05% of CPU time. For sub-microsecond operations (10's or 100's of nanoseconds range), the impact of SLF4J is much more dramatic, and you are _forced_ to use guard branches or static constants to either minimise the impact of autoboxing and varargs or to DCE out the logging statements altogether, both approaches producing unsightly code.

## Why isn't a Tag called a Marker?
We felt that the term _tag_ was a more intuitive description of a decorator of log entries, whereas a _marker_ could mean a number of things to an uninitiated user. (For example, a marker has an entirely different meaning when working with I/O streams.) As markers, while available in Log4J and Logback, are a rarely used feature, we felt that by using a substitute term we wouldn't offend anyone in the developer community.

Saying that, we don't have any strong feelings about this term either way, and will happily listen to suggestions. 

## Where are the bindings for other loggers?
We chose to implement one mapping — SFL4J, as it integrates with every mainstream logger. Performance-wise, when logging is enabled, having an extra layer of indirection will carry a small penalty. If you need a direct binding for a specific logger that bypasses SLF4J, we'll gladly accept a PR.

## How can I create my own binding?
Bindings are fairly straightforward to add; just follow the [zerolog-slf4j17](https://github.com/obsidiandynamics/zerolog/tree/master/slf4j17) example.

You need to implement three classes:

* `LogService` — acts as an abstract factory for creating instances of `LogTarget`.
* `LogTarget` — responsible for the actual logging delegation. Has two methods: 
    + `boolean isEnabled(int level)` — for determining whether logging for the given level is enabled
    + `void log(int level, String tag, String format, int argc, Object[] argv, Throwable throwable, String entrypoint)` — for handling the log event (only called if the logging was enabled for that level)
* `LogServiceBinding` — loaded by [SPI](https://docs.oracle.com/javase/tutorial/sound/SPI-intro.html) when the logging subsystem is bootstrapped, responsible for supplying a `LogService` instance.

`LogService` and `LogServiceBinding` are typically one-liners; `LogTarget` is where the bulk of the implementation will reside. Expect a bit of boilerplate mapping code between Zlg and your target logger. For SLFL4J, we made heavy use of function references to streamline the mappings.

Having implemented the above classes, you will also need to add a file named `com.obsidiandynamics.zerolog.LogServiceBinding` to `META-INF/services`. It should contain a single line — the fully qualified name of your `LogServiceBinding` implementation.

**Note:** If, for whatever reason, you are bundling multiple log bindings into an 'uber' jar, make sure you correctly use merge service files. Using the [Gradle Shadow](https://github.com/johnrengelman/shadow) with the `mergeServiceFiles()` option set will take care of this.

## Why use printf-style formatting?
The printf style offers rich formatting options, whereas the traditional stash style is only good for substitution. In practice, when using SLF4J, one often succumbs to this style:

```java
logger.debug(String.format("Connecting to %s:%d [timeout: %,.1f sec]", address, port, timeout));
```

There are two subtle problems with this approach. Firstly, `String.format()` will be unconditionally evaluated, irrespective of whether logging is enabled. This can be rather costly. Secondly, the penalty for getting the format specifiers wrong is severe — `format()` will throw an `IllegalFormatException`. The last thing you need when logging an error or a warning is to have the log call bail on you.

Zlg uses a safe form of `String.format()` (called `SafeFormat.format()`) which is tolerant of format errors, printing the description of the error along with the original format string and arguments. It looks like this:

```
11:29:43.425 INF [main]: WARNING - could not format 'Pi is %d' with args [3.14]:
java.util.IllegalFormatConversionException: d != java.lang.Double
```

## Is class/method/line location information preserved?
When using the `zerolog-slf4j17` binding, location information is correctly preserved for all location-aware loggers.

## I don't care about coverage, can I have a true _zero_-footprint logger?
If sub-nanosecond penalties for suppressed logs are still too high and you require _true zero_, your only option is to strip out the logging instructions altogether. Fortunately, you don't need to do this during compilation; JIT DCE (dead code elimination) can intelligently do this for you on the fly. There are a couple of patterns that achieve zero footprint in different ways.

1. **Branching on a static constant** — will lead to DCE for one of the branches. Example:

```java
private static final Zlg zlg = Zlg.forDeclaringClass().get();

private static final boolean TRACE_ENABLED = false;

public static void withStaticConstant(String address, int port, double timeout) {
  if (TRACE_ENABLED) {
    zlg.t("Connecting to %s:%d [timeout: %.1f sec]", z -> z.arg(address).arg(port).arg(timeout));
  }
}
```

2. **Assertions** — when running with `-ea` logging instructions will be evaluated; otherwise they will be DCE'ed. Example:

```java
private static final Zlg zlg = Zlg.forDeclaringClass().get();

public static void withAssert(String address, int port, double timeout) {
  assert zlg.level(LogLevel.TRACE).format("Connecting to %s:%d [timeout: %.1f sec]").arg(address).arg(port).arg(timeout).log();
}
```

**Note:** Rather than chaining arguments within a lambda, the assertion example uses a slightly longer, continuous chaining style, culminating with a call to `log()`, which returns a constant `true`. If assertions are enabled with the `-ea` JVM argument, the log instruction will be evaluated and will never fail the assertion. Otherwise, the entire fluent chain will be dropped by DCE.

The choice of using option one or two depends on whether you are targeting zero overhead for both production and testing scenarios or only for production. In case of the latter, the `-ea` flag naturally solves the problem, without forcing you to change your class before building. In either case, you will sacrifice code coverage, as both techniques introduce a parasitic branching instruction behind the scenes; only one path is traversed during the test.

**Note:** Outside of the `assert` example, using of the continuous chaining style is strongly discouraged. You run the risk of forgetting to append the final `log()` at the end of the chain, which will have the effect of 'swallowing' the log without forwarding the log event to the underlying logger.

## Is Zlg thread-safe?
All public Zlg classes are thread-safe. For performance, Zlg will pool certain objects in thread-local contexts; the argument chain is one such example. So while `Zlg` instances may be safely shared between threads, once a thread starts building an argument chain, that thread will own that chain — it mustn't share it with other threads.

Zlg will not (and cannot) make any thread-safety guarantees in relation to the underlying logger; however, it would be unusual for a logger to not be thread-safe.

## Is Zlg serializable?
No. While serialization support will likely be added in the near future, until such time you should declare the field with the `transient` modifier and restore it by hand during deserialization.

## Can Zlg be mocked?
Zlg's design is heavily interface-driven, to simplify mocking and testing, which in itself allows us to maintain Zlg with 100% instruction and branch coverage. Even with interfaces, using mocking frameworks (like Mockito) didn't feel like a natural fit for the fluent-style chaining — there are too many methods to mock and verification needs to be depth-aware. (That's probably the only practical drawback of fluent chaining.) 

Long story short, **the recommended way of mocking Zlg is using `MockLogTarget`**. Examples below.

```java
// sink for our mock logs
final MockLogTarget target = new MockLogTarget();

// pipe logs to a mock target
final Zlg zlg = target.logger();

// do some logging...
zlg.t("Pi is %.2f", z -> z.arg(Math.PI).tag("math"));
zlg.d("Euler's number is %.2f", z -> z.arg(Math.E).tag("math"));
zlg.c("Avogadro constant is %.3e", z -> z.arg(6.02214086e23).tag("chemistry"));
zlg.w("An I/O error has occurred", z -> z.threw(new FileNotFoundException()));

// find entries tagged with 'math'
final List<Entry> math = target.entries().tagged("math").list();
System.out.println(math);

// find entries at or above debug
final List<Entry> debugAndAbove = target.entries().forLevelAndAbove(LogLevel.DEBUG).list();
System.out.println(debugAndAbove);

// find entries containing an IOException (or subclass thereof)
final List<Entry> withException = target.entries().withException(IOException.class).list();
System.out.println(withException);

// count number of entries containing the substring 'is'
System.out.println("Entries containing 'is': " + target.entries().containing("is").count());
    
// assert that only one entry contained an exception
target.entries().withThrowable().assertCount(1);
    
// of all the tagged entries, assert that at most two weren't tagged 'chemistry'
target.entries().tagged().not().tagged("chemistry").assertCountAtMost(2);
```

**Note:** If all you need is a no-op logger to quiesce any potential output, and don't care about mocking the fluent call chain, just use `Zlg.nop()` to obtain a silent logger.

## Can I use Mockito for mocking Zlg?
While we use Mockito internally to test the daylights out of Zlg, our _strong recommendation_ is to use `MockLogTarget` exclusively for any application-level mocking of the logger. If you _really_ must to use a mocking framework, here is an example of mocking various parts of the log chain with Mockito 2.18:

```java
final Zlg zlg = mock(Zlg.class, Answers.CALLS_REAL_METHODS);
final LogChain logChain = mock(LogChain.class, Answers.CALLS_REAL_METHODS);
when(logChain.format(any())).thenReturn(logChain);
when(logChain.arg(anyDouble())).thenReturn(logChain);
when(zlg.level(anyInt())).thenReturn(logChain);

zlg.t("the value of Pi is %.2f", z -> z.arg(Math.PI));

verify(logChain).format(contains("the value of Pi"));
verify(logChain).arg(eq(Math.PI));
verify(logChain).flush();
```

## How can I wrap an existing SLF4J logger?
Use `Slf4jWrapper.of(Logger)`, as shown in the example below.

```java
final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

final Zlg zlg = Zlg.forName("wrapper")
    .withConfigService(new LogConfig().withLogService(Slf4jWrapper.of(logger))).get();

zlg.i("Logging to a wrapped SLF4J instance");
```

**Note:** The name passed to `forName()` has no effect in the above example, as `Slf4jWrapper` will return a pre-canned logger.

## How do I correctly write a logging helper or a custom logging façade?
We've all done this before; written a static helper method that logs events in a particular way. Often this is done for logging messages or exceptions. For the latter, we sometimes log an exception internally before letting it percolate up the call stack. 

A related, although a somewhat less common scenario is when library developers try to outsmart the world by writing their own lightweight logging façade, ostensibly allowing the user to plug in any logger without tying them to SLF4J (but in reality adding no value and causing a heartache for all involved).

While the latter is widely considered an anti-pattern, the use of helpers in niche contexts can sometimes be hugely convenient as it leads to standardisation of logging and minimises code duplication. There is no compelling reason why code de-duplication should be discouraged for logging.

The major problem with logging helpers (and hand-rolled façades, for that matter) is that they interfere with the location awareness of the underlying concrete loggers. Loggers capture and output class, method name and line number of the call site; adding a level of indirection obfuscates the real call site, losing crucial debugging data.

Zlg provides a facility for overriding the call site _entrypoint_, letting you write 'lossless' custom logging helpers, bridges and façades. Capturing the call site is done by calling `entrypoint()` on the log chain, supplying the fully-qualified class name of the immediate entrypoint. Example below.

```java
private static final Zlg zlg = Zlg.forDeclaringClass()
    .withConfigService(new LogConfig().withBaseLevel(LogLevel.TRACE)).get();

// class nesting lets us demarcate the entrypoint
private static class LogHelper {
  private static final String entrypoint = LogHelper.class.getName();

  static void traceIOError(String summary, IOException cause) throws IOException {
    // override the call site entry point to the helper class
    zlg.t("I/O error: %s", z -> z.arg(summary).threw(cause).tag("I/O").entrypoint(entrypoint));
    throw cause;
  }
}

public static void main(String[] args) throws IOException {
  // log an error via our helper
  LogHelper.traceIOError("No more data", new EOFException());
}
```

The only caveat is that helpers must be encapsulated in their own class (which may be private), which provides a clean demarcation between the helper and the rest of the application. Calls to the helper must be made from outside the helper class.

Running the example above will output the class/method/line of the call to the helper, not the call to Zlg.

**Note:** The entrypoint trick is also used to implement a bridging logger.

## Can you bridge other loggers to Zlg?
The purpose of a bridge is to route the log events from an encumbered logging framework (e.g. `java.util.logging`) to Zlg. It can almost be thought of as a _reverse binding_, so to speak.

Normally, if you are using Zlg over SLF4J (i.e. the recommended approach), this isn't necessary. SLF4J already comes with all the mainstream bridges you'll need. For example, SLF4J can be bridged from JUL, Log4j, JCL, to name a few.

Zlg has implemented some of its own bridge modules for certain mainstream and niche loggers. These include:

* [bridge-jul](https://github.com/obsidiandynamics/zerolog/tree/master/bridge-jul)
* [bridge-hazelcast](https://github.com/obsidiandynamics/zerolog/tree/master/bridge-hazelcast)

Implementing a custom bridge isn't a big deal. The best place to start is to dissect one of the existing bridge implementations and adapt it to your purpose. Don't neglect call site locations, otherwise log events will appear to be coming from the bridge's plumbing rather than the application code. Use the entrypoint trick when implementing a bridge.

Use the `bridge-xxx` convention to name your bridge modules. If you feel like contributing your bridge, we'd love a PR.
