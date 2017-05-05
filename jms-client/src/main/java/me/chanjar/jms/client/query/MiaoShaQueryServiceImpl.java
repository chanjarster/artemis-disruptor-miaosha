package me.chanjar.jms.client.query;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 在实际项目中，应该使用缓存机制查询，而不是直接从数据库中查询
 */
@Component
public class MiaoShaQueryServiceImpl implements MiaoShaQueryService {

  private JdbcTemplate jdbcTemplate;

  @Override
  public List<ItemEntity> queryAll() {
    return jdbcTemplate.query("SELECT ID, NAME, AMOUNT FROM ITEM", (rs, num) -> {
      ItemEntity itemEntity = new ItemEntity();
      itemEntity.setId(rs.getLong(1));
      itemEntity.setName(rs.getString(2));
      itemEntity.setAmount(rs.getInt(3));
      return itemEntity;
    });
  }

  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }
}
