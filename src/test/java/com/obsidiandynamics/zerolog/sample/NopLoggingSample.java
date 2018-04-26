package com.obsidiandynamics.zerolog.sample;

import com.obsidiandynamics.zerolog.*;

public final class NopLoggingSample {
  public static void main(String[] args) {
    final Zlg zlg = Zlg.nop();
    zlg.i("Hush");
  }
}
