package me.chanjar.jms.client.command;

import me.chanjar.jms.base.utils.MessageConvertUtils;
import me.chanjar.jms.msg.ResponseDto;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.SimpleMessageConverter;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

public class ResponseJmsMessageListener implements MessageListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(ResponseJmsMessageListener.class);

  private ResponseCache responseCache;

  private MessageConverter messageConverter = new SimpleMessageConverter();

  @Override
  public void onMessage(Message message) {
    try {
      ResponseDto responseDto = MessageConvertUtils.fromMessage(messageConverter, message);
      responseCache.put(responseDto);
    } catch (JMSException e) {
      LOGGER.error(ExceptionUtils.getStackTrace(e));
      throw new RuntimeException(e);
    }
  }

  public void setResponseCache(ResponseCache responseCache) {
    this.responseCache = responseCache;
  }

}
