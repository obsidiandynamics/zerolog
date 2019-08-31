package com.obsidiandynamics.zerolog.util;

import static com.obsidiandynamics.func.Functions.*;

import com.obsidiandynamics.worker.*;
import com.obsidiandynamics.zerolog.*;

/**
 *  A Zerolog {@link WorkerExceptionHandler} for dealing with uncaught exceptions.
 */
public final class ZlgWorkerExceptionHandler implements WorkerExceptionHandler {
  private final Zlg zlg;
  
  private final int logLevel;
  
  public ZlgWorkerExceptionHandler(Zlg zlg) {
    this(zlg, LogLevel.ERROR);
  }
  
  public ZlgWorkerExceptionHandler(Zlg zlg, int logLevel) {
    this.zlg = mustExist(zlg, "Zlg cannot be null");
    this.logLevel = logLevel;
  }

  @Override
  public void handle(WorkerThread thread, Throwable cause) {
    zlg.level(logLevel).format("Exception in thread %s").arg(thread::getName).threw(cause).log();
  }
}
