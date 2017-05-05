package me.chanjar.jms.base.factory.consumer;

import javax.jms.*;

public abstract class MessageConsumerFactory {
  private MessageConsumerFactory() {

  }

  /**
   * @param session
   * @param destination
   * @param option
   * @param messageListener 可选
   * @return
   * @throws JMSException
   */
  public static MessageConsumer createMessageConsumer(
      Session session,
      Destination destination,
      MessageConsumerOption option,
      MessageListener messageListener) throws JMSException {

    MessageConsumer messageConsumer;
    if (option.getMessageSelector() != null && option.getNoLocal() != null) {
      messageConsumer = session.createConsumer(destination, option.getMessageSelector(), option.getNoLocal());
    } else if (option.getMessageSelector() != null) {
      messageConsumer = session.createConsumer(destination, option.getMessageSelector());
    } else {
      messageConsumer = session.createConsumer(destination);
    }
    if (messageListener != null) {
      messageConsumer.setMessageListener(messageListener);
    }
    return messageConsumer;

  }

}
