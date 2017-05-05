package me.chanjar.jms.base.sender.simple;

import me.chanjar.jms.base.utils.DupMessageDetectStrategy;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

public abstract class SimpleJmsMessageSenderFactory {

  private SimpleJmsMessageSenderFactory() {
  }

  /**
   * @param session
   * @param messageProducer
   * @param dupMessageDetectStrategy
   * @return
   * @throws JMSException
   */
  public static SimpleJmsMessageSender create(
      Session session,
      MessageProducer messageProducer,
      DupMessageDetectStrategy dupMessageDetectStrategy
  ) throws JMSException {

    SimpleJmsMessageSender messageSender = new SimpleJmsMessageSender();
    messageSender.setMessageProducer(messageProducer);
    messageSender.setSession(session);
    if (dupMessageDetectStrategy != null) {
      messageSender.setDupMessageDetectStrategy(dupMessageDetectStrategy);
    }

    return messageSender;

  }

}
