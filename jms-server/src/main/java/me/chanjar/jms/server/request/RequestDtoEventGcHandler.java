package me.chanjar.jms.server.request;

import com.lmax.disruptor.EventHandler;
import me.chanjar.jms.server.request.RequestDtoEvent;

/**
 * RequestDtoEvent的GC处理器
 */
public class RequestDtoEventGcHandler implements EventHandler<RequestDtoEvent> {

  @Override
  public void onEvent(RequestDtoEvent event, long sequence, boolean endOfBatch) throws Exception {

    event.clearForGc();

  }

}
