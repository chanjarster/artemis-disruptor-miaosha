package me.chanjar.jms.base.lifecycle;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.util.Assert;

import javax.jms.Connection;
import javax.jms.JMSException;

public class ConnectionLifeCycleContainer implements SmartLifecycle {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionLifeCycleContainer.class);

  private volatile boolean running;

  private final Object monitor = new Object();

  private final Connection connection;

  public ConnectionLifeCycleContainer(Connection connection) {
    Assert.notNull(connection, "Connection must not be null");
    this.connection = connection;
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

    synchronized (this.monitor) {
      if (!this.running) {
        try {

          this.connection.start();
          LOGGER.info("Start connection {}", this.connection.toString());

        } catch (Exception e) {

          LOGGER.error("Error happened when starting connection: {}", this.connection.toString());
          LOGGER.error(ExceptionUtils.getStackTrace(e));

        } finally {

          this.running = false;

        }
      }
    }

  }

  @Override
  public void stop() {

    synchronized (this.monitor) {

      if (this.running) {

        try {

          this.connection.stop();
          this.connection.close();
          this.running = false;

          LOGGER.info("Close connection {}", this.connection.toString());

          monitor.wait(1000L);

        } catch (JMSException e) {

          LOGGER.error("Error happened when closing connection: {}", this.connection.toString());
          throw new JmsConnectionCloseException(e);

        } catch (InterruptedException e) {

          LOGGER.error("Error happened when closing connection: {}", this.connection.toString());
          throw new JmsConnectionCloseException(e);

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
    return JmsObjectLifecycleOrderConstants.CONNECTION;
  }

  public static class JmsConnectionCloseException extends RuntimeException {
    private static final long serialVersionUID = -5707901053880272357L;

    public JmsConnectionCloseException(Throwable cause) {
      super(cause);
    }
  }

}
