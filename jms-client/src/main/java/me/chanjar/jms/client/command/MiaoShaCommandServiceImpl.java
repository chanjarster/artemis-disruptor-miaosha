package me.chanjar.jms.client.command;

import me.chanjar.jms.base.sender.JmsMessageSender;
import me.chanjar.jms.msg.RequestDto;
import me.chanjar.jms.msg.ResponseDto;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;

@Component
public class MiaoShaCommandServiceImpl implements MiaoShaCommandService {

  private static final Logger LOGGER = LoggerFactory.getLogger(MiaoShaCommandService.class);

  private ResponseCache responseCache;

  private JmsMessageSender jmsMessageSender;

  @Override
  public String doRequest(RequestDto requestDto) {
    try {
      jmsMessageSender.sendMessage(requestDto);
      return requestDto.getId();
    } catch (JMSException e) {
      LOGGER.error(ExceptionUtils.getStackTrace(e));
      throw new RuntimeException(e);
    }
  }

  @Override
  public ResponseDto getResponse(String requestId) {
    return responseCache.getAndRemove(requestId);
  }

  @Autowired
  public void setResponseCache(ResponseCache responseCache) {
    this.responseCache = responseCache;
  }

  @Autowired
  @Qualifier("requestMessageSender")
  public void setJmsMessageSender(JmsMessageSender jmsMessageSender) {
    this.jmsMessageSender = jmsMessageSender;
  }


}
