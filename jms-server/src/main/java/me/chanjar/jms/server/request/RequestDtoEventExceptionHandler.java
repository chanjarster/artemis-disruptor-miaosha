package me.chanjar.jms.server.request;

import com.lmax.disruptor.ExceptionHandler;
import me.chanjar.jms.msg.ResponseDto;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestDtoEventExceptionHandler implements ExceptionHandler<RequestDtoEvent> {

  private static final Logger LOGGER = LoggerFactory.getLogger(RequestDtoEventExceptionHandler.class);

  @Override
  public void handleEventException(Throwable ex, long sequence, RequestDtoEvent event) {

    // 遇到异常要记录日志的
    event.setResponseDto(
        createExceptionResponseDto(event.getRequestDto().getId(), ExceptionUtils.getStackTrace(ex))
    );
    LOGGER.error("{} : {}. {} ", event.getRequestDto().getClass().getName(), event.getRequestDto().getId(), ExceptionUtils.getStackTrace(ex));
  }

  private ResponseDto createExceptionResponseDto(String requestId, String exception) {
    ResponseDto responseDto = new ResponseDto(requestId);
    responseDto.setErrorMessage(exception);
    responseDto.setSuccess(false);
    return responseDto;
  }

  @Override
  public void handleOnStartException(Throwable ex) {
    LOGGER.error("Exception during onStart()", ex);
  }

  @Override
  public void handleOnShutdownException(Throwable ex) {
    LOGGER.error("Exception during onShutdown()", ex);
  }

}
