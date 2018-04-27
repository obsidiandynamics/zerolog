package com.obsidiandynamics.zerolog;

import static org.junit.Assert.*;

import java.util.*;
import java.util.concurrent.locks.*;
import java.util.logging.*;

import org.junit.*;

import com.obsidiandynamics.assertion.*;

public final class JulZlgBridgeTest {
  private static final Lock lock = new ReentrantLock();
  
  private final List<Handler> rootHandlers = new ArrayList<>();
  
  @Before
  public void before() {
    lock.lock();
    saveHandlers();
  }
  
  private void saveHandlers() {
    Arrays.stream(JulZlgBridge.getRootLogger().getHandlers()).forEach(rootHandlers::add);
  }
  
  @After
  public void after() {
    restoreHandlers();
    lock.unlock();
  }
  
  private void restoreHandlers() {
    final Logger root = JulZlgBridge.getRootLogger();
    Arrays.stream(JulZlgBridge.getRootLogger().getHandlers()).forEach(root::removeHandler);
    rootHandlers.forEach(root::addHandler);
  }
  
  @Test
  public void testConformance() {
    Assertions.assertUtilityClassWellDefined(JulZlgBridge.class);
  }
  
  @Test
  public void testIsInstalled() {
    assertFalse(JulZlgBridge.isInstalled());
    
    JulZlgBridge.install(Zlg.nop());
    assertTrue(JulZlgBridge.isInstalled());
    
    JulZlgBridge.uninstall();
    assertFalse(JulZlgBridge.isInstalled());
  }
  
  @Test
  public void testUninstallAndInstallAll() {
    final int countBefore = JulZlgBridge.getRootLogger().getHandlers().length;
    assertTrue(countBefore > 0);
    
    final Set<Handler> uninstalled = JulZlgBridge.uninstallAllHandlers();
    assertEquals(countBefore, uninstalled.size());
    assertEquals(0, JulZlgBridge.getRootLogger().getHandlers().length);
    
    JulZlgBridge.installAllHandlers(uninstalled);
    assertEquals(countBefore, JulZlgBridge.getRootLogger().getHandlers().length);
  }
  
  @Test
  public void testStashUnstash() {
    final int countBefore = JulZlgBridge.getRootLogger().getHandlers().length;
    assertTrue(countBefore > 0);
    
    JulZlgBridge.stashAllHandlers();
    assertEquals(0, JulZlgBridge.getRootLogger().getHandlers().length);
    
    JulZlgBridge.unstashAllHandlers();
    assertEquals(countBefore, JulZlgBridge.getRootLogger().getHandlers().length);
  }
  
  @Test
  public void testLog() {
    JulZlgBridge.stashAllHandlers();
    assertEquals(0, JulZlgBridge.getRootLogger().getHandlers().length);
    
    final MockLogTarget target = new MockLogTarget();
    final Zlg zlg = target.logger();
    
    JulZlgBridge.install(zlg);
    assertTrue(JulZlgBridge.isInstalled());
    assertEquals(1, JulZlgBridge.getRootLogger().getHandlers().length);
    
    final Logger logger = Logger.getLogger("logger");
    logger.setLevel(Level.FINEST);
    
    logger.log(Level.FINE, "Pi is {0}", 3.14);
    target.entries().assertCount(1);
    target.entries().forLevel(LogLevel.DEBUG).withMessage("Pi is 3.14").assertCount(1);
        
    JulZlgBridge.unstashAllHandlers();
  }
}
