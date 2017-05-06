package me.chanjar.jms.base.netty;

import io.netty.util.ThreadDeathWatcher;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.Ordered;

import java.util.concurrent.TimeUnit;

/**
 * 解决netty在类似tomcat的container中的时候, 会产生的memory leak的问题<br/>
 * 根据下面两个连接提供的解决方案实现:
 * <ol>
 * <li>https://github.com/relayrides/pushy/issues/29</li>
 * <li>https://github.com/netty/netty/issues/2084</li>
 * </ol>
 */
public class NettyLifeCycleContainer implements SmartLifecycle {

  private static final Logger LOGGER = LoggerFactory.getLogger(NettyLifeCycleContainer.class);

  private static final int ORDER = Ordered.LOWEST_PRECEDENCE;

  private volatile boolean running;

  private final Object monitor = new Object();

  @Override
  public boolean isAutoStartup() {
    return true;
  }

  @Override
  public void stop(Runnable callback) {
    stop();
    callback.run();
  }

  @Override
  public void start() {
    LOGGER.info("Manage life cycle of " + GlobalEventExecutor.class.getName());
    this.running = true;
  }

  @Override
  public void stop() {

    synchronized (this.monitor) {

      if (this.running) {

        try {

          GlobalEventExecutor.INSTANCE.awaitInactivity(5L, TimeUnit.SECONDS);
          LOGGER.info("Close {}", GlobalEventExecutor.class.getName());

        } catch (Exception e) {

          LOGGER.warn("Error happened when close {}", GlobalEventExecutor.class.getName());
          LOGGER.warn(ExceptionUtils.getStackTrace(e));

        }

        try {

          ThreadDeathWatcher.awaitInactivity(5L, TimeUnit.SECONDS);
          LOGGER.info("Close {}", ThreadDeathWatcher.class.getName());

        } catch (Exception e) {

          LOGGER.warn("Error happened when close {}", ThreadDeathWatcher.class.getName());
          LOGGER.warn(ExceptionUtils.getStackTrace(e));

        }

        this.running = false;
      }

    }
  }

  @Override
  public boolean isRunning() {
    return this.running;
  }

  @Override
  public int getPhase() {
    return ORDER;
  }

}

