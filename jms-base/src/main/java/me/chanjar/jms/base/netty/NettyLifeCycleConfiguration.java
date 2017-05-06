package me.chanjar.jms.base.netty;

import io.netty.util.ThreadDeathWatcher;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({ GlobalEventExecutor.class, ThreadDeathWatcher.class })
public class NettyLifeCycleConfiguration {

  @Bean
  public NettyLifeCycleContainer nettyLifeCycleContainer() {
    return new NettyLifeCycleContainer();
  }

}
