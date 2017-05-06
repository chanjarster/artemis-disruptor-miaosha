package me.chanjar.jms.server.command.item;

import me.chanjar.jms.server.command.infras.CommandExecutor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class ItemAmountUpdateCommandExecutor implements CommandExecutor<ItemAmountUpdateCommandBuffer> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ItemAmountUpdateCommandExecutor.class);

  private static final String SQL = "UPDATE ITEM SET AMOUNT = ? WHERE ID = ?";

  private JdbcTemplate jdbcTemplate;

  public ItemAmountUpdateCommandExecutor(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void execute(ItemAmountUpdateCommandBuffer commandBuffer) {

    List<ItemAmountUpdateCommand> commands = commandBuffer.get();
    if (CollectionUtils.isEmpty(commands)) {
      return;
    }

    List<Object[]> args = commands.stream().map(cmd -> new Object[] { cmd.getAmount(), cmd.getItemId() })
        .collect(toList());
    try {

      jdbcTemplate.batchUpdate(SQL, args);
      commands.forEach(command -> LOGGER.info("Executed", command));

    } catch (Exception e) {

      commands.forEach(command -> LOGGER.error("Failed", command));
      LOGGER.error(ExceptionUtils.getStackTrace(e));

    }
  }

}
