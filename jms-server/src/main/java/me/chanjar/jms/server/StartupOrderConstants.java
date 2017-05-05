package me.chanjar.jms.server;

import org.springframework.jca.endpoint.GenericMessageEndpointManager;

/**
 * 程序启动时数据刷新的顺序 <br>
 * 数字越小越靠前 <br>
 * 排在最后的也比JMS的初始化靠前 见 {@link GenericMessageEndpointManager#phase} <br>
 */
public abstract class StartupOrderConstants {

  public static final int DISRUPTOR_REQUEST_DTO = 1;
  public static final int DISRUPTOR_ORDER_INSERT = 2;
  public static final int DISRUPTOR_ITEM_UPDATE = 3;

  private StartupOrderConstants() {
    // singleton
  }

}
