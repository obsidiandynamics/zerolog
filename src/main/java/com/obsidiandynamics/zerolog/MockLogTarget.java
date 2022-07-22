package com.obsidiandynamics.zerolog;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

import com.obsidiandynamics.format.*;

/**
 *  A mock {@link LogTarget} that accumulates all log entries and provides a fluent
 *  query API for subsequent retrieval.
 */
public final class MockLogTarget implements LogTarget {
  /**
   *  A single record log entry, comprising the log attributes as they were handed
   *  to this mock by {@link ZlgImpl}.
   */
  public static final class LogEntry {
    private final long timestamp;
    private final int level;
    private final String tag;
    private final String format;
    private final Object[] args;
    private final String message;
    private final Throwable throwable;
    private final String entrypoint;
    
    LogEntry(long timestamp, int level, String tag, String format, Object[] args, String message, 
             Throwable throwable, String entrypoint) {
      this.timestamp = timestamp;
      this.level = level;
      this.tag = tag;
      this.format = format;
      this.args = args;
      this.message = message;
      this.throwable = throwable;
      this.entrypoint = entrypoint;
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

    public List<Object> getArgs() {
      return Arrays.asList(args);
    }

    public String getMessage() {
      return message;
    }

    public Throwable getThrowable() {
      return throwable;
    }
    
    public String getEntrypoint() {
      return entrypoint;
    }

    @Override
    public String toString() {
      return LogEntry.class.getSimpleName() + " [timestamp=" + new Date(timestamp) + ", level=" + level 
          + ", tag=" + tag + ", format=" + format 
          + ", args=" + Arrays.toString(args) + ", message=" + message + ", throwable=" + throwable + "]";
    }
  }
  
  private final int enabledLevel;
  
  private final List<LogEntry> entries = new CopyOnWriteArrayList<>();
  
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
  public void log(int level, String tag, String format, int argc, Object[] argv, Throwable throwable, String entrypoint) {
    final long timestamp = System.currentTimeMillis();
    final Object[] args = SafeFormat.copyArgs(argc, argv);
    final String message = SafeFormat.format(format, argc, argv);
    final LogEntry entry = new LogEntry(timestamp, level, tag, format, args, message, throwable, entrypoint);
    entries.add(entry);
  }
  
  /**
   *  Resets the state of this log target, purging all recorded entries.
   */
  public void reset() {
    entries.clear();
  }
  
  static final class CountAssertionError extends AssertionError {
    private static final long serialVersionUID = 1L;
    CountAssertionError(String m) { super(m); }
  }
  
  static final class IncompleteStateException extends IllegalStateException {
    private static final long serialVersionUID = 1L;
    IncompleteStateException(String m) { super(m); }
  }
  
  /**
   *  A queryable view over the underlying entries, allowing for chained application of
   *  {@link Predicate} filters to progressively narrow down the view, inspect and enact 
   *  assertions over the filtered elements.
   */
  public final class LogEntries implements Iterable<LogEntry> {
    private final Predicate<LogEntry> predicate;
    
    private boolean not;
    
    private void ensureCompletableState() {
      if (not) {
        throw new IncompleteStateException("Fluent chain appears to be incomplete; "
            + "did you forget to append a predicate after not()?");
      }
    }
    
    LogEntries(Predicate<LogEntry> predicate) {
      this.predicate = predicate;
    }
    
    public List<LogEntry> list() {
      ensureCompletableState();
      return Collections.unmodifiableList(entries).stream().filter(predicate).collect(Collectors.toList());
    }
    
    public int count() {
      ensureCompletableState();
      return list().size();
    }
    
    public void assertCount(int expected) {
      if (count() != expected) {
        throw new CountAssertionError(String.format("Expected %,d; was %,d", expected, count()));
      }
    }
    
    public void assertCountAtLeast(int minExpected) {
      if (count() < minExpected) {
        throw new CountAssertionError(String.format("Expected at least %,d; was %,d", minExpected, count()));
      }
    }
    
    public void assertCountAtMost(int maxExpected) {
      if (count() > maxExpected) {
        throw new CountAssertionError(String.format("Expected at most %,d; was %,d", maxExpected, count()));
      }
    }
    
    @Override
    public String toString() {
      ensureCompletableState();
      return list().toString();
    }

    @Override
    public Iterator<LogEntry> iterator() {
      ensureCompletableState();
      return list().iterator();
    }
    
    public LogEntries not() {
      ensureCompletableState();
      not = true;
      return this;
    }
    
    public LogEntries where(Predicate<LogEntry> predicate) {
      final Predicate<LogEntry> predicateToAppend;
      if (not) {
        predicateToAppend = predicate.negate();
        not = false;
      } else {
        predicateToAppend = predicate;
      }
      return new LogEntries(this.predicate.and(predicateToAppend));
    }
    
    public LogEntries forLevel(int level) {
      return where(e -> e.level == level);
    }
    
    public LogEntries forLevelAndBelow(int level) {
      return where(e -> e.level <= level);
    }
    
    public LogEntries forLevelAndAbove(int level) {
      return where(e -> e.level >= level);
    }
    
    public LogEntries tagged() {
      return where(e -> e.tag != null);
    }
    
    public LogEntries tagged(String tag) {
      return where(e -> Objects.equals(e.tag, tag));
    }
    
    public LogEntries before(long timestamp) {
      return where(e -> e.timestamp < timestamp);
    }
    
    public LogEntries after(long timestamp) {
      return where(e -> e.timestamp > timestamp);
    }
    
    public LogEntries withArg() {
      return where(e -> ! e.getArgs().isEmpty());
    }
    
    public LogEntries withArg(Object arg) {
      return where(e -> e.getArgs().contains(arg));
    }
    
    public LogEntries withArgType(Class<?> argClass) {
      return where(e -> e.getArgs().stream().anyMatch(argClass::isInstance));
    }
    
    public LogEntries withFormat(CharSequence format) {
      return where(e -> e.format.contentEquals(format));
    }
    
    public LogEntries withMessage(CharSequence message) {
      return where(e -> e.message.equals(message));
    }
    
    public LogEntries containing(CharSequence substring) {
      return where(e -> e.message.contains(substring));
    }
    
    public LogEntries withThrowableType(Class<? extends Throwable> throwableClass) {
      return where(e -> throwableClass.isInstance(e.throwable));
    }
    
    public LogEntries withThrowable(Throwable throwable) {
      return where(e -> Objects.equals(throwable, e.throwable));
    }
    
    public LogEntries withThrowable() {
      return where(e -> e.throwable != null);
    }
    
    public LogEntries withEntrypoint(String entrypoint) {
      return where(e -> e.entrypoint.equals(entrypoint));
    }
  }
  
  /**
   *  Obtains a queryable view of the stored {@link LogEntry} records.
   *  
   *  @return A queryable view of log entries.
   */
  public LogEntries entries() {
    return new LogEntries(__ -> true);
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
