package me.chanjar.jms.server.memdb;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * 商品
 */
public class Item implements Serializable {

  private static final long serialVersionUID = -873268150277605569L;
  /**
   * ID
   */
  private final Long id;

  /**
   * 库存
   */
  private int amount;

  public Item(Long id, int amount) {
    this.id = id;
    this.amount = amount;
  }

  public Long getId() {
    return id;
  }

  public int getAmount() {
    return amount;
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }

  /**
   * 减库存，如果库存不足，则扣减失败
   *
   * @return
   */
  public boolean decreaseAmount() {

    if (!hasRemaining()) {
      return false;
    }
    amount--;
    return true;

  }

  /**
   * 是否还有库存
   *
   * @return
   */
  public boolean hasRemaining() {
    return amount > 0;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("id", id)
        .append("amount", amount)
        .toString();
  }

}
