package me.chanjar.jms.base.lifecycle;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.util.Assert;

import javax.jms.ConnectionFactory;

public class ConnectionFactoryLifeCycleContainer implements SmartLifecycle {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionLifeCycleContainer.class);

  private volatile boolean running;

  private final Object monitor = new Object();

  private final ConnectionFactory connectionFactory;

  public ConnectionFactoryLifeCycleContainer(ConnectionFactory connectionFactory) {
    Assert.notNull(connectionFactory, "connectionFactory must not be null");
    this.connectionFactory = connectionFactory;
  }

  @Override
  public boolean isAutoStartup() {
    return true;
  }

  @Override
  public void stop(Runnable callback) {
    stop();
    callback.run();
  }

  @Override
  public void start() {

    LOGGER.info("Manage life cycle of " + this.connectionFactory.toString());
    this.running = true;

  }

  @Override
  public void stop() {

    synchronized (this.monitor) {

      if (this.running) {

        try {

          if (this.connectionFactory instanceof AutoCloseable) {
            ((AutoCloseable) this.connectionFactory).close();
            LOGGER.info("Close connection factory {}", this.connectionFactory.toString());
          } else {
            LOGGER.info("{} doesn't implements AutoCloseable, won't do close.", this.connectionFactory.toString());
          }

        } catch (Exception e) {

          LOGGER.error("Error happened when closing connection factory: {}", this.connectionFactory.toString());
          LOGGER.error(ExceptionUtils.getStackTrace(e));

        } finally {

          this.running = false;

        }

      }

    }

  }

  @Override
  public boolean isRunning() {
    return this.running;
  }

  @Override
  public int getPhase() {
    return JmsObjectLifecycleOrderConstants.CONNECTION_FACTORY;
  }

}
