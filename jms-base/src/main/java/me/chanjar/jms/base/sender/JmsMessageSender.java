package me.chanjar.jms.base.sender;

import me.chanjar.jms.base.msg.MessageDto;

import javax.jms.JMSException;

/**
 * JMS消息发送器
 */
public interface JmsMessageSender {

  /**
   * 发送Jms消息
   *
   * @param payload 消息主体
   * @throws JMSException
   */
  void sendMessage(MessageDto payload) throws JMSException;

}
