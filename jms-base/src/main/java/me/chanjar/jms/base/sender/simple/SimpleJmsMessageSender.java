package me.chanjar.jms.base.sender.simple;

import me.chanjar.jms.base.sender.JmsMessageSender;
import me.chanjar.jms.base.utils.ArtemisMessageDtoDupMessageDetectStrategy;
import me.chanjar.jms.base.msg.MessageDto;
import me.chanjar.jms.base.utils.DupMessageDetectStrategy;
import me.chanjar.jms.base.utils.MessageConvertUtils;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.SimpleMessageConverter;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

public class SimpleJmsMessageSender implements JmsMessageSender {

  private Session session;

  private MessageProducer messageProducer;

  private MessageConverter messageConverter = new SimpleMessageConverter();

  private DupMessageDetectStrategy dupMessageDetectStrategy = new ArtemisMessageDtoDupMessageDetectStrategy();

  @Override
  public void sendMessage(MessageDto payload) throws JMSException {

    Message message = MessageConvertUtils.toMessage(messageConverter, session, payload);
    dupMessageDetectStrategy.setId(message, payload);
    messageProducer.send(message);

  }

  public void setSession(Session session) {
    this.session = session;
  }

  public void setMessageProducer(MessageProducer messageProducer) {
    this.messageProducer = messageProducer;
  }

  public void setMessageConverter(MessageConverter messageConverter) {
    this.messageConverter = messageConverter;
  }

  public void setDupMessageDetectStrategy(DupMessageDetectStrategy dupMessageDetectStrategy) {
    this.dupMessageDetectStrategy = dupMessageDetectStrategy;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("SimpleJmsMessageSender{");
    sb.append("session=").append(session);
    sb.append(", messageProducer=").append(messageProducer);
    sb.append('}');
    return sb.toString();
  }

}
