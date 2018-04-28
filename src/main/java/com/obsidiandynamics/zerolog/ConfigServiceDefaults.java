package com.obsidiandynamics.zerolog;

import static com.obsidiandynamics.zerolog.PropertiesConfigService.*;

import java.net.*;

import com.obsidiandynamics.props.*;

public final class ConfigServiceDefaults {
  public static final String KEY_DEFAULT_CONFIG_URI = "zlg.default.config.uri";
  
  private static final URI failsafeUri = URI.create("cp://zlg-failsafe.properties");
  
  /** The default URI to load the properties file from. Supports both cp:// and file:// schemes. */
  private static final URI defaultConfigUri = Props.get(KEY_DEFAULT_CONFIG_URI, 
                                                        URI::create, 
                                                        URI.create("cp://zlg.properties"));
  
  /** The default config service. */
  private static final ConfigService defaultConfigService = 
      new PropertiesConfigService(forUri(defaultConfigUri, failsafeUri));
  
  public static ConfigService getDefaultConfigService() {
    return defaultConfigService;
  }
  
  private ConfigServiceDefaults() {}
}
