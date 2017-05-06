package me.chanjar.jms.server.config;

import com.lmax.disruptor.dsl.Disruptor;
import me.chanjar.jms.base.sender.JmsMessageSender;
import me.chanjar.jms.base.lifecycle.DisruptorLifeCycleContainer;
import me.chanjar.jms.server.StartupOrderConstants;
import me.chanjar.jms.server.command.infras.CommandDispatcher;
import me.chanjar.jms.server.command.infras.DefaultCommandDispatcher;
import me.chanjar.jms.server.memdb.ItemRepository;
import me.chanjar.jms.server.request.*;
import me.chanjar.jms.base.utils.BeanRegisterUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.JMSException;
import java.util.concurrent.Executors;

@Configuration
@EnableConfigurationProperties({ RequestDisruptorConfiguration.DisruptorProperties.class })
public class RequestDisruptorConfiguration implements ApplicationContextAware {

  private ApplicationContext applicationContext;

  @Autowired
  private DisruptorProperties disruptorProperties;

  @Bean
  public CommandDispatcher commandDispatcher() {
    return new DefaultCommandDispatcher();
  }

  @Bean
  public RequestDtoEventProducer requestDtoEventProducer() throws JMSException {

    RequestDtoEventDbOutputer requestDtoEventDbOutputer = new RequestDtoEventDbOutputer();
    requestDtoEventDbOutputer.setCommandDispatcher(commandDispatcher());

    RequestDtoEventJmsOutputer requestDtoEventJmsOutputer = new RequestDtoEventJmsOutputer();
    requestDtoEventJmsOutputer.setMessageSender(applicationContext.getBean("responseMessageSender", JmsMessageSender.class));

    Disruptor<RequestDtoEvent> disruptor = new Disruptor<>(
        new RequestDtoEventFactory(),
        disruptorProperties.getJvmQueueSize(),
        Executors.defaultThreadFactory()
    );

    disruptor
        .handleEventsWith(requestDtoEventBusinessHandler())
        .then(requestDtoEventJmsOutputer, requestDtoEventDbOutputer)
        .then(new RequestDtoEventGcHandler());

    // disruptor 的异常处理是这样的,
    // 不论这种形式 A->B, 还是这种形式 A,B->C,D, 只有抛出异常的那个handler会中断执行
    disruptor.setDefaultExceptionHandler(new RequestDtoEventExceptionHandler());

    RequestDtoEventProducer requestDtoEventProducer = new RequestDtoEventProducer(disruptor.getRingBuffer());

    BeanRegisterUtils.registerSingleton(
        applicationContext,
        "RequestDtoEventDisruptorLifeCycleContainer",
        new DisruptorLifeCycleContainer("RequestDtoEventDisruptor", disruptor,
            StartupOrderConstants.DISRUPTOR_REQUEST_DTO));

    return requestDtoEventProducer;
  }

  private RequestDtoEventBusinessHandler requestDtoEventBusinessHandler() {
    RequestDtoEventBusinessHandler requestDtoEventBusinessHandler = new RequestDtoEventBusinessHandler();
    requestDtoEventBusinessHandler.setItemRepository(applicationContext.getBean(ItemRepository.class));
    return requestDtoEventBusinessHandler;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  @ConfigurationProperties(prefix = "request-disruptor")
  public static class DisruptorProperties {

    private int jvmQueueSize;

    public int getJvmQueueSize() {
      return jvmQueueSize;
    }

    public void setJvmQueueSize(int jvmQueueSize) {
      this.jvmQueueSize = jvmQueueSize;
    }

  }

}
