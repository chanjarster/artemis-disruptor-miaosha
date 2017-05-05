package me.chanjar.jms.server.request;

import me.chanjar.jms.msg.RequestDto;
import me.chanjar.jms.msg.ResponseDto;
import me.chanjar.jms.server.command.infras.CommandCollector;

public class RequestDtoEvent {

  private RequestDto requestDto;

  /**
   * 数据库操作Command收集器
   */
  private final CommandCollector commandCollector = new CommandCollector();

  /**
   * 响应结果
   */
  private ResponseDto responseDto;

  public RequestDto getRequestDto() {
    return requestDto;
  }

  public void setRequestDto(RequestDto requestDto) {
    this.requestDto = requestDto;
  }

  public void setResponseDto(ResponseDto responseDto) {
    this.responseDto = responseDto;
  }

  public CommandCollector getCommandCollector() {
    return commandCollector;
  }

  public ResponseDto getResponseDto() {
    return responseDto;
  }

  public void clearForGc() {
    this.requestDto = null;
    this.commandCollector.getCommandList().clear();
    this.responseDto = null;
  }

  public boolean hasErrorOrException() {
    return responseDto != null && (responseDto.getErrorMessage() != null);
  }

}
