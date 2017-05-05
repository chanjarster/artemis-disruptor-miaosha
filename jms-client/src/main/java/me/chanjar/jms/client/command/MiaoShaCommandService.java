package me.chanjar.jms.client.command;

import me.chanjar.jms.msg.RequestDto;
import me.chanjar.jms.msg.ResponseDto;

public interface MiaoShaCommandService {

  String doRequest(RequestDto requestDto);

  ResponseDto getResponse(String requestId);

}
