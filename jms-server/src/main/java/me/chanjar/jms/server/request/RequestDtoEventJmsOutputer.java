package me.chanjar.jms.server.request;

import com.lmax.disruptor.EventHandler;
import me.chanjar.jms.base.sender.JmsMessageSender;
import me.chanjar.jms.msg.ResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 将结果RequestDtoEvent的结果输出到jms的handler
 */
public class RequestDtoEventJmsOutputer implements EventHandler<RequestDtoEvent> {

  private static final Logger LOGGER = LoggerFactory.getLogger(RequestDtoEventJmsOutputer.class);

  private JmsMessageSender messageSender;

  @Override
  public void onEvent(RequestDtoEvent event, long sequence, boolean endOfBatch) throws Exception {

    ResponseDto responseDto = event.getResponseDto();
    if (responseDto == null) {
      return;
    }
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Send Response for Request {}. Id: {}", responseDto.getRequestId(), responseDto.getId());
    }
    // 这里这么做没有问题, 因为实际的调用方是单线程调用的, 多线程下则会出现并发问题
    messageSender.sendMessage(responseDto);

  }

  public void setMessageSender(JmsMessageSender messageSender) {
    this.messageSender = messageSender;
  }
}
