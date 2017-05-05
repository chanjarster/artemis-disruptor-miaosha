package me.chanjar.jms.base.lifecycle;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.util.Assert;

import javax.jms.Session;

public class SessionLifeCycleContainer implements SmartLifecycle {

  private static final Logger LOGGER = LoggerFactory.getLogger(SessionLifeCycleContainer.class);

  private volatile boolean running;

  private final Object monitor = new Object();

  private final Session session;

  public SessionLifeCycleContainer(Session session) {
    Assert.notNull(session, "Session must not be null");
    this.session = session;
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
    LOGGER.info("Manage life cycle of " + this.session.toString());
    this.running = true;
  }

  @Override
  public void stop() {

    synchronized (this.monitor) {

      if (this.running) {

        try {

          this.session.close();
          LOGGER.info("Close session {}", this.session.toString());

        } catch (Exception e) {

          LOGGER.error("Error happened when closing session: {}", this.session.toString());
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
    return JmsObjectLifecycleOrderConstants.SESSION;
  }

}
