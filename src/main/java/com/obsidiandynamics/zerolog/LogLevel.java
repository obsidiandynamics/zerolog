package com.obsidiandynamics.zerolog;

public enum LogLevel {
  OFF("OFF"), // highest conceivable level, used to turn off logging
  ERROR("ERR"),
  WARN("WRN"),
  INFO("INF"),
  CONF("CFG"),
  DEBUG("DBG"),
  TRACE("TRC");
  
  private final String shortName;
  
  private LogLevel(String shortName) {
    this.shortName = shortName;
  }
  
  public String getShortName() {
    return shortName;
  }
  
  public boolean sameOrHigherThan(LogLevel other) {
    return sameOrHigherThan(other.ordinal());
  }
  
  public boolean sameOrHigherThan(int logLevelOrdinal) {
    return ordinal() <= logLevelOrdinal;
  }
}
