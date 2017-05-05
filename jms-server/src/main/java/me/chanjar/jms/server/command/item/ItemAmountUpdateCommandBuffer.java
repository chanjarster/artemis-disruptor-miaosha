package me.chanjar.jms.server.command.item;


import me.chanjar.jms.server.command.infras.CommandBuffer;
import me.chanjar.jms.server.command.infras.CommandBufferOverflowException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link ItemAmountUpdateCommand}的Buffer <br>
 * 内部存储是一个以{@link ItemAmountUpdateCommand#itemId}为Key的Map <br>
 * 这样做的好处是, 如果有多个相同Key的{@link ItemAmountUpdateCommand}, 那么也就只会记录最后一个<br>
 * 从而减少了Sql语句数量
 */
public class ItemAmountUpdateCommandBuffer implements CommandBuffer<ItemAmountUpdateCommand> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ItemAmountUpdateCommandBuffer.class);

  private final Map<Long, ItemAmountUpdateCommand> commandMap = new HashMap<>();

  private final int capacity;

  public ItemAmountUpdateCommandBuffer(int capacity) {
    this.capacity = capacity;
  }

  @Override
  public boolean hasRemaining() {
    return commandMap.size() < this.capacity;
  }

  /**
   * @param command
   * @throws CommandBufferOverflowException
   */
  @Override
  public void put(ItemAmountUpdateCommand command) {

    Long key = command.getItemId();
    if (!hasRemaining() && commandMap.get(key) == null) {
      throw new CommandBufferOverflowException();
    }

    ItemAmountUpdateCommand prevValue = this.commandMap.put(key, command);
    if (prevValue != null) {
      LOGGER.info("Optimized", command);
    }
    LOGGER.info("Put", command);

  }

  @Override
  public void clear() {
    commandMap.clear();
  }

  @Override
  public List<ItemAmountUpdateCommand> get() {
    return new ArrayList<>(commandMap.values());
  }

}
