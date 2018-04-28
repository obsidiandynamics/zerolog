<img src="https://raw.githubusercontent.com/wiki/obsidiandynamics/zerolog/images/zerolog-logo.png" width="90px" alt="logo"/> SLF4J 1.7.x Binding
===
[![Download](https://api.bintray.com/packages/obsidiandynamics/zerolog/zerolog-core/images/download.svg) ](https://bintray.com/obsidiandynamics/zerolog/zerolog-core/_latestVersion)

This module provides a binding for the SLF4J API, version 1.7.x.

# Getting Started
## Dependencies
Gradle builds are hosted on JCenter. Add the following snippet to your build file, replacing `x.y.z` with the version shown on the Download badge at the top of this README.

```groovy
compile "com.obsidiandynamics.zerolog:zerolog-core:x.y.z"
compile "com.obsidiandynamics.zerolog:zerolog-slf4j17:x.y.z"
```

**Note:** `zerolog-slf4j17` doesn't declare a specific `slf4j-api` version Maven dependency, allowing you to nominate _any_ binary-compatible SLF4J API version in your project. The upshot is that you have to explicitly include the `slf4j-api` version in your build file. This can be relegated to the `runtime` configuration (unless you need to use SLF4J directly in your application, alongside Zlg).

## Installation
Installation is automatic — simply include the library on the classpath to activate the binding (replacing the default `SysOutLoggingService`). Make sure an alternate binding isn't specified in `zlg.properties`, as the latter takes precedence.

In the presence of this binding, all log events will be pumped to SLF4J. The baseline log level specified by the `zlg.base.level` property in `zlg.properties` acts as a preliminary filter, overriding any SLF4J (or concrete logger) configuration. Ensure that `zlg.base.level` is set to the lowest expected logging level.

To manually select the binding (if multiple bindings are present on the classpath), edit your `zlg.properties` and set the property `zlg.log.service` to `com.obsidiandynamics.zerolog.Slf4jLogService`.