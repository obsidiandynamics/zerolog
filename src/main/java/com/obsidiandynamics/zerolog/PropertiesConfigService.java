package com.obsidiandynamics.zerolog;

import java.io.*;
import java.net.*;
import java.util.*;

import com.obsidiandynamics.func.*;
import com.obsidiandynamics.io.*;
import com.obsidiandynamics.props.*;

public final class PropertiesConfigService implements ConfigService {
  public static final String KEY_BASE_LEVEL = "zlg.base.level";
  public static final String KEY_LOG_SERVICE = "zlg.log.service";
  
  static final class ServiceInstantiationException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    ServiceInstantiationException(Throwable cause) { super(cause); }
  }  
  
  static final class PropertiesLoadException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    PropertiesLoadException(Throwable cause) { super(cause); }
  }
  
  @FunctionalInterface
  public interface PropertiesLoader extends CheckedSupplier<Properties, Exception> {}
  
  private final PropertiesLoader propsLoader;
  
  private final Object cacheLock = new Object();
  
  private LogConfig cachedConfig;
  
  public PropertiesConfigService(PropertiesLoader propsLoader) {
    this.propsLoader = propsLoader;
  }

  @Override
  public LogConfig get() {
    synchronized (cacheLock) {
      if (cachedConfig == null) {
        final Properties props = Exceptions.wrap(propsLoader::get, PropertiesLoadException::new);
        cachedConfig = loadConfig(props);
      }
    }
    return cachedConfig;
  }
  
  private static LogConfig loadConfig(Properties props) {
    final LogLevel.Enum baseLevel = Props.get(props, KEY_BASE_LEVEL, LogLevel.Enum::valueOf, null);
    final String logServiceClass = Props.get(props, KEY_LOG_SERVICE, String::valueOf, null);
    
    final LogConfig config = new LogConfig();
    if (baseLevel != null) {
      config.withBaseLevel(baseLevel.getLevel());
    }
    if (logServiceClass != null && ! logServiceClass.equals(config.getLogService().getClass().getName())) {
      config.withLogService(instantiateLogService(logServiceClass));
    }
    return config;
  }
  
  private static LogService instantiateLogService(String logServiceClassName) {
    return Exceptions.wrap(() -> Classes.cast(Class.forName(logServiceClassName).getDeclaredConstructor().newInstance()), 
                           ServiceInstantiationException::new);
  }
  
  public static PropertiesLoader forUri(URI uri) {
    return forUri(uri, uri);
  }
  
  public static PropertiesLoader forUri(URI preferredUri, URI failsafeUri) {
    return () -> {
      final Properties props = new Properties();
      try {
        props.load(ResourceLoader.stream(preferredUri));
      } catch (FileNotFoundException e) {
        props.load(ResourceLoader.stream(failsafeUri));
      }
      return props;
    };
  }
}
