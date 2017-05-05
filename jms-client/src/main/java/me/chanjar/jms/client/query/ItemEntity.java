package me.chanjar.jms.client.query;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * Created by qianjia on 2017/5/5.
 */
public class ItemEntity implements Serializable {

  /**
   * ID
   */
  private Long id;

  /**
   * 名称
   */
  private String name;
  /**
   * 库存
   */
  private int amount;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getAmount() {
    return amount;
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ItemEntity that = (ItemEntity) o;

    if (amount != that.amount) return false;
    if (id != null ? !id.equals(that.id) : that.id != null) return false;
    return name != null ? name.equals(that.name) : that.name == null;
  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + amount;
    return result;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("id", id)
        .append("name", name)
        .append("amount", amount)
        .toString();
  }

}
