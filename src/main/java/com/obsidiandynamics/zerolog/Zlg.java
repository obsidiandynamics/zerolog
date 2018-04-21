package com.obsidiandynamics.zerolog;

import java.util.function.*;

import com.obsidiandynamics.zerolog.util.*;

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
  
  default void e(String message) { 
    level(LogLevel.ERROR).format(message).done(); 
  }
  
  default void e(String summary, Throwable cause) {
    level(LogLevel.ERROR).format(summary).threw(cause).done(); 
  }
  
  default void e(String format, Consumer<LogChain> logChainConsumer) {
    level(LogLevel.ERROR).format(format).with(logChainConsumer);
  }
  
  default void w(String message) { 
    level(LogLevel.WARN).format(message).done(); 
  }
  
  default void w(String summary, Throwable cause) {
    level(LogLevel.WARN).format(summary).threw(cause).done(); 
  }
  
  default void w(String format, Consumer<LogChain> logChainConsumer) {
    level(LogLevel.WARN).format(format).with(logChainConsumer);
  }
  
  default void i(String message) { 
    level(LogLevel.INFO).format(message).done(); 
  }
  
  default void i(String summary, Throwable cause) {
    level(LogLevel.INFO).format(summary).threw(cause).done(); 
  }
  
  default void i(String format, Consumer<LogChain> logChainConsumer) {
    level(LogLevel.INFO).format(format).with(logChainConsumer);
  }
  
  default void c(String message) { 
    level(LogLevel.CONF).format(message).done();
  }
  
  default void c(String summary, Throwable cause) {
    level(LogLevel.CONF).format(summary).threw(cause).done(); 
  }
  
  default void c(String format, Consumer<LogChain> logChainConsumer) {
    level(LogLevel.CONF).format(format).with(logChainConsumer);
  }
  
  default void d(String message) { 
    level(LogLevel.DEBUG).format(message).done(); 
  }

  default void d(String summary, Throwable cause) {
    level(LogLevel.DEBUG).format(summary).threw(cause).done(); 
  }
  
  default void d(String format, Consumer<LogChain> logChainConsumer) {
    level(LogLevel.DEBUG).format(format).with(logChainConsumer);
  }
  
  default void t(String message) {
    level(LogLevel.TRACE).format(message).done(); 
  }
  
  default void t(String summary, Throwable cause) {
    level(LogLevel.TRACE).format(summary).threw(cause).done(); 
  }
  
  default void t(String format, Consumer<LogChain> logChainConsumer) {
    level(LogLevel.TRACE).format(format).with(logChainConsumer);
  }
  
  static ZlgBuilder forName(String name) {
    return new ZlgBuilder(name);
  }
  
  static ZlgBuilder forClass(Class<?> cls) {
    return forName(cls.getName());
  }
  
  static ZlgBuilder forDeclaringClass() {
    return forClass(CallingClass.forDepth(2));
  }
}
