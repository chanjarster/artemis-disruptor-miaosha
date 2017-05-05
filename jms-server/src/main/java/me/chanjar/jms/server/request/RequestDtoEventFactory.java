package me.chanjar.jms.server.request;

import com.lmax.disruptor.EventFactory;
import me.chanjar.jms.server.request.RequestDtoEvent;

public class RequestDtoEventFactory implements EventFactory<RequestDtoEvent> {

  @Override
  public RequestDtoEvent newInstance() {
    return new RequestDtoEvent();
  }

}
