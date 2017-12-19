package me.chanjar.jms.msg;

import me.chanjar.jms.base.msg.MessageDto;

/**
 * 购买请求的响应结果
 */
public class ResponseDto extends MessageDto {

  private static final long serialVersionUID = -4690648814874030736L;
  /**
   * 关联的RequestDto的id
   */
  private final String requestId;

  /**
   * 错误消息
   */
  protected String errorMessage;

  /**
   * 是否成功处理请求
   */
  protected boolean success;

  public ResponseDto(String requestId) {
    super();
    this.requestId = requestId;
  }

  public String getRequestId() {
    return requestId;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ResponseDto that = (ResponseDto) o;

    if (success != that.success) return false;
    if (requestId != null ? !requestId.equals(that.requestId) : that.requestId != null) return false;
    return errorMessage != null ? errorMessage.equals(that.errorMessage) : that.errorMessage == null;
  }

  @Override
  public int hashCode() {
    int result = requestId != null ? requestId.hashCode() : 0;
    result = 31 * result + (errorMessage != null ? errorMessage.hashCode() : 0);
    result = 31 * result + (success ? 1 : 0);
    return result;
  }
}
