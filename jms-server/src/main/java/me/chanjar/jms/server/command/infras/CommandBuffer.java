package me.chanjar.jms.server.command.infras;

import java.util.List;

public interface CommandBuffer<T extends Command> {

  /**
   * Buffer是否已经满了
   *
   * @return
   */
  boolean hasRemaining();

  /**
   * 放入Command
   *
   * @param command
   */
  void put(T command);

  /**
   * 清空缓存
   */
  void clear();

  /**
   * 获得{@link Command}
   *
   * @return
   */
  List<T> get();

}
