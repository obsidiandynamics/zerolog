package com.obsidiandynamics.zerolog;

import com.hazelcast.logging.*;

public final class ZlgFactory extends LoggerFactorySupport {
  private static ConfigService configService = ConfigServiceDefaults.getDefaultConfigService();
  
  public static void setConfigService(ConfigService configService) {
    ZlgFactory.configService = configService;
  }
  
  @Override
  public ILogger createLogger(String name) {
    return new ZlgLogger(Zlg.forName(name).withConfigService(configService).get());
  }
}
