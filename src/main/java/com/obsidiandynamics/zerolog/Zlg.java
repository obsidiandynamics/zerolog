package com.obsidiandynamics.zerolog;

import java.util.function.*;

public interface Zlg {
  interface LogChain {
    static int MAX_ARGS = 64;
    
    LogChain tag(String tag);
    
    LogChain format(String format);
    
    LogChain arg(boolean arg);
    
    LogChain arg(BooleanSupplier supplier);
    
    LogChain arg(byte arg);
    
    LogChain arg(char arg);
    
    LogChain arg(double arg);
    
    LogChain arg(DoubleSupplier supplier);
    
    LogChain arg(float arg);
    
    LogChain arg(int arg);
    
    LogChain arg(IntSupplier supplier);
    
    LogChain arg(long arg);
    
    LogChain arg(LongSupplier supplier);
    
    LogChain arg(short arg);
    
    LogChain arg(Object arg);
    
    LogChain arg(Supplier<?> supplier);
    
    <T> LogChain arg(T value, Function<? super T, ?> transform);
    
    LogChain threw(Throwable throwable);
    
    default void with(Consumer<LogChain> logChainConsumer) {
      logChainConsumer.accept(this);
      done();
    }
    
    void done();
    
    default boolean log() {
      done();
      return true;
    }
  }
  
  LogChain level(int level);
  
  boolean isEnabled(int level);
  
  default LogChain e(String format) { return level(LogLevel.ERROR).format(format); }
  
  default void e(String format, Consumer<LogChain> logChainConsumer) {
    level(LogLevel.ERROR).format(format).with(logChainConsumer);
  }
  
  default LogChain w(String format) { return level(LogLevel.WARN).format(format); }
  
  default void w(String format, Consumer<LogChain> logChainConsumer) {
    level(LogLevel.WARN).format(format).with(logChainConsumer);
  }
  
  default LogChain i(String format) { return level(LogLevel.INFO).format(format); }
  
  default void i(String format, Consumer<LogChain> logChainConsumer) {
    level(LogLevel.INFO).format(format).with(logChainConsumer);
  }
  
  default LogChain c(String format) { return level(LogLevel.CONF).format(format); }
  
  default void c(String format, Consumer<LogChain> logChainConsumer) {
    level(LogLevel.CONF).format(format).with(logChainConsumer);
  }
  
  default LogChain d(String format) { return level(LogLevel.DEBUG).format(format); }
  
  default void d(String format, Consumer<LogChain> logChainConsumer) {
    level(LogLevel.DEBUG).format(format).with(logChainConsumer);
  }
  
  default LogChain t(String format) { return level(LogLevel.TRACE).format(format); }
  
  default void t(String format, Consumer<LogChain> logChainConsumer) {
    level(LogLevel.TRACE).format(format).with(logChainConsumer);
  }
  
  static ZlgBuilder forName(String name) {
    return new ZlgBuilder(name);
  }
  
  static ZlgBuilder forClass(Class<?> cls) {
    return forName(cls.getName());
  }
}
