package com.obsidiandynamics.zerolog;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

/**
 *  A mock {@link LogTarget} that accumulates all log entries and provides a fluent
 *  query API for subsequent retrieval.
 */
public final class MockLogTarget implements LogTarget {
  /**
   *  A single record log entry, comprising the log attributes as they were handed
   *  to this mock by {@link ZlgImpl}.
   */
  public static final class Entry {
    private final long timestamp;
    private final int level;
    private final String tag;
    private final String format;
    private final Object[] args;
    private final String message;
    private final Throwable throwable;
    
    Entry(long timestamp, int level, String tag, String format, Object[] args, String message, Throwable throwable) {
      this.timestamp = timestamp;
      this.level = level;
      this.tag = tag;
      this.format = format;
      this.args = args;
      this.message = message;
      this.throwable = throwable;
    }
    
    public long getTimestamp() {
      return timestamp;
    }

    public int getLevel() {
      return level;
    }

    public String getTag() {
      return tag;
    }

    public String getFormat() {
      return format;
    }

    public Object[] getArgs() {
      return args;
    }

    public String getMessage() {
      return message;
    }

    public Throwable getThrowable() {
      return throwable;
    }

    @Override
    public String toString() {
      return Entry.class.getSimpleName() + " [timestamp=" + new Date(timestamp) + ", level=" + level 
          + ", tag=" + tag + ", format=" + format 
          + ", args=" + Arrays.toString(args) + ", message=" + message + ", throwable=" + throwable + "]";
    }
  }
  
  private final int enabledLevel;
  
  private final List<Entry> entries = new CopyOnWriteArrayList<>();
  
  /**
   *  Creates a new mock target at the lowest log level — {@link LogLevel#TRACE}.
   */
  public MockLogTarget() {
    this(LogLevel.TRACE);
  }
  
  /**
   *  Creates a new mock target that will accept logs at the given level (or above).
   *  
   *  @param enabledLevel The level to enable.
   */
  public MockLogTarget(int enabledLevel) {
    this.enabledLevel = enabledLevel;
  }

  @Override
  public boolean isEnabled(int level) {
    return level >= enabledLevel;
  }

  @Override
  public void log(int level, String tag, String format, int argc, Object[] argv, Throwable throwable) {
    final long timestamp = System.currentTimeMillis();
    final Object[] args = SafeFormat.copyArgs(argc, argv);
    final String message = SafeFormat.format(format, argc, argv);
    final Entry entry = new Entry(timestamp, level, tag, format, args, message, throwable);
    entries.add(entry);
  }
  
  /**
   *  Resets the state of this log target, purging all recorded entries.
   */
  public void reset() {
    entries.clear();
  }
  
  /**
   *  A queryable view over the underlying entries, allowing for chained application of
   *  {@link Predicate} filters to progressively narrow down the view.
   */
  public final class Entries implements Iterable<Entry> {
    private final Predicate<Entry> predicate;
    
    Entries(Predicate<Entry> predicate) {
      this.predicate = predicate;
    }
    
    public List<Entry> list() {
      return Collections.unmodifiableList(entries).stream().filter(predicate).collect(Collectors.toList());
    }

    @Override
    public Iterator<Entry> iterator() {
      return list().iterator();
    }
    
    public Entries filter(Predicate<Entry> predicate) {
      return new Entries(this.predicate.and(predicate));
    }
    
    public Entries forLevel(int level) {
      return filter(e -> e.level == level);
    }
    
    public Entries forLevelAndAbove(int level) {
      return filter(e -> e.level >= level);
    }
    
    public Entries tagged(String tag) {
      return filter(e -> Objects.equals(e.tag, tag));
    }
    
    public Entries before(long timestamp) {
      return filter(e -> e.timestamp < timestamp);
    }
    
    public Entries after(long timestamp) {
      return filter(e -> e.timestamp > timestamp);
    }
    
    public Entries containing(CharSequence substring) {
      return filter(e -> e.message.contains(substring));
    }
    
    public Entries withException(Class<? extends Throwable> exceptionClass) {
      return filter(e -> e.throwable != null && exceptionClass.isInstance(e.throwable));
    }
  }
  
  /**
   *  Obtains a queryable view of the stored {@link Entry} records.
   *  
   *  @return A queryable view of log entries.
   */
  public Entries entries() {
    return new Entries(__ -> true);
  }

  /**
   *  Provides a {@link LogService} for serving <em>this</em> {@link MockLogTarget} instance.
   *  
   *  @return A log service factory.
   */
  public LogService logService() {
    return __ -> this;
  }
  
  /**
   *  A convenience method for creating a fully-configured {@link Zlg} logger instance that will
   *  delegate logs to <em>this</em> {@link MockLogTarget} instance.
   *  
   *  @return A pre-configured logger.
   */
  public Zlg logger() {
    return Zlg
        .forName("mock")
        .withConfigService(new LogConfig().withBaseLevel(enabledLevel).withLogService(logService()))
        .get();
  }
}
