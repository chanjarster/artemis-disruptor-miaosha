package me.chanjar.jms.server.command.infras;

/**
 * {@link Command}执行器
 */
public interface CommandExecutor<T extends CommandBuffer> {

  void execute(T commandBuffer);

}
