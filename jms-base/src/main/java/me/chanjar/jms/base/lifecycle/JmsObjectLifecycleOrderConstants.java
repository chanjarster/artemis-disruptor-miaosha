package me.chanjar.jms.base.lifecycle;

import org.springframework.core.Ordered;

public abstract class JmsObjectLifecycleOrderConstants {

  private JmsObjectLifecycleOrderConstants() {
  }

  public static final int CONNECTION_FACTORY = Ordered.LOWEST_PRECEDENCE - 4;
  public static final int CONNECTION = Ordered.LOWEST_PRECEDENCE - 3;
  public static final int SESSION = Ordered.LOWEST_PRECEDENCE - 2;
  public static final int PRODUCER_CONSUMER = Ordered.LOWEST_PRECEDENCE - 1;
  public static final int ES_CLIENT = Ordered.LOWEST_PRECEDENCE;

}
