package me.chanjar.jms.server.command.infras;

import me.chanjar.jms.base.utils.StrongUuidGenerator;
import me.chanjar.jms.msg.RequestDto;

import java.io.Serializable;

/**
 * 数据库操作命令
 */
public abstract class Command implements Serializable {

  private static final long serialVersionUID = -2463630580877588711L;
  protected final String id;

  protected final String requestId;

  /**
   * Command来源的requestId
   *
   * @param requestId
   */
  public Command(String requestId) {
    this.id = StrongUuidGenerator.getNextId();
    this.requestId = requestId;
  }

  /**
   * 全局唯一Id, uuid
   *
   * @return
   */
  public String getId() {
    return id;
  }

  /**
   * 对应的{@link RequestDto#id}
   *
   * @return
   */
  public String getRequestId() {
    return requestId;
  }
}
