package me.chanjar.jms.client.query;

import java.util.List;

/**
 * 秒杀商品查询Service
 */
public interface MiaoShaQueryService {

  /**
   * 查询所有秒杀商品，
   *
   * @return
   */
  List<ItemEntity> queryAll();

}
