package com.obsidiandynamics.zerolog;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.function.*;

import org.junit.*;
import org.mockito.*;

import com.obsidiandynamics.func.*;
import com.obsidiandynamics.zerolog.Zlg.*;

public final class ZlgTest {
  @Test
  public void testDefaultMessageMethods() {
    final Zlg z = mock(Zlg.class, Answers.CALLS_REAL_METHODS);
    final LogChain chain = mock(LogChain.class, Answers.CALLS_REAL_METHODS);
    when(chain.format(any())).thenReturn(chain);
    when(chain.entrypoint(any())).thenReturn(chain);
    when(z.level(anyInt())).thenReturn(chain);
    
    z.t("trace");
    verify(z).level(eq(LogLevel.TRACE));
    verify(chain).format(eq("trace"));

    z.d("debug");
    verify(z).level(eq(LogLevel.DEBUG));
    verify(chain).format(eq("debug"));

    z.c("conf");
    verify(z).level(eq(LogLevel.CONF));
    verify(chain).format(eq("conf"));

    z.i("info");
    verify(z).level(eq(LogLevel.INFO));
    verify(chain).format(eq("info"));

    z.w("warn");
    verify(z).level(eq(LogLevel.WARN));
    verify(chain).format(eq("warn"));

    z.e("error");
    verify(z).level(eq(LogLevel.ERROR));
    verify(chain).format(eq("error"));
    
    assertTrue(chain.log());
    verify(chain).log();

    verify(z, times(6)).level(anyInt());
    verify(chain, times(1)).entrypoint(notNull());
    verify(chain, times(7))._done();
    verifyNoMoreInteractions(chain);
  }
  
  @Test
  public void testDefaultSummaryAndThrowableMethods() {
    final Zlg z = mock(Zlg.class, Answers.CALLS_REAL_METHODS);
    final LogChain chain = mock(LogChain.class, Answers.CALLS_REAL_METHODS);
    when(chain.format(any())).thenReturn(chain);
    when(chain.entrypoint(any())).thenReturn(chain);
    when(z.level(anyInt())).thenReturn(chain);
    when(chain.threw(isA(Throwable.class))).thenReturn(chain);
    
    final Throwable cause = new Throwable("test");
    
    z.t("trace", cause);
    verify(z).level(eq(LogLevel.TRACE));
    verify(chain).format(eq("trace"));

    z.d("debug", cause);
    verify(z).level(eq(LogLevel.DEBUG));
    verify(chain).format(eq("debug"));

    z.c("conf", cause);
    verify(z).level(eq(LogLevel.CONF));
    verify(chain).format(eq("conf"));

    z.i("info", cause);
    verify(z).level(eq(LogLevel.INFO));
    verify(chain).format(eq("info"));

    z.w("warn", cause);
    verify(z).level(eq(LogLevel.WARN));
    verify(chain).format(eq("warn"));

    z.e("error", cause);
    verify(z).level(eq(LogLevel.ERROR));
    verify(chain).format(eq("error"));
    
    verify(z, times(6)).level(anyInt());
    verify(chain, times(6)).threw(eq(cause));
    verify(chain, times(6))._done();
    verifyNoMoreInteractions(chain);
  }
  
  @Test
  public void testChainWithLambda() {
    final LogChain chain = mock(LogChain.class, Answers.CALLS_REAL_METHODS);
    final Consumer<LogChain> logChainConsumer = Classes.cast(mock(Consumer.class));
    
    chain._done(logChainConsumer);
    verify(logChainConsumer).accept(eq(chain));
    verify(chain)._done();
  }
  
  @Test
  public void testZlgWithLambda() {
    final LogChain chain = mock(LogChain.class, Answers.CALLS_REAL_METHODS);
    when(chain.format(any())).thenReturn(chain);
    when(chain.entrypoint(any())).thenReturn(chain);
    final Zlg z = mock(Zlg.class, Answers.CALLS_REAL_METHODS);
    when(z.level(anyInt())).thenReturn(chain);
    
    final Consumer<LogChain> logChainConsumer = Classes.cast(mock(Consumer.class));
    
    z.t("format", logChainConsumer);
    verify(z).level(eq(LogLevel.TRACE));
    
    z.d("format", logChainConsumer);
    verify(z).level(eq(LogLevel.DEBUG));

    z.c("format", logChainConsumer);
    verify(z).level(eq(LogLevel.CONF));

    z.i("format", logChainConsumer);
    verify(z).level(eq(LogLevel.INFO));

    z.w("format", logChainConsumer);
    verify(z).level(eq(LogLevel.WARN));

    z.e("format", logChainConsumer);
    verify(z).level(eq(LogLevel.ERROR));
    
    verify(logChainConsumer, times(6)).accept(eq(chain));
    verify(chain, times(6))._done();
  }
  
  @Test
  public void testForDeclaringClass() {
    final LogService logService = mock(LogService.class);
    final Zlg zlg = Zlg.forDeclaringClass().withConfigService(new LogConfig().withLogService(logService)).get();
    assertNotNull(zlg);
    verify(logService).get(eq(ZlgTest.class.getName()));
  }
}
