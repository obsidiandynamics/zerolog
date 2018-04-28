<img src="https://raw.githubusercontent.com/wiki/obsidiandynamics/zerolog/images/zerolog-logo.png" width="90px" alt="logo"/> Hazelcast to Zlg Bridge
===
[![Download](https://api.bintray.com/packages/obsidiandynamics/zerolog/zerolog-core/images/download.svg) ](https://bintray.com/obsidiandynamics/zerolog/zerolog-core/_latestVersion)

This module provides a bridge from Hazelcast's `com.hazelcast.logging.ILogger`to Zlg, while forwarding the call site location to the underlying concrete logger.

# Getting Started
## Dependencies
Gradle builds are hosted on JCenter. Add the following snippet to your build file, replacing `x.y.z` with the version shown on the Download badge at the top of this README.

```groovy
compile "com.obsidiandynamics.zerolog:zerolog-core:x.y.z"
compile "com.obsidiandynamics.zerolog:zerolog-bridge-hazelcast:x.y.z"
```

**Note:** `zerolog-bridge-hazelcast` doesn't declare a specific `com.hazelcast:hazelcast` version Maven dependency, allowing you to include _any_ binary-compatible Hazelcast library in your project. This bridge has been tested with Hazelcast 3.10 (Beta 2).

## Programmatic installation
The following example installs the bridge for the system property and demonstrates logging via Hazelcast, ending up in Zlg. Afterwards the bridge is uninstalled.

```java
// install the bridge; all logs will now be pumped to Zlg
HazelcastZlgBridge.install();

// create a Hazelcast instance and get a logger
final HazelcastInstance instance = new TestHazelcastInstanceFactory().newHazelcastInstance();
final ILogger logger = instance.getLoggingService().getLogger(HazelcastZlgBridgeSimplifiedSample.class);

logger.warning("This is a drill, this is a drill");

// clean up
instance.shutdown();
HazelcastZlgBridge.uninstall();
```

## Installing via a system property
You can also configure the bridge by setting the `hazelcast.logging.class` system property to `com.obsidiandynamics.zerolog.ZlgFactory`.

