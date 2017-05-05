package me.chanjar.jms.client.command;

import me.chanjar.jms.msg.ResponseDto;

/**
 * 响应结果缓存
 */
public interface ResponseCache {

  /**
   * 放入响应结果
   *
   * @param responseDto
   */
  void put(ResponseDto responseDto);

  /**
   * 获得响应结果，并将其从缓存中删除
   *
   * @param requestId
   * @return
   */
  ResponseDto getAndRemove(String requestId);

}
