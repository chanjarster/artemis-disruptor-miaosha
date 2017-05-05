package me.chanjar.jms.server.command.item;

import me.chanjar.jms.server.command.infras.CommandExecutor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

public class ItemAmountUpdateCommandExecutor implements CommandExecutor<ItemAmountUpdateCommandBuffer> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ItemAmountUpdateCommandExecutor.class);

  private static final String SQL = "UPDATE ITEM SET AMOUNT = ? WHERE ID = ?";

  private final DataSource dataSource;

  public ItemAmountUpdateCommandExecutor(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public void execute(ItemAmountUpdateCommandBuffer commandBuffer) {

    List<ItemAmountUpdateCommand> commands = commandBuffer.get();
    if (CollectionUtils.isEmpty(commands)) {
      return;
    }

    try (Connection connection = dataSource.getConnection()) {

      try (PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {

        for (ItemAmountUpdateCommand command : commands) {

          preparedStatement.setInt(1, command.getAmount());
          preparedStatement.setLong(2, command.getItemId());
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

      commands.forEach(command ->  LOGGER.info("Executed", command));

    } catch (Exception e) {

      commands.forEach(command ->  LOGGER.error("Failed", command));
      LOGGER.error(ExceptionUtils.getStackTrace(e));

    }
  }

}
