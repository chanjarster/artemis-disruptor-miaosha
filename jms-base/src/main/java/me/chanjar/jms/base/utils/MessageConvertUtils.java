package me.chanjar.jms.base.utils;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;


public abstract class MessageConvertUtils {

  private MessageConvertUtils() {
    // singleton
  }

  /**
   * 把对象转换成{@link Message}
   *
   * @param jmsTemplate
   * @param session
   * @param payload
   * @return
   * @throws JMSException
   */
  public static Message toMessage(JmsTemplate jmsTemplate, Session session, Object payload) throws JMSException {
    return toMessage(jmsTemplate.getMessageConverter(), session, payload);
  }

  /**
   * 把对象转换成{@link Message}
   *
   * @param messageConverter
   * @param session
   * @param payload
   * @return
   * @throws JMSException
   */
  public static Message toMessage(MessageConverter messageConverter, Session session, Object payload)
      throws JMSException {
    return messageConverter.toMessage(payload, session);
  }

  /**
   * 把{@link Message}转换成对象
   *
   * @param jmsTemplate
   * @param message
   * @param <T>
   * @return
   * @throws JMSException
   */
  public static <T> T fromMessage(JmsTemplate jmsTemplate, Message message) throws JMSException {
    return fromMessage(jmsTemplate.getMessageConverter(), message);
  }

  /**
   * 把{@link Message}转换成对象
   *
   * @param messageConverter
   * @param message
   * @param <T>
   * @return
   * @throws JMSException
   */
  public static <T> T fromMessage(MessageConverter messageConverter, Message message) throws JMSException {
    return (T) messageConverter.fromMessage(message);
  }

}
