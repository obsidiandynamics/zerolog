package com.obsidiandynamics.zerolog;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.*;
import org.mockito.*;

import com.obsidiandynamics.zerolog.Zlg.*;

public final class ZlgMockTest {
  @Test
  public void testMock() {
    final Zlg zlg = mock(Zlg.class, Answers.CALLS_REAL_METHODS);
    final LogChain logChain = mock(LogChain.class, Answers.CALLS_REAL_METHODS);
    when(logChain.format(any())).thenReturn(NopLogChain.getInstance());
    when(zlg.level(anyInt())).thenReturn(logChain);
    
    zlg.t("the value of Pi is %.3f").arg(Math.PI).log();
  }
}
