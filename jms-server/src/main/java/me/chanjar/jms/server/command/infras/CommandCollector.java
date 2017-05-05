package me.chanjar.jms.server.command.infras;


import java.util.ArrayList;
import java.util.List;

/**
 * 数据库操作Command收集器
 */
public class CommandCollector {

  private List<Command> commandList = new ArrayList<>(4);

  public List<Command> getCommandList() {
    return commandList;
  }

  public void addCommand(Command command) {
    commandList.add(command);
  }

}
