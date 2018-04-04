package com.obsidiandynamics.zerolog;

import com.obsidiandynamics.zerolog.Zlg.*;

final class NopLevelChain implements LogChain {
  private static final NopLevelChain instance = new NopLevelChain();
  
  static NopLevelChain getInstance() { return instance; }
  
  private NopLevelChain() {}
  
  @Override
  public LogChain tag(String tag) {
    return this;
  }

  @Override
  public LogChain format(String format) {
    return this;
  }

  @Override
  public LogChain arg(boolean arg) {
    return this;
  }

  @Override
  public LogChain arg(byte arg) {
    return this;
  }

  @Override
  public LogChain arg(char arg) {
    return this;
  }

  @Override
  public LogChain arg(double arg) {
    return this;
  }

  @Override
  public LogChain arg(float arg) {
    return this;
  }

  @Override
  public LogChain arg(int arg) {
    return this;
  }

  @Override
  public LogChain arg(long arg) {
    return this;
  }

  @Override
  public LogChain arg(short arg) {
    return this;
  }

  @Override
  public LogChain arg(Object arg) {
    return this;
  }

  @Override
  public LogChain stack(Throwable throwable) {
    return this;
  }

  @Override
  public void log() {}
}
