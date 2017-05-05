package me.chanjar.jms.base.factory.producer;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

public abstract class MessageProducerFactory {
  private MessageProducerFactory() {

  }

  public static MessageProducer createMessageProducer(
      Session session,
      Destination destination,
      MessageProducerOption producerOption) throws JMSException {

    MessageProducer producer = session.createProducer(destination);
    producer.setDeliveryDelay(producerOption.getDeliveryDelay());
    producer.setDeliveryMode(producerOption.getDeliveryMode());
    producer.setDisableMessageTimestamp(producerOption.isDisableMessageTimestamp());
    producer.setDisableMessageID(producerOption.isDisableMessageId());
    producer.setPriority(producerOption.getPriority());
    producer.setTimeToLive(producerOption.getTimeToLive());

    return producer;

  }

}
