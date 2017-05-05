package me.chanjar.jms.base.lifecycle;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.util.Assert;

import javax.jms.MessageConsumer;

public class MessageConsumerLifeCycleContainer implements SmartLifecycle {

  private static final Logger LOGGER = LoggerFactory.getLogger(MessageConsumerLifeCycleContainer.class);

  private volatile boolean running;

  private final Object monitor = new Object();

  private final MessageConsumer messageConsumer;

  public MessageConsumerLifeCycleContainer(MessageConsumer messageConsumer) {
    Assert.notNull(messageConsumer, "MessageConsumer must not be null");
    this.messageConsumer = messageConsumer;
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
    LOGGER.info("Manage life cycle of " + this.messageConsumer.toString());
    this.running = true;
  }

  @Override
  public void stop() {

    synchronized (this.monitor) {

      if (this.running) {

        try {

          this.messageConsumer.close();
          LOGGER.info("Close messageConsumer {}", this.messageConsumer.toString());

        } catch (Exception e) {

          LOGGER.error("Error happened when closing messageConsumer: {}", this.messageConsumer.toString());
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
    return JmsObjectLifecycleOrderConstants.PRODUCER_CONSUMER;
  }

}
