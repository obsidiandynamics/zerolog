package com.obsidiandynamics.zerolog.util;

/**
 *  Utilities for working with the call stack.
 */
public final class Stacks extends SecurityManager {
  private static final Stacks instance = new Stacks();
  
  private Stacks() {}
  
  /**
   *  Obtains the class at the given stack depth.
   *  
   *  @param depth The stack depth.
   *  @return The {@link Class} at the given depth.
   */
  public static Class<?> classForDepth(int depth) {
    return instance.getClassContext()[depth];
  }
  
  /**
   *  Obtains a {@link StackTraceElement} at the location immediately preceding the
   *  entrypoint.
   *  
   *  @param entrypoint The entrypoint.
   *  @return The preceding {@link StackTraceElement}, or {@code null} if no matching
   *          location could be obtained.
   */
  public static StackTraceElement locate(String entrypoint) {
    final StackTraceElement[] elements = Thread.currentThread().getStackTrace();
    final int index = indexOf(entrypoint, elements);
    return index != -1 && index < elements.length - 1 ? elements[index + 1] : null;
  }
  
  private static int indexOf(String entrypoint, StackTraceElement[] elements) {
    for (int i = 1; i < elements.length; i++) {
      final StackTraceElement element = elements[i];
      if (element.getClassName().equals(entrypoint)) {
        return i;
      }
    }
    return -1;
  }
  
  /**
   *  Formats the location in the form &lt;simple class name&gt;.&lt;method name&gt;:&lt;line number&gt;.<p>
   *  
   *  If a {@code null} element is provided, the resulting location will be returned
   *  as the string {@code "?.?:?"}. 
   *  
   *  @param element The stack trace element; may be {@code null}.
   *  @return The formatted location.
   */
  public static String formatLocation(StackTraceElement element) {
    if (element != null) {
      final String simpleName = getSimpleClassName(element.getClassName());
      return simpleName + "." + element.getMethodName() + ":" + element.getLineNumber();
    } else {
      return "?.?:?";
    }
  }
  
  /**
   *  Returns the simple name of the underlying class as given in the source code or 
   *  an empty string if the underlying class is anonymous. This implementation is
   *  significantly faster than {@link Class#getSimpleName()} as it doesn't require a
   *  class lookup.<p>
   *  
   *  This implementation is a close approximation {@link Class#getSimpleName()}; the 
   *  differences are:<br>
   *  1. This implementation ignores array component types.<br>
   *  2. Extraneous $ characters are mistaken for an inner/local/anonymous class demarcator.<br>
   *  3. This implementation is more lenient, allowing certain invalid characters.<br>
   *  
   *  @param className The class name.
   *  @return The simple name of the given class.
   */
  public static String getSimpleClassName(String className) {
    final int lastDollar = className.lastIndexOf('$');
    if (lastDollar != -1) {
      // inner, local or anonymous class
      final int length = className.length();
      int index = lastDollar + 1;
      
      // skip over any leading digits that follow '$' in anonymous classes
      while (index < length && isAsciiDigit(className.charAt(index))) {
        index++;
      }
      return className.substring(index);
    } else {
      // top-level class
      final int lastDot = className.lastIndexOf('.');
      return lastDot != -1 ? className.substring(lastDot + 1) : className;
    }
  }
  
  /**
   *  Checks if the given character is an ASCII digit. Differs from {@link Character#isDigit(char)}
   *  in that the latter considers the Unicode character set and answers {@code true} to some 
   *  non-ASCII digits.
   * 
   *  @param ch The character to test.
   *  @return True if the character is an ASCII digit.
   */
  private static boolean isAsciiDigit(char ch) {
    return '0' <= ch && ch <= '9';
  }
}
