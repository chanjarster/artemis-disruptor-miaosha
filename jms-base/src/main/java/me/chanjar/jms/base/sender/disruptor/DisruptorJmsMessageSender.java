package me.chanjar.jms.base.sender.disruptor;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.dsl.Disruptor;
import me.chanjar.jms.base.msg.MessageDto;
import me.chanjar.jms.base.sender.JmsMessageSender;
import me.chanjar.jms.base.utils.ArtemisMessageDtoDupMessageDetectStrategy;
import me.chanjar.jms.base.utils.DupMessageDetectStrategy;
import me.chanjar.jms.base.utils.MessageConvertUtils;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.SimpleMessageConverter;

import javax.jms.*;

/**
 * <p>
 * 内部使用Disruptor来单线程的调用{@link Session}和{@link MessageProducer}的方法来发送消息。
 * </p>
 * <p>
 * 因为使用Disruptor的缘故, 消息的发送是异步的, 也就是说{@link #sendMessage(MessageDto)}方法:
 * <ol>
 * <li>调用返回不代表消息已经被发送.</li>
 * <li>无法做事务控制</li>
 * <li>永远不会抛出异常, 因为异常不产生在调用线程里</li>
 * </ol>
 * 它的好处是能够避免频繁开启session，message producer的开销，并且不会有多线程并发问题，因为session，message producer不是线程安全的
 * </p>
 * <p>
 * 因此, {@link DisruptorJmsMessageSender}适合用在只需发送消息，不需关心消息是否被正确消费，不需关心消息的返回结果的场景下. <br>
 * 在使用的时候, 一个{@link DisruptorJmsMessageSender}只能对应一个{@link Destination}, 且一个{@link Destination}只需要一个{@link DisruptorJmsMessageSender}就够了
 * </p>
 */
public class DisruptorJmsMessageSender implements JmsMessageSender, EventHandler<PayloadEvent> {

  private Session session;

  private MessageProducer messageProducer;

  private PayloadEventProducer payloadEventProducer;

  private MessageConverter messageConverter = new SimpleMessageConverter();

  private DupMessageDetectStrategy dupMessageDetectStrategy = new ArtemisMessageDtoDupMessageDetectStrategy();

  private Disruptor disruptor;

  @Override
  public void sendMessage(MessageDto payload) throws JMSException {
    payloadEventProducer.onData(payload);
  }

  @Override
  public void onEvent(PayloadEvent event, long sequence, boolean endOfBatch) throws Exception {

    Message message = MessageConvertUtils.toMessage(messageConverter, session, event.getPayload());
    dupMessageDetectStrategy.setId(message, event.getPayload());
    messageProducer.send(message);

    // 把payload清空, 一遍下次使用
    event.setPayload(null);
  }

  public void setMessageConverter(MessageConverter messageConverter) {
    this.messageConverter = messageConverter;
  }

  public void setSession(Session session) {
    this.session = session;
  }

  public void setMessageProducer(MessageProducer messageProducer) {
    this.messageProducer = messageProducer;
  }

  public void setPayloadEventProducer(PayloadEventProducer payloadEventProducer) {
    this.payloadEventProducer = payloadEventProducer;
  }

  public void setDupMessageDetectStrategy(DupMessageDetectStrategy dupMessageDetectStrategy) {
    this.dupMessageDetectStrategy = dupMessageDetectStrategy;
  }

  public Disruptor getDisruptor() {
    return disruptor;
  }

  public void setDisruptor(Disruptor disruptor) {
    this.disruptor = disruptor;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("DisruptorJmsMessageSender{");
    sb.append("session=").append(session);
    sb.append(", messageProducer=").append(messageProducer);
    sb.append('}');
    return sb.toString();
  }

}
