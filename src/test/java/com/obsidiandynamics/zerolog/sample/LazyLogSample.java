package com.obsidiandynamics.zerolog.sample;

import static java.util.stream.Collectors.*;

import java.util.*;

import com.obsidiandynamics.zerolog.*;

public final class LazyLogSample {
  private static final Zlg zlg = Zlg.forDeclaringClass().get();
  
  public static void logWithSupplier() {
    final List<Integer> numbers = Arrays.asList(0, 1, 2, 3);
    final int index = 4;
    
    if (index >= numbers.size()) {
      zlg.i("invalid index %d, must be 0 < index < %d", z -> z.arg(index).arg(numbers::size));
    }
  }
  
  public static final class Name {
    final String forename;
    final String surname;
    
    Name(String forename, String surname) {
      this.forename = forename;
      this.surname = surname;
    }
  }
  
  public static void logWithDescriber() {
    final List<Name> hackers = Arrays.asList(new Name("Kevin", "Flynn"), 
                                             new Name("Thomas", "Anderson"), 
                                             new Name("Angela", "Bennett"));
    final String surnameToFind = "Smith";
    
    if (! hackers.stream().anyMatch(n -> n.surname.contains(surnameToFind))) {
      zlg.i("%s not found among %s", z -> z.arg(surnameToFind).arg(hackers, LazyLogSample::tokeniseSurnames));
    }
  }
  
  public static List<String> tokeniseSurnames(Collection<Name> names) {
    return names.stream().map(n -> n.forename + " " + n.surname.replaceAll(".", "X")).collect(toList());
  }
  
  public static void main(String[] args) {
    logWithSupplier();
    logWithDescriber();
  }
}
