package me.chanjar.jms.server.command.infras.disruptor;

import com.lmax.disruptor.EventHandler;
import me.chanjar.jms.server.command.infras.Command;

/**
 * DbCommandEvent的GC处理器
 */
public class CommandEventGcHandler<T extends Command> implements EventHandler<CommandEvent<T>> {

  @Override
  public void onEvent(CommandEvent<T> event, long sequence, boolean endOfBatch) throws Exception {
    event.clearForGc();
  }

}
