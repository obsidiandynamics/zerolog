package com.obsidiandynamics.zerolog;

import java.util.function.*;

import com.obsidiandynamics.zerolog.util.*;

public interface Zlg {
  static String entrypoint = Zlg.class.getName();
  
  interface LogChain {
    static String entrypoint = LogChain.class.getName();
    
    static int MAX_ARGS = 64;
    
    LogChain tag(String tag);
    
    LogChain format(String format);
    
    LogChain message(Object message);
    
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
    
    LogChain arg(Object arg); // lgtm [java/confusing-method-signature]
    
    LogChain arg(Supplier<?> supplier);
    
    LogChain threw(Throwable throwable);
    
    LogChain entrypoint(String entrypoint);
    
    /**
     *  Feeds the log chain into a consumer and then completes the log chain.
     *  
     *  @param logChainConsumer Consumer for the log chain.
     */
    default void flush(Consumer<LogChain> logChainConsumer) {
      logChainConsumer.accept(this);
      flush(Zlg.entrypoint);
    }
    
    /**
     *  Completes the log chain. <em>Must only to be called by the framework</em> in order to 
     *  preserve location information.
     *  
     *  @param assumedEntrypoint The assumed default entrypoint. 
     *                           May be overridden by a prior call to {@link #entrypoint(String)}.
     */
    void flush(String assumedEntrypoint);
    
    default boolean log() {
      flush(entrypoint);
      return true;
    }
  }
  
  LogChain level(int level);
  
  boolean isEnabled(int level);
  
  default void e(Object message) { 
    level(LogLevel.ERROR).message(message).flush(entrypoint); 
  }
  
  default void e(String summary, Throwable cause) {
    level(LogLevel.ERROR).message(summary).threw(cause).flush(entrypoint); 
  }
  
  default void e(String format, Consumer<LogChain> logChainConsumer) {
    level(LogLevel.ERROR).format(format).flush(logChainConsumer);
  }
  
  default void w(Object message) { 
    level(LogLevel.WARN).message(message).flush(entrypoint); 
  }
  
  default void w(String summary, Throwable cause) {
    level(LogLevel.WARN).message(summary).threw(cause).flush(entrypoint); 
  }
  
  default void w(String format, Consumer<LogChain> logChainConsumer) {
    level(LogLevel.WARN).format(format).flush(logChainConsumer);
  }
  
  default void i(Object message) { 
    level(LogLevel.INFO).message(message).flush(entrypoint); 
  }
  
  default void i(String summary, Throwable cause) {
    level(LogLevel.INFO).message(summary).threw(cause).flush(entrypoint); 
  }
  
  default void i(String format, Consumer<LogChain> logChainConsumer) {
    level(LogLevel.INFO).format(format).flush(logChainConsumer);
  }
  
  default void c(Object message) { 
    level(LogLevel.CONF).message(message).flush(entrypoint);
  }
  
  default void c(String summary, Throwable cause) {
    level(LogLevel.CONF).message(summary).threw(cause).flush(entrypoint); 
  }
  
  default void c(String format, Consumer<LogChain> logChainConsumer) {
    level(LogLevel.CONF).format(format).flush(logChainConsumer);
  }
  
  default void d(Object message) { 
    level(LogLevel.DEBUG).message(message).flush(entrypoint); 
  }

  default void d(String summary, Throwable cause) {
    level(LogLevel.DEBUG).message(summary).threw(cause).flush(entrypoint); 
  }
  
  default void d(String format, Consumer<LogChain> logChainConsumer) {
    level(LogLevel.DEBUG).format(format).flush(logChainConsumer);
  }
  
  default void t(Object message) {
    level(LogLevel.TRACE).message(message).flush(entrypoint); 
  }
  
  default void t(String summary, Throwable cause) {
    level(LogLevel.TRACE).message(summary).threw(cause).flush(entrypoint); 
  }
  
  default void t(String format, Consumer<LogChain> logChainConsumer) {
    level(LogLevel.TRACE).format(format).flush(logChainConsumer);
  }
  
  static ZlgBuilder forName(String name) {
    return new ZlgBuilder(name);
  }
  
  static ZlgBuilder forClass(Class<?> cls) {
    return forName(cls.getName());
  }
  
  static ZlgBuilder forDeclaringClass() {
    return forClass(Stacks.classForDepth(2));
  }
  
  /** Pre-canned no-op logger. */
  static Zlg nop = Zlg.forName("no-op").withConfigService(new LogConfig()
                                                          .withBaseLevel(LogLevel.OFF)
                                                          .withLogService(LogService.nop())).get();
  
  static Zlg nop() {
    return nop;
  }
}
