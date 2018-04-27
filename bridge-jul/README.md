<img src="https://raw.githubusercontent.com/wiki/obsidiandynamics/zerolog/images/zerolog-logo.png" width="90px" alt="logo"/> JUL to Zlg Bridge
===
[![Download](https://api.bintray.com/packages/obsidiandynamics/zerolog/zerolog-core/images/download.svg) ](https://bintray.com/obsidiandynamics/zerolog/zerolog-core/_latestVersion)

# Intro
This module provides a bridge from `java.util.logging` to Zlg, while forwarding the call site location to the underlying concrete logger.

# Getting Started
## Dependencies
Gradle builds are hosted on JCenter. Add the following snippet to your build file, replacing `x.y.z` with the version shown on the Download badge at the top of this README.

```groovy
compile "com.obsidiandynamics.zerolog:zerolog-core:x.y.z"
compile "com.obsidiandynamics.zerolog:zerolog-bridge-jul:x.y.z"
```

## Sample code
The following example installs the bridge (stashing prior handlers first) and demonstrates logging via JUL, ending up in Zlg. Afterwards the original handlers are restored from the stash.

```java
final Logger logger = Logger.getLogger(JulZlgBridgeSample.class.getName());

// log without the bridge... will go to System.err
logger.log(Level.INFO, "Hello");

final Zlg zlg = Zlg.forDeclaringClass().get();

// stash existing handlers and install a Zlg handler
JulZlgBridge.stashAllHandlers();
JulZlgBridge.install(zlg);

// logging will now end up with Zlg
logger.log(Level.INFO, "Pi is {0} ≈ {1}/{2}", new Object[] {Math.PI, 22, 7});

// uninstall Zlg and restore the previously stashed loggers
JulZlgBridge.uninstall();
JulZlgBridge.unstashAllHandlers();

// logging is now resumed to System.err
logger.log(Level.INFO, "Goodbye");
```

You can keep the original handlers and use the bridge at the same time by skipping the (un)stashing.