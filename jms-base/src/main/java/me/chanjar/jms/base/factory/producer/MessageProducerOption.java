package me.chanjar.jms.base.factory.producer;

import javax.jms.DeliveryMode;
import javax.jms.Message;
import javax.jms.MessageProducer;

public class MessageProducerOption {

  private long timeToLive = Message.DEFAULT_TIME_TO_LIVE;

  private int deliveryMode = DeliveryMode.NON_PERSISTENT;

  private int priority = Message.DEFAULT_PRIORITY;

  private boolean disableMessageId = false;

  private boolean disableMessageTimestamp = false;

  private long deliveryDelay = Message.DEFAULT_DELIVERY_DELAY;

  /**
   * @param timeToLive
   * @see MessageProducer#setTimeToLive(long)
   */
  public void setTimeToLive(long timeToLive) {
    this.timeToLive = timeToLive;
  }

  /**
   * Default - {@link DeliveryMode#NON_PERSISTENT}
   *
   * @param deliveryMode
   * @see MessageProducer#setDeliveryMode(int)
   */
  public void setDeliveryMode(int deliveryMode) {
    this.deliveryMode = deliveryMode;
  }

  /**
   * @param priority
   * @see MessageProducer#setPriority(int)
   */
  public void setPriority(int priority) {
    this.priority = priority;
  }

  /**
   * @param disableMessageId
   * @see MessageProducer#setDisableMessageID(boolean)
   */
  public void setDisableMessageId(boolean disableMessageId) {
    this.disableMessageId = disableMessageId;
  }

  /**
   * @param disableMessageTimestamp
   * @see MessageProducer#setDisableMessageTimestamp(boolean)
   */
  public void setDisableMessageTimestamp(boolean disableMessageTimestamp) {
    this.disableMessageTimestamp = disableMessageTimestamp;
  }

  /**
   * @param deliveryDelay
   * @see MessageProducer#setDeliveryDelay(long)
   */
  public void setDeliveryDelay(long deliveryDelay) {
    this.deliveryDelay = deliveryDelay;
  }

  public long getTimeToLive() {
    return timeToLive;
  }

  public int getDeliveryMode() {
    return deliveryMode;
  }

  public int getPriority() {
    return priority;
  }

  public boolean isDisableMessageId() {
    return disableMessageId;
  }

  public boolean isDisableMessageTimestamp() {
    return disableMessageTimestamp;
  }

  public long getDeliveryDelay() {
    return deliveryDelay;
  }
}
