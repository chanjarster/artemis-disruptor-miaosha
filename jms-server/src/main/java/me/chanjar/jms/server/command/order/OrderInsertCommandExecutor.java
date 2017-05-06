package me.chanjar.jms.server.command.order;

import me.chanjar.jms.server.command.infras.CommandExecutor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class OrderInsertCommandExecutor implements CommandExecutor<OrderInsertCommandBuffer> {

  private static final Logger LOGGER = LoggerFactory.getLogger(OrderInsertCommandExecutor.class);

  private static final String SQL = "INSERT INTO ITEM_ORDER(ID, ITEM_ID, USER_ID)\n"
      + "VALUES (SEQ_ITEM_ORDER.nextval, ?, ?)";

  private JdbcTemplate jdbcTemplate;

  public OrderInsertCommandExecutor(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void execute(OrderInsertCommandBuffer commandBuffer) {

    List<OrderInsertCommand> commands = commandBuffer.get();
    if (CollectionUtils.isEmpty(commands)) {
      return;
    }

    List<Object[]> args = commands.stream().map(cmd -> new Object[] { cmd.getItemId(), cmd.getUserId() })
        .collect(toList());

    try  {

      jdbcTemplate.batchUpdate(SQL, args);
      commands.forEach(command -> LOGGER.info("Executed", command));

    } catch (Exception e) {

      commands.forEach(command -> LOGGER.error("Failed", command));
      LOGGER.error(ExceptionUtils.getStackTrace(e));

    }

  }

}
