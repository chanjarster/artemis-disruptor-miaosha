package me.chanjar.jms.server.command.infras;

/**
 * 数据库命令分派器, 分派给{@link CommandProcessor}
 */
public interface CommandDispatcher {

  void dispatch(Command command);

  void registerCommandProcessor(CommandProcessor commandProcessor);

}
