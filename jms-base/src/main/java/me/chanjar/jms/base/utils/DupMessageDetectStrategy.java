package me.chanjar.jms.base.utils;

import javax.jms.JMSException;
import javax.jms.Message;

/**
 * 消息重发检测策略 <br>
 * https://activemq.apache.org/artemis/docs/1.3.0/duplicate-detection.html#using-duplicate-detection-for-message-sending <br>
 */
public interface DupMessageDetectStrategy {

  void setId(Message message, Object payload) throws JMSException;
}
