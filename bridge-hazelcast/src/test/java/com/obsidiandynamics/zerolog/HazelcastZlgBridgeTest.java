package com.obsidiandynamics.zerolog;

import static org.junit.Assert.*;

import java.util.concurrent.locks.*;

import org.junit.*;

import com.hazelcast.logging.*;
import com.obsidiandynamics.assertion.*;

public final class HazelcastZlgBridgeTest {
  private static final String PROPERTY_KEY = "hazelcast.logging.class";
  
  private static final Lock lock = new ReentrantLock();
  
  private String factoryClass;
  
  @Before
  public void before() {
    lock.lock();
    saveFactory();
  }
  
  private void saveFactory() {
    factoryClass = System.getProperty(PROPERTY_KEY);
  }
  
  @After
  public void after() {
    restoreFactory();
    lock.unlock();
  }
  
  private void restoreFactory() {
    if (factoryClass != null) {
      System.setProperty(PROPERTY_KEY, factoryClass);
      factoryClass = null;
    } else {
      System.getProperties().remove(PROPERTY_KEY);
    }
  }
  
  @Test
  public void testConformance() {
    Assertions.assertUtilityClassWellDefined(HazelcastZlgBridge.class);
  }

  @Test
  public void testInstallation() {
    assertFalse(HazelcastZlgBridge.isInstalled());
    
    HazelcastZlgBridge.install();
    assertTrue(HazelcastZlgBridge.isInstalled());
    
    HazelcastZlgBridge.uninstall();
    assertFalse(HazelcastZlgBridge.isInstalled());

    // repeated uninstall should do nothing
    HazelcastZlgBridge.uninstall();
    assertFalse(HazelcastZlgBridge.isInstalled());
  }
  
  @Test
  public void testLog() {
    final MockLogTarget target = new MockLogTarget();
    
    HazelcastZlgBridge.install(new LogConfig().withLogService(target.logService()));
    assertTrue(HazelcastZlgBridge.isInstalled());
    
    final LoggerFactory loggerFactory = Logger.newLoggerFactory("irrelevant-since-the-property-is-set");
    final ILogger logger = loggerFactory.getLogger(HazelcastZlgBridgeTest.class.getName());
    
    logger.info("Test message");
    target.entries().assertCount(1);
    target.entries().forLevel(LogLevel.INFO).withMessage("Test message").assertCount(1);
    
    HazelcastZlgBridge.uninstall();
  }
}
