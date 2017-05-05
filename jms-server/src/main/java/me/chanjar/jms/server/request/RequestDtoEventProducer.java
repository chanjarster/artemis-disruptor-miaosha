package me.chanjar.jms.server.request;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;
import me.chanjar.jms.msg.RequestDto;

public class RequestDtoEventProducer {

  private static final EventTranslatorOneArg<RequestDtoEvent, RequestDto> TRANSLATOR =
      (event, sequence, requestDto) -> event.setRequestDto(requestDto);

  private final RingBuffer<RequestDtoEvent> ringBuffer;

  public RequestDtoEventProducer(RingBuffer<RequestDtoEvent> ringBuffer) {
    this.ringBuffer = ringBuffer;
  }

  public void onData(RequestDto requestDto) {
    ringBuffer.publishEvent(TRANSLATOR, requestDto);
  }

}
