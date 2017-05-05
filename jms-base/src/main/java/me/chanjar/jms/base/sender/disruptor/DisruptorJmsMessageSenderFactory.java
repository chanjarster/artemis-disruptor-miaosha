package me.chanjar.jms.base.sender.disruptor;

import com.lmax.disruptor.dsl.Disruptor;
import me.chanjar.jms.base.utils.DupMessageDetectStrategy;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import java.util.concurrent.Executors;

public abstract class DisruptorJmsMessageSenderFactory {

  private DisruptorJmsMessageSenderFactory() {
  }

  /**
   * 得到返回的结果后, 必须执行 {@link DisruptorJmsMessageSender#getDisruptor()}.start() 才可以使用
   *
   * @param session
   * @param messageProducer
   * @param dupMessageDetectStrategy
   * @param ringBufferSize           必须是2的次方
   * @return
   * @throws JMSException
   */
  public static DisruptorJmsMessageSender create(
      Session session,
      MessageProducer messageProducer,
      DupMessageDetectStrategy dupMessageDetectStrategy,
      int ringBufferSize
  ) throws JMSException {

    Disruptor<PayloadEvent> disruptor = new Disruptor<>(
        PayloadEvent::new,
        ringBufferSize,
        Executors.defaultThreadFactory()
    );

    PayloadEventProducer payloadEventProducer = new PayloadEventProducer(disruptor.getRingBuffer());

    DisruptorJmsMessageSender messageSender = new DisruptorJmsMessageSender();
    messageSender.setSession(session);
    messageSender.setMessageProducer(messageProducer);
    messageSender.setPayloadEventProducer(payloadEventProducer);
    if (dupMessageDetectStrategy != null) {
      messageSender.setDupMessageDetectStrategy(dupMessageDetectStrategy);
    }

    disruptor.handleEventsWith(messageSender);

    messageSender.setDisruptor(disruptor);

    return messageSender;

  }

}
