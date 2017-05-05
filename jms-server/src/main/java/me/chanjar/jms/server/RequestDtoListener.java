package me.chanjar.jms.server;

import me.chanjar.jms.base.utils.MessageConvertUtils;
import me.chanjar.jms.msg.RequestDto;
import me.chanjar.jms.server.request.RequestDtoEventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.SimpleMessageConverter;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

public class RequestDtoListener implements MessageListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(RequestDtoListener.class);

  private RequestDtoEventProducer requestDtoEventProducer;

  private MessageConverter messageConverter = new SimpleMessageConverter();

  @Override
  public void onMessage(Message message) {
    if (!(message instanceof ObjectMessage)) {
      LOGGER.error("Not ObjectMessage but actually {}", message.getClass().getSimpleName());
      return;
    }
    try {
      RequestDto requestDto = MessageConvertUtils.fromMessage(messageConverter, message);
      requestDtoEventProducer.onData(requestDto);
    } catch (JMSException e) {
      LOGGER.error("Error when onMessage", e);
      throw new RuntimeException(e);
    }
  }

  public void setRequestDtoEventProducer(RequestDtoEventProducer requestDtoEventProducer) {
    this.requestDtoEventProducer = requestDtoEventProducer;
  }

  public void setMessageConverter(MessageConverter messageConverter) {
    this.messageConverter = messageConverter;
  }


}
