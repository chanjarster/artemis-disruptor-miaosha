package me.chanjar.jms.server.command.order;

import me.chanjar.jms.server.command.infras.Command;

/**
 * 保存订单的命令
 */
public class OrderInsertCommand extends Command {

  private static final long serialVersionUID = -1844388054958673686L;
  private final Long itemId;

  private final String userId;

  /**
   * @param requestId Command来源的requestId
   * @param itemId    商品ID
   * @param userId    用户ID
   */
  public OrderInsertCommand(String requestId, Long itemId, String userId) {
    super(requestId);
    this.itemId = itemId;
    this.userId = userId;
  }

  public Long getItemId() {
    return itemId;
  }

  public String getUserId() {
    return userId;
  }

}
