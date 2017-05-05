package me.chanjar.jms.base.lifecycle;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.util.Assert;

import javax.jms.MessageProducer;

public class MessageProducerLifeCycleContainer implements SmartLifecycle {

  private static final Logger LOGGER = LoggerFactory.getLogger(MessageProducerLifeCycleContainer.class);

  private volatile boolean running;

  private final Object monitor = new Object();

  private final MessageProducer messageProducer;

  public MessageProducerLifeCycleContainer(MessageProducer messageProducer) {
    Assert.notNull(messageProducer, "MessageProducer must not be null");
    this.messageProducer = messageProducer;
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
    LOGGER.info("Manage life cycle of " + this.messageProducer.toString());
    this.running = true;
  }

  @Override
  public void stop() {

    synchronized (this.monitor) {

      if (this.running) {

        try {

          this.messageProducer.close();
          LOGGER.info("Close messageProducer {}", this.messageProducer.toString());

        } catch (Exception e) {

          LOGGER.error("Error happened when closing messageProducer: {}", this.messageProducer.toString());
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
