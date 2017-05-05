package me.chanjar.jms.base.utils;

import me.chanjar.jms.base.msg.MessageDto;
import me.chanjar.jms.base.utils.DupMessageDetectStrategy;

import javax.jms.JMSException;
import javax.jms.Message;

/**
 * 适用于Artemis, {@link MessageDto}的消息重复检测策略 <br>
 * https://activemq.apache.org/artemis/docs/1.3.0/duplicate-detection.html#using-duplicate-detection-for-message-sending <br>
 */
public class ArtemisMessageDtoDupMessageDetectStrategy implements DupMessageDetectStrategy {

  private static final String HDR_DUPLICATE_DETECTION_ID =
      org.apache.activemq.artemis.api.core.Message.HDR_DUPLICATE_DETECTION_ID.toString();

  @Override
  public void setId(Message message, Object payload) throws JMSException {
    message.setStringProperty(HDR_DUPLICATE_DETECTION_ID, ((MessageDto) payload).getId());
  }

}
