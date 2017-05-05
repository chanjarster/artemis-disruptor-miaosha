package me.chanjar.jms.server.command.order;


import me.chanjar.jms.server.command.infras.CommandBuffer;
import me.chanjar.jms.server.command.infras.CommandBufferOverflowException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class OrderInsertCommandBuffer implements CommandBuffer<OrderInsertCommand> {

  private static final Logger LOGGER = LoggerFactory.getLogger(OrderInsertCommandBuffer.class);

  private final List<OrderInsertCommand> commandList;

  private final int capacity;

  public OrderInsertCommandBuffer(int capacity) {
    this.capacity = capacity;
    this.commandList = new ArrayList<>(capacity);
  }

  @Override
  public boolean hasRemaining() {
    return commandList.size() < this.capacity;
  }

  /**
   * @param command
   * @throws CommandBufferOverflowException
   */
  @Override
  public void put(OrderInsertCommand command) {

    if (!hasRemaining()) {
      throw new CommandBufferOverflowException();
    }

    this.commandList.add(command);
    LOGGER.info("Put", command);

  }

  @Override
  public void clear() {
    commandList.clear();
  }

  @Override
  public List<OrderInsertCommand> get() {
    return new ArrayList<>(commandList);
  }

}
