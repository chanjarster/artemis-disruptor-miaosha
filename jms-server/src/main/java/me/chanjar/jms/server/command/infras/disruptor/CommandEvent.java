package me.chanjar.jms.server.command.infras.disruptor;

import me.chanjar.jms.server.command.infras.Command;

public class CommandEvent<T extends Command> {

  private T command;

  public T getCommand() {
    return command;
  }

  public void setCommand(T command) {
    this.command = command;
  }

  public void clearForGc() {
    this.command = null;
  }

}
