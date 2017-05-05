package me.chanjar.jms.server.memdb;

import java.io.Serializable;

/**
 * 商品
 */
public class Item implements Serializable {

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

    if (amount == 0) {
      return false;
    }
    amount--;
    return true;

  }

}
