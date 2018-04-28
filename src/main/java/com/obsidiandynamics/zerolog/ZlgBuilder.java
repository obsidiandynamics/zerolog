package com.obsidiandynamics.zerolog;

/**
 *  Fluent builder for {@link Zlg} instances.
 */
public final class ZlgBuilder {
  private final String name;
  
  private ConfigService configService = ConfigServiceDefaults.getDefaultConfigService();

  ZlgBuilder(String name) {
    this.name = name;
  }
  
  public ZlgBuilder withConfigService(ConfigService configService) {
    this.configService = configService;
    return this;
  }
  
  /**
   *  Obtains a configured {@link Zlg} instance.
   *  
   *  @return A {@link Zlg} logger.
   */
  public Zlg get() {
    return new ZlgImpl(name, configService.get());
  }
}
