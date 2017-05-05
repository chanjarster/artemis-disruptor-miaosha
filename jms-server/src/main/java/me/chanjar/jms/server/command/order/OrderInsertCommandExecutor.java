package me.chanjar.jms.server.command.order;

import me.chanjar.jms.server.command.infras.CommandExecutor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

public class OrderInsertCommandExecutor implements CommandExecutor<OrderInsertCommandBuffer> {

  private static final Logger LOGGER = LoggerFactory.getLogger(OrderInsertCommandExecutor.class);

  private static final String INSERT_SQL = "INSERT INTO ITEM_ORDER(ID, ITEM_ID, USER_ID)\n"
      + "VALUES (SEQ_ITEM_ORDER.nextval, ?, ?)";

  private final DataSource dataSource;

  public OrderInsertCommandExecutor(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public void execute(OrderInsertCommandBuffer commandBuffer) {

    List<OrderInsertCommand> commands = commandBuffer.get();
    if (CollectionUtils.isEmpty(commands)) {
      return;
    }

    try (Connection connection = dataSource.getConnection()) {

      try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL)) {

        for (OrderInsertCommand command : commands) {

          preparedStatement.setLong(1, command.getItemId());
          preparedStatement.setString(2, command.getUserId());

          preparedStatement.addBatch();

        }

        preparedStatement.executeBatch();

      } catch (Exception e) {

        if (!connection.getAutoCommit()) {
          connection.rollback();
        }

        throw e;

      }

      if (!connection.getAutoCommit()) {
        connection.commit();
      }

      commands.forEach(command -> LOGGER.info("Executed", command));

    } catch (Exception e) {

      commands.forEach(command -> LOGGER.error("Failed", command));
      LOGGER.error(ExceptionUtils.getStackTrace(e));

    }

  }

}
