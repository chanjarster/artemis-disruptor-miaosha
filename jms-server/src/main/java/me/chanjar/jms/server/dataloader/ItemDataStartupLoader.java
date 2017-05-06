package me.chanjar.jms.server.dataloader;

import me.chanjar.jms.server.memdb.Item;
import me.chanjar.jms.server.memdb.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 启动时，将数据库中的数据，加载到内存中
 */
@Component
public class ItemDataStartupLoader extends DataStartupLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger(ItemDataStartupLoader.class);

  private JdbcTemplate jdbcTemplate;

  private ItemRepository itemRepository;

  @Override
  protected void doLoad() {
    List<Item> items = jdbcTemplate.query("select id, amount from item",
        (rs, rowNum) -> new Item(rs.getLong(1), rs.getInt(2)));

    items.stream().forEach(item -> {
      itemRepository.put(item);
      LOGGER.info("Load Item from database: {}", item.toString());
    });

  }

  @Override
  public int getPhase() {
    return Ordered.LOWEST_PRECEDENCE;
  }

  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Autowired
  public void setItemRepository(ItemRepository itemRepository) {
    this.itemRepository = itemRepository;
  }

}
