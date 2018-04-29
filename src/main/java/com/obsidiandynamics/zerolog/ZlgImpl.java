package com.obsidiandynamics.zerolog;

import java.util.function.*;

final class ZlgImpl implements Zlg {
  static final class DuplicateValueException extends IllegalStateException {
    private static final long serialVersionUID = 1L;
    DuplicateValueException(String m) { super(m); }
  }
  
  static final class MissingValueException extends IllegalStateException {
    private static final long serialVersionUID = 1L;
    MissingValueException(String m) { super(m); }
  }
  
  static final class TooManyArgsException extends IllegalStateException {
    private static final long serialVersionUID = 1L;
    TooManyArgsException(String m) { super(m); }
  }

  final class LogChainImpl implements LogChain {
    private int level;
    private String tag;
    private String format;
    private int argc;
    private Object[] argv = new Object[MAX_ARGS];
    private Throwable throwable;
    private String entrypoint;
    
    private void reset() {
      tag = null;
      format = null;
      for (int i = 0; i < argc; i++) {
        argv[i] = null;
      }
      argc = 0;
      throwable = null;
      entrypoint = null;
    }

    @Override
    public LogChain tag(String tag) {
      if (this.tag != null) throw new DuplicateValueException("Duplicate call to tag()");
      this.tag = tag;
      return this;
    }

    @Override
    public LogChain format(String format) {
      if (this.format != null) throw new DuplicateValueException("Duplicate call to format()");
      this.format = format;
      return this;
    }
    
    @Override
    public LogChain message(Object message) {
      return format("%s").arg(message);
    }

    @Override
    public LogChain arg(boolean arg) {
      return appendArg(arg);
    }

    @Override
    public LogChain arg(BooleanSupplier supplier) {
      return appendArg(supplier.getAsBoolean());
    }

    @Override
    public LogChain arg(byte arg) {
      return appendArg(arg);
    }

    @Override
    public LogChain arg(char arg) {
      return appendArg(arg);
    }

    @Override
    public LogChain arg(double arg) {
      return appendArg(arg);
    }

    @Override
    public LogChain arg(DoubleSupplier supplier) {
      return appendArg(supplier.getAsDouble());
    }

    @Override
    public LogChain arg(float arg) {
      return appendArg(arg);
    }

    @Override
    public LogChain arg(int arg) {
      return appendArg(arg);
    }

    @Override
    public LogChain arg(IntSupplier supplier) {
      return appendArg(supplier.getAsInt());
    }

    @Override
    public LogChain arg(long arg) {
      return appendArg(arg);
    }

    @Override
    public LogChain arg(LongSupplier supplier) {
      return appendArg(supplier.getAsLong());
    }

    @Override
    public LogChain arg(short arg) {
      return appendArg(arg);
    }

    @Override
    public LogChain arg(Object arg) {
      return appendArg(arg);
    }

    @Override
    public LogChain arg(Supplier<?> supplier) {
      return appendArg(supplier.get());
    }

    private LogChain appendArg(Object arg) {
      if (argc == MAX_ARGS) throw new TooManyArgsException("Number of args cannot exceed " + MAX_ARGS);
      argv[argc++] = arg;
      return this;
    }
    
    @Override
    public LogChain entrypoint(String entrypoint) {
      this.entrypoint = entrypoint;
      return this;
    }

    @Override
    public LogChain threw(Throwable throwable) {
      if (this.throwable != null) throw new DuplicateValueException("Duplicate call to threw()");
      this.throwable = throwable;
      return this;
    }

    @Override
    public void flush(String assumedEntrypoint) {
      if (format == null) throw new MissingValueException("Missing call to format()");
      final String entrypoint = this.entrypoint;
      target.log(level, tag, format, argc, argv, throwable, entrypoint != null ? entrypoint : assumedEntrypoint);
      reset();
    }
  }
  
  private final int baseLevel;
  
  private final LogTarget target;
  
  private final ThreadLocal<LogChainImpl> threadLocalChain = ThreadLocal.withInitial(LogChainImpl::new);
  
  ZlgImpl(String name, LogConfig config) {
    baseLevel = config.getBaseLevel();
    target = config.getLogService().get(name);
  }
  
  @Override
  public LogChain level(int level) {
    if (isEnabled(level)) {
      if (level == LogLevel.OFF) throw new IllegalArgumentException("Cannot log at level " + LogLevel.Enum.OFF.name());
      
      final LogChainImpl chain = threadLocalChain.get();
      chain.level = level;
      return chain;
    } else {
      return NopLogChain.getInstance();
    }
  }
  
  @Override
  public boolean isEnabled(int level) {
    return level >= baseLevel && target.isEnabled(level);
  }
}
