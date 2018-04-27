package com.obsidiandynamics.zerolog;

import java.util.*;
import java.util.logging.*;

/**
 *  Bridges a {@link java.util.logging.Logger} to Zerolog.
 */
public final class JulZlgBridge {
  /** Stashed handlers that have been temporarily uninstalled. */
  private static final Set<Handler> handlerStash = new HashSet<>();
  
  /**
   *  Determines whether at least one bridge is currently installed.
   *  
   *  @return True if a bridge is installed.
   */
  public static boolean isInstalled() {
    return Arrays.stream(getRootLogger().getHandlers()).filter(ZlgHandler.class::isInstance).findFirst().isPresent();
  }
  
  /**
   *  Installs a bridge to a specific {@link Zlg} instance.
   *  
   *  @param zlg The logger instance to bridge to.
   */
  public static void install(Zlg zlg) {
    getRootLogger().addHandler(new ZlgHandler(zlg));
  }
  
  /**
   *  Installs the given set of handlers into the root logger.
   *  
   *  @param handlers The handlers to install.
   */
  public static void installAllHandlers(Set<Handler> handlers) {
    final Logger root = getRootLogger();
    handlers.forEach(root::addHandler);
  }
  
  /**
   *  Uninstalls all bridge instances. 
   */
  public static void uninstall() {
    final Logger root = getRootLogger();
    Arrays.stream(root.getHandlers()).filter(ZlgHandler.class::isInstance).forEach(root::removeHandler);
  }
  
  /**
   *  Uninstalls all handlers from the root logger, returning the set of uninstalled handlers.
   *  
   *  @return The handlers that have been uninstalled.
   */
  public static Set<Handler> uninstallAllHandlers() {
    final Logger root = getRootLogger();
    final Set<Handler> stash = new HashSet<>();
    Arrays.stream(root.getHandlers()).forEach(handler -> {
      stash.add(handler);
      root.removeHandler(handler);
    });
    return stash;
  }
  
  /**
   *  Uninstalls all handlers from the root logger and stashes these handlers internally.
   */
  public static void stashAllHandlers() {
    handlerStash.addAll(uninstallAllHandlers());
  }
  
  /**
   *  Restores all handlers from the internal stash by installing them into the root handler.
   */
  public static void unstashAllHandlers() {
    installAllHandlers(handlerStash);
    handlerStash.clear();
  }
  
  /**
   *  Obtains the root {@link Logger} instance.
   *  
   *  @return The root logger.
   */
  public static Logger getRootLogger() {
    return LogManager.getLogManager().getLogger("");
  }
  
  private JulZlgBridge() {}
}
