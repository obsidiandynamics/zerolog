package com.obsidiandynamics.zerolog;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.*;
import org.mockito.*;

import com.obsidiandynamics.zerolog.Zlg.*;

public final class ZlgMockTest {
  @Test
  public void testMockEntireStack() {
    final Zlg zlg = mock(Zlg.class, Answers.CALLS_REAL_METHODS);
    final LogChain logChain = mock(LogChain.class, Answers.CALLS_REAL_METHODS);
    when(logChain.format(any())).thenReturn(logChain);
    when(logChain.entrypoint(any())).thenReturn(logChain);
    when(logChain.arg(anyDouble())).thenReturn(logChain);
    when(zlg.level(anyInt())).thenReturn(logChain);
    
    zlg.t("the value of Pi is %.2f", z -> z.arg(Math.PI));
    
    verify(logChain).format(contains("the value of Pi"));
    verify(logChain).arg(eq(Math.PI));
    verify(logChain).flush(eq(Zlg.ENTRYPOINT));
  }
  
  @Test
  public void testMockLogTarget() {
    final LogTarget logTarget = mock(LogTarget.class);
    when(logTarget.isEnabled(eq(LogLevel.TRACE))).thenReturn(true);
    final Zlg zlg = Zlg.forName("mock")
        .withConfigService(new LogConfig().withBaseLevel(LogLevel.TRACE).withLogService(__ -> logTarget))
        .get();
    
    zlg.t("the value of Pi is %.2f", z -> z.arg(Math.PI).entrypoint("entrypoint"));
    verify(logTarget).log(eq(LogLevel.TRACE), 
                          isNull(), 
                          eq("the value of Pi is %.2f"), 
                          eq(1), 
                          isNotNull(),
                          isNull(),
                          eq("entrypoint"));
  }
}
