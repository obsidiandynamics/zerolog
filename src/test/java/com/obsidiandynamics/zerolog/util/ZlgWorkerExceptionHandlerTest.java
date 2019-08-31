package com.obsidiandynamics.zerolog.util;

import org.junit.*;

import com.obsidiandynamics.worker.*;
import com.obsidiandynamics.zerolog.*;

public final class ZlgWorkerExceptionHandlerTest {
  @Test
  public void testHandle() {
    final MockLogTarget logTarget = new MockLogTarget();
    final ZlgWorkerExceptionHandler handler = new ZlgWorkerExceptionHandler(logTarget.logger());
    final WorkerThread thread = WorkerThread.builder()
        .withOptions(new WorkerOptions().withName("testThread"))
        .onCycle(__ -> {})
        .build();
    final Exception cause = new Exception("Simulated");
    
    handler.handle(thread, cause);
    logTarget.entries().assertCount(1);
    logTarget.entries().forLevel(LogLevel.ERROR)
    .withMessage("Exception in thread testThread").withThrowable(cause).assertCount(1);
  }
}
