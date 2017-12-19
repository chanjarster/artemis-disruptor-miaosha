package me.chanjar.jms.server.command.item;

import me.chanjar.jms.server.command.infras.Command;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 更新商品库存的命令
 */
public class ItemAmountUpdateCommand extends Command {

  private static final long serialVersionUID = 7896607558242859910L;
  private final Long itemId;

  private final int amount;

  /**
   * @param requestId Command来源的requestId
   * @param itemId    商品ID
   * @param amount    库存
   */
  public ItemAmountUpdateCommand(String requestId, Long itemId, int amount) {
    super(requestId);
    this.itemId = itemId;
    this.amount = amount;
  }

  public Long getItemId() {
    return itemId;
  }

  public int getAmount() {
    return amount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ItemAmountUpdateCommand that = (ItemAmountUpdateCommand) o;

    if (amount != that.amount) return false;
    return itemId != null ? itemId.equals(that.itemId) : that.itemId == null;
  }

  @Override
  public int hashCode() {
    int result = itemId != null ? itemId.hashCode() : 0;
    result = 31 * result + amount;
    return result;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("id", id)
        .append("requestId", requestId)
        .append("itemId", itemId)
        .append("amount", amount)
        .toString();
  }

}
