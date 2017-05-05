package me.chanjar.jms.server.command.infras.disruptor;

import com.lmax.disruptor.EventFactory;
import me.chanjar.jms.server.command.infras.Command;

public class CommandEventFactory<T extends Command> implements EventFactory<CommandEvent<T>> {
  @Override
  public CommandEvent<T> newInstance() {
    return new CommandEvent<>();
  }
}
