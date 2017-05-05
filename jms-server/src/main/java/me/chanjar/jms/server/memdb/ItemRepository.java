package me.chanjar.jms.server.memdb;

/**
 * 商品内存数据库
 */
public interface ItemRepository {

  void put(Item item);

  Item get(Long id);

}
