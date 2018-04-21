package com.obsidiandynamics.zerolog;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.function.*;

import org.junit.*;

import com.obsidiandynamics.func.*;
import com.obsidiandynamics.zerolog.Zlg.*;
import com.obsidiandynamics.zerolog.ZlgImpl.*;

public final class ZlgImplTest {
  private static class LogMocks implements ConfigService {
    private final int baseLevel;
    final LogService service = mock(LogService.class);
    final LogTarget target = mock(LogTarget.class);
    Object[] argv;
    
    LogMocks(int baseLevel, int localLevel) {
      this.baseLevel = baseLevel;
      
      when(service.get(any())).thenReturn(target);
      when(target.isEnabled(anyInt())).thenReturn(true);
      
      doAnswer(invocation -> {
        final int argc = invocation.getArgument(3);
        final Object[] argv = invocation.getArgument(4);
        this.argv = new Object[argc];
        System.arraycopy(argv, 0, this.argv, 0, argc);
        return null;
      }).when(target).log(anyInt(), any(), any(), anyInt(), any(), any());
      
      for (LogLevel.Enum level : LogLevel.Enum.values()) {
        when(target.isEnabled(eq(level.getLevel()))).thenReturn(level.getLevel() >= localLevel);
      }
    }
    
    @Override
    public LogConfig get() {
      return new LogConfig().withBaseLevel(baseLevel).withLogService(service);
    }
  }

  /**
   *  Tests logging at debug level, which should be disabled at the base level.
   */
  @Test
  public void testLogDebug() {
    final LogMocks mocks = new LogMocks(LogLevel.CONF, LogLevel.INFO);
    final Zlg zlg = Zlg.forName("test").withConfigService(mocks).get();
    
    zlg.d("message", z -> z.arg(100).arg(200).arg((Object) null).tag("tag"));
    verifyNoMoreInteractions(mocks.target);
  }

  /**
   *  Tests logging at configuration level, which should be enabled at the base level but disabled locally.
   */
  @Test
  public void testLogConf() {
    final LogMocks mocks = new LogMocks(LogLevel.CONF, LogLevel.INFO);
    final Zlg zlg = Zlg.forName("test").withConfigService(mocks).get();
    
    zlg.c("message", z -> z.arg(100).arg(200).arg((Object) null).tag("tag"));
    verify(mocks.target).isEnabled(eq(LogLevel.CONF));
    verifyNoMoreInteractions(mocks.target);
  }

  /**
   *  Tests logging at info level, which should be enabled.
   */
  @Test
  public void testLogInfoWithoutArgsTagOrStack() {
    final LogMocks mocks = new LogMocks(LogLevel.CONF, LogLevel.INFO);
    final Zlg zlg = Zlg.forName("test").withConfigService(mocks).get();
    final String format = "message";
    
    zlg.i(format);
    verify(mocks.target).isEnabled(eq(LogLevel.INFO));
    verify(mocks.target).log(eq(LogLevel.INFO), isNull(), eq(format), eq(0), any(), isNull());
    assertArrayEquals(new Object[] {}, mocks.argv);
  }

  /**
   *  Tests logging at info level, which should be enabled. This test includes message arguments, 
   *  as well as the tag and the exception elements.
   */
  @Test
  public void testLogInfoWithTagAndException() {
    final LogMocks mocks = new LogMocks(LogLevel.CONF, LogLevel.INFO);
    final Zlg zlg = Zlg.forName("test").withConfigService(mocks).get();
    final String format = "message %d, %d";
    final Exception exception = new Exception("simulated");
    
    zlg.i(format, z -> z.arg(100).arg(200).tag("tag").threw(exception));
    verify(mocks.target).isEnabled(eq(LogLevel.INFO));
    verify(mocks.target).log(eq(LogLevel.INFO), eq("tag"), eq(format), eq(2), any(), eq(exception));
    assertArrayEquals(new Object[] {100, 200}, mocks.argv);
  }

  /**
   *  Tests logging at info level with all argument types.
   */
  @Test
  public void testLogInfoWithAllArgTypes() {
    final LogMocks mocks = new LogMocks(LogLevel.CONF, LogLevel.INFO);
    final Zlg zlg = Zlg.forName("test").withConfigService(mocks).get();
    final String format = "message";
    
    zlg
    .level(LogLevel.INFO)
    .format(format)
    .arg(true)
    .arg((byte) 0x01)
    .arg('c')
    .arg(3.14d)
    .arg(3.14f)
    .arg(42)
    .arg(42L)
    .arg("string")
    .arg((short) 42)
    .done();
    verify(mocks.target).isEnabled(eq(LogLevel.INFO));
    verify(mocks.target).log(eq(LogLevel.INFO), isNull(), eq(format), eq(9), any(), isNull());
    assertArrayEquals(new Object[] {true, (byte) 0x01, 'c', 3.14d, 3.14f, 42, 42L, "string", (short) 42}, mocks.argv);
  }

  /**
   *  Tests logging at info level with a null object.
   */
  @Test
  public void testLogInfoWithNullArg() {
    final LogMocks mocks = new LogMocks(LogLevel.CONF, LogLevel.INFO);
    final Zlg zlg = Zlg.forName("test").withConfigService(mocks).get();
    final String format = "message %s";
    
    zlg
    .level(LogLevel.INFO)
    .format(format)
    .arg((Object) null)
    .done();
    verify(mocks.target).isEnabled(eq(LogLevel.INFO));
    verify(mocks.target).log(eq(LogLevel.INFO), isNull(), eq(format), eq(1), any(), isNull());
    assertArrayEquals(new Object[] {null}, mocks.argv);
  }
  
  @Test
  public void testLogAllLevels() {
    final LogMocks mocks = new LogMocks(LogLevel.TRACE, LogLevel.TRACE);
    final Zlg zlg = Zlg.forName("test").withConfigService(mocks).get();
    
    for (LogLevel.Enum level : LogLevel.Enum.values()) {
      if (level.getLevel() != LogLevel.OFF) {
        zlg.level(level.getLevel()).format("format").done();
        verify(mocks.target).log(eq(level.getLevel()), isNull(), eq("format"), eq(0), any(), isNull());
      }
    }
  }
  
  @Test
  public void testLogOffAllLevels() {
    final LogMocks mocks = new LogMocks(LogLevel.OFF, LogLevel.TRACE);
    final Zlg zlg = Zlg.forName("test").withConfigService(mocks).get();
    
    for (LogLevel.Enum level : LogLevel.Enum.values()) {
      if (level.getLevel() != LogLevel.OFF) {
        zlg.level(level.getLevel()).format("format").done();
      }
    }
    verifyNoMoreInteractions(mocks.target);
  }
  
  @Test(expected=DuplicateValueException.class)
  public void testDuplicateTag() {
    final Zlg zlg = Zlg.forName("test").withConfigService(new LogConfig()).get();
    zlg.i("message", z -> z.tag("tag").tag("tag"));
  }
  
  @Test(expected=DuplicateValueException.class)
  public void testDuplicateFormat() {
    final Zlg zlg = Zlg.forName("test").withConfigService(new LogConfig()).get();
    zlg.level(LogLevel.INFO).format("message").format("message");
  }
  
  @Test(expected=DuplicateValueException.class)
  public void testDuplicateStack() {
    final Zlg zlg = Zlg.forName("test").withConfigService(new LogConfig()).get();
    final Exception exception = new Exception("simulated");
    zlg.i("message", z -> z.threw(exception).threw(exception));
  }
  
  @Test(expected=MissingValueException.class)
  public void testMissingFormat() {
    final Zlg zlg = Zlg.forName("test").withConfigService(new LogConfig()).get();
    zlg.level(LogLevel.INFO).done();
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testLogOff() {
    final Zlg zlg = Zlg.forName("test").withConfigService(new LogConfig()).get();
    zlg.level(LogLevel.OFF);
  }
  
  @Test
  public void testSufficientArgs() {
    final Zlg zlg = Zlg.forName("test").withConfigService(new LogConfig()).get();
    final LogChain chain = zlg.level(LogLevel.INFO).format("format");
    // should be able to add up to MAX_ARGS arguments without failure
    for (int i = 0; i < LogChain.MAX_ARGS; i++) {
      chain.arg(i);
    }
  }
  
  @Test(expected=TooManyArgsException.class)
  public void testTooManyArgs() {
    final Zlg zlg = Zlg.forName("test").withConfigService(new LogConfig()).get();
    final LogChain chain = zlg.level(LogLevel.INFO).format("format");
    // should be able to add up to MAX_ARGS arguments
    for (int i = 0; i < LogChain.MAX_ARGS; i++) {
      chain.arg(i);
    }
    
    // expect failure beyond any additional args
    chain.arg(LogChain.MAX_ARGS);
  }
  
  @Test
  public void testLazy() {
    final LogMocks mocks = new LogMocks(LogLevel.TRACE, LogLevel.TRACE);
    final Zlg zlg = Zlg.forName("test").withConfigService(mocks).get();
    
    final BooleanSupplier booleanSupplier = mock(BooleanSupplier.class);
    final DoubleSupplier doubleSupplier = mock(DoubleSupplier.class);
    final IntSupplier intSupplier = mock(IntSupplier.class);
    final LongSupplier longSupplier = mock(LongSupplier.class);
    final Supplier<String> stringSupplier = Classes.cast(mock(Supplier.class));
    when(stringSupplier.get()).thenReturn("suppliedString");
    
    zlg
    .level(LogLevel.INFO)
    .format("format")
    .arg(booleanSupplier)
    .arg(doubleSupplier)
    .arg(intSupplier)
    .arg(longSupplier)
    .arg(stringSupplier)
    .done();
    
    verify(booleanSupplier).getAsBoolean();
    verify(doubleSupplier).getAsDouble();
    verify(intSupplier).getAsInt();
    verify(longSupplier).getAsLong();
    verify(stringSupplier).get();
  }
}
