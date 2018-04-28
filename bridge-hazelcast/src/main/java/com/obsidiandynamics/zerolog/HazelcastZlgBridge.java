package com.obsidiandynamics.zerolog;

/**
 *  Bridges a {@link com.hazelcast.logging.ILogger} to Zerolog.
 */
public final class HazelcastZlgBridge {
  private static final String PROPERTY_KEY = "hazelcast.logging.class";
  
  private static final String factoryClass = ZlgFactory.class.getName();

  /**
   *  Determines whether the bridge is currently installed.
   *  
   *  @return True if the bridge is installed.
   */
  public static boolean isInstalled() {
    return factoryClass.equals(System.getProperty(PROPERTY_KEY));
  }
  
  /**
   *  Installs the bridge with the default {@link ConfigService}.
   */
  public static void install() {
    install(ConfigServiceDefaults.getDefaultConfigService());
  }
  
  /**
   *  Installs the bridge with a specified {@link ConfigService}.
   *  
   *  @param configService A way to configure Zlg.
   */
  public static void install(ConfigService configService) {
    ZlgFactory.setConfigService(configService);
    System.setProperty(PROPERTY_KEY, factoryClass);
  }
  
  /**
   *  Uninstalls the current bridge.
   */
  public static void uninstall() {
    if (isInstalled()) {
      System.getProperties().remove(PROPERTY_KEY);
      ZlgFactory.setConfigService(ConfigServiceDefaults.getDefaultConfigService());
    }
  }
  
  private HazelcastZlgBridge() {}
}
