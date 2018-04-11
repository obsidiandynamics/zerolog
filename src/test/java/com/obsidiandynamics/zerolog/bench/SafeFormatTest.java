package com.obsidiandynamics.zerolog.bench;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.*;

import com.obsidiandynamics.assertion.*;
import com.obsidiandynamics.zerolog.*;

public final class SafeFormatTest {
  @Test
  public void testConformance() {
    Assertions.assertUtilityClassWellDefined(SafeFormat.class);
  }

  @Test
  public void testFormatSuccess() {
    assertEquals("pi is 3.14", SafeFormat.format("pi is %.2f", 1, new Object[] {Math.PI}));
  }

  @Test
  public void testFormatError() {
    final String format = "pi is %2d";
    final Object[] args = { Math.PI };
    final String out = SafeFormat.format(format, 1, new Object[] {Math.PI});
    assertTrue("out=" + out, out.contains("WARNING -"));
    assertTrue("out=" + out, out.contains(format));
    assertTrue("out=" + out, out.contains(Arrays.asList(args).toString()));
    assertTrue("out=" + out, out.contains(IllegalFormatConversionException.class.getName()));
  }
}
