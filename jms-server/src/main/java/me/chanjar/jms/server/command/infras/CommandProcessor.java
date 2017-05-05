package me.chanjar.jms.server.command.infras;

/**
 * {@link Command}处理器
 */
public interface CommandProcessor<T extends Command> {

  Class<T> getMatchClass();

  void process(T command);

}
