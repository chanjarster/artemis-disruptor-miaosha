package me.chanjar.jms.server.command.infras.disruptor;

import com.lmax.disruptor.EventHandler;
import me.chanjar.jms.server.command.infras.Command;
import me.chanjar.jms.server.command.infras.CommandBuffer;
import me.chanjar.jms.server.command.infras.CommandExecutor;

public class CommandEventDbHandler<T extends Command> implements EventHandler<CommandEvent<T>> {

  private final CommandBuffer<T> commandBuffer;

  private final CommandExecutor commandExecutor;

  public CommandEventDbHandler(CommandBuffer<T> commandBuffer, CommandExecutor commandExecutor) {
    this.commandBuffer = commandBuffer;
    this.commandExecutor = commandExecutor;
  }

  @Override
  public void onEvent(CommandEvent<T> event, long sequence, boolean endOfBatch) throws Exception {

    if (!commandBuffer.hasRemaining()) {
      flushBuffer();
    }

    commandBuffer.put(event.getCommand());

    if (endOfBatch) {
      flushBuffer();
    }

  }

  private void flushBuffer() {
    commandExecutor.execute(commandBuffer);
    commandBuffer.clear();
  }

}
