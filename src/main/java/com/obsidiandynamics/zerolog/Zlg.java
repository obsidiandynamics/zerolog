package com.obsidiandynamics.zerolog;

public interface Zlg {
  interface LogChain {
    static int MAX_ARGS = 64;
    
    LogChain tag(String tag);
    
    LogChain format(String format);
    
    LogChain arg(boolean arg);
    
    LogChain arg(byte arg);
    
    LogChain arg(char arg);
    
    LogChain arg(double arg);
    
    LogChain arg(float arg);
    
    LogChain arg(int arg);
    
    LogChain arg(long arg);
    
    LogChain arg(short arg);
    
    LogChain arg(Object arg);
    
    LogChain threw(Throwable throwable);
    
    void log();
    
    default boolean logb() {
      log();
      return true;
    }
  }
  
  LogChain level(int level);
  
  boolean isEnabled(int level);
  
  default LogChain e(String format) { return level(LogLevel.ERROR).format(format); }
  
  default LogChain w(String format) { return level(LogLevel.WARN).format(format); }
  
  default LogChain i(String format) { return level(LogLevel.INFO).format(format); }
  
  default LogChain c(String format) { return level(LogLevel.CONF).format(format); }
  
  default LogChain d(String format) { return level(LogLevel.DEBUG).format(format); }
  
  default LogChain t(String format) { return level(LogLevel.TRACE).format(format); }
  
  static ZlgBuilder forName(String name) {
    return new ZlgBuilder(name);
  }
  
  static ZlgBuilder forClass(Class<?> cls) {
    return forName(cls.getName());
  }
}
