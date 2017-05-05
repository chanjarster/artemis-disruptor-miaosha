package me.chanjar.jms.base.sender.disruptor;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;

import javax.jms.JMSException;

public class PayloadEventProducer {

  private final RingBuffer<PayloadEvent> ringBuffer;

  private static final EventTranslatorOneArg<PayloadEvent, Object> TRANSLATOR =
      (event, sequence, requestDto) -> event.setPayload(requestDto);

  public PayloadEventProducer(RingBuffer<PayloadEvent> ringBuffer) {
    this.ringBuffer = ringBuffer;
  }

  public void onData(Object payload) throws JMSException {

    ringBuffer.publishEvent(TRANSLATOR, payload);

  }

}
