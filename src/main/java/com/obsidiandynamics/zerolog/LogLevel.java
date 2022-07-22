package com.obsidiandynamics.zerolog;

public final class LogLevel {
  public static final int OFF = 0x700;   // highest conceivable level, used to turn off logging
  public static final int ERROR = 0x600;
  public static final int WARN = 0x500;
  public static final int INFO = 0x400;
  public static final int CONF = 0x300;
  public static final int DEBUG = 0x200;
  public static final int TRACE = 0x100;
  
  private LogLevel() {}
  
  /**
   *  Obtains the notional ordinal for the given log level. The ordinal is the zero-based position
   *  of the {@code int}-based {@code level} as it appears in {@link LogLevel}.
   *  
   *  @param level The log level.
   *  @return The corresponding ordinal.
   */
  static int ordinal(int level) {
    // very fast, but brittle; take great care when changing level values
    final int numberOfLevels = 7; // number of supported enum constants
    final int trailingMask = 8;   // number of trailing zeros in the level hex values, multiplied by 4
    return numberOfLevels - (level >> trailingMask);
  }
  
  /**
   *  Maps a given {@code int}-based {@code level} to an element residing in a given array, based on its
   *  notional ordinal.
   *  
   *  @param level The log level.
   *  @param elements The array elements.
   *  @return The corresponding element.
   */
  static <T> T map(int level, T[] elements) {
    try {
      return elements[LogLevel.ordinal(level)];
    } catch (Throwable e) {
      throw new IllegalArgumentException("No matching entry for level " + level);
    }
  }
  
  enum Enum {
    OFF("OFF", LogLevel.OFF),
    ERROR("ERR", LogLevel.ERROR),
    WARN("WRN", LogLevel.WARN),
    INFO("INF", LogLevel.INFO),
    CONF("CFG", LogLevel.CONF),
    DEBUG("DBG", LogLevel.DEBUG),
    TRACE("TRC", LogLevel.TRACE);
    
    private final String shortName;
    private final int level;
    
    Enum(String shortName, int level) {
      this.shortName = shortName;
      this.level = level;
    }
    
    String getShortName() {
      return shortName;
    }
    
    int getLevel() {
      return level;
    }
    
    static Enum match(int level) {
      return map(level, Enum.values());
    }
  }
}
