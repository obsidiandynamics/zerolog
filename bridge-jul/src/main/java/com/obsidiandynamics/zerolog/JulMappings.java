package com.obsidiandynamics.zerolog;

import java.util.function.*;
import java.util.logging.*;

final class JulMappings {
  private static final LevelMapping[] defaultMappings = new LevelMapping[9];
  
  static LevelMapping[] getDefaultMappings() {
    return defaultMappings;
  }
  
  static final class LevelMapping {
    final Level julLevel;
    final int zlgLevel;
    
    LevelMapping(Level julLevel, int zlgLevel) {
      this.julLevel = julLevel;
      this.zlgLevel = zlgLevel;
    }
  }
  
  static {
    int i = 0;
    for (Level level : new Level[] {Level.ALL,
                                    Level.FINEST, 
                                    Level.FINER,
                                    Level.FINE,
                                    Level.CONFIG,
                                    Level.INFO,
                                    Level.WARNING,
                                    Level.SEVERE,
                                    Level.OFF}) {
      defaultMappings[i++] = new LevelMapping(level, mapLevel(level));
    }
  }
  
  static int mapLevel(Level level) {
    final int intValue = level.intValue();
    
    if (intValue <= Level.FINER.intValue()) {
      return LogLevel.TRACE;
    } else if (intValue <= Level.FINE.intValue()) {
      return LogLevel.DEBUG;
    } else if (intValue <= Level.CONFIG.intValue()) {
      return LogLevel.CONF;
    } else if (intValue <= Level.INFO.intValue()) {
      return LogLevel.INFO;
    } else if (intValue <= Level.WARNING.intValue()) {
      return LogLevel.WARN;
    } else if (intValue <= Level.SEVERE.intValue()) {
      return LogLevel.ERROR;
    } else {
      return LogLevel.OFF;
    }
  }
  
  static Level findFirst(LevelMapping[] mappings, IntPredicate predicate) {
    for (LevelMapping mapping : mappings) {
      if (predicate.test(mapping.zlgLevel)) {
        return mapping.julLevel;
      }
    }
    throw new IllegalStateException("No matching level found");
  }
  
  private JulMappings() {}
}
