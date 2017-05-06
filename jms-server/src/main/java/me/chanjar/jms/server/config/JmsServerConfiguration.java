package me.chanjar.jms.server.config;

import com.lmax.disruptor.dsl.Disruptor;
import me.chanjar.jms.base.factory.consumer.MessageConsumerFactory;
import me.chanjar.jms.base.factory.consumer.MessageConsumerOption;
import me.chanjar.jms.base.factory.producer.MessageProducerFactory;
import me.chanjar.jms.base.factory.producer.MessageProducerOption;
import me.chanjar.jms.base.factory.session.SessionFactory;
import me.chanjar.jms.base.factory.session.SessionOption;
import me.chanjar.jms.base.lifecycle.MessageConsumerLifeCycleContainer;
import me.chanjar.jms.base.lifecycle.MessageProducerLifeCycleContainer;
import me.chanjar.jms.base.lifecycle.SessionLifeCycleContainer;
import me.chanjar.jms.base.sender.JmsMessageSender;
import me.chanjar.jms.base.sender.disruptor.DisruptorJmsMessageSender;
import me.chanjar.jms.base.sender.disruptor.DisruptorJmsMessageSenderFactory;
import me.chanjar.jms.base.utils.ArtemisMessageDtoDupMessageDetectStrategy;
import me.chanjar.jms.base.lifecycle.DisruptorLifeCycleContainer;
import me.chanjar.jms.server.RequestDtoListener;
import me.chanjar.jms.server.request.RequestDtoEventProducer;
import me.chanjar.jms.base.utils.BeanRegisterUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import javax.jms.*;

@Configuration
public class JmsServerConfiguration implements ApplicationContextAware {

  private ApplicationContext applicationContext;

  @Autowired
  @Qualifier("defaultConnection")
  private Connection connection;

  @Autowired
  @Qualifier("defaultQueue")
  private Queue queue;

  @Autowired
  @Qualifier("defaultTopic")
  private Topic topic;

  @Bean
  public Session requestSession() throws JMSException {
    return createSession();

  }

  @Bean
  public SessionLifeCycleContainer requestSessionLifeCycleContainer() throws JMSException {
    return new SessionLifeCycleContainer(requestSession());
  }

  @Bean
  public RequestDtoListener requestJmsMessageListener() throws JMSException {
    RequestDtoListener messageListener = new RequestDtoListener();
    messageListener.setRequestDtoEventProducer(applicationContext.getBean("requestDtoEventProducer", RequestDtoEventProducer.class));
    return messageListener;
  }

  @Bean
  public MessageConsumer requestMessageConsumer() throws JMSException {
    return MessageConsumerFactory.createMessageConsumer(
        requestSession(),
        queue,
        new MessageConsumerOption(),
        requestJmsMessageListener()
    );

  }

  @Bean
  public MessageConsumerLifeCycleContainer requestMessageConsumerLifeCycleContainer() throws JMSException {
    return new MessageConsumerLifeCycleContainer(requestMessageConsumer());
  }

  @Bean
  public Session responseSession() throws JMSException {
    return createSession();
  }

  @Bean
  public SessionLifeCycleContainer responseSessionLifeCycleContainer() throws JMSException {
    return new SessionLifeCycleContainer(responseSession());
  }

  @Bean
  public MessageProducer responseMessageProducer() throws JMSException {
    return MessageProducerFactory.createMessageProducer(
        responseSession(),
        topic,
        new MessageProducerOption());

  }

  @Bean
  public MessageProducerLifeCycleContainer responseMessageProducerLifeCycleContainer() throws JMSException {
    return new MessageProducerLifeCycleContainer(responseMessageProducer());
  }

  @Bean
  public JmsMessageSender responseMessageSender(@Value("${jms-sender.ring-buffer-size}")int ringBufferSize) throws JMSException {

    DisruptorJmsMessageSender disruptorJmsMessageSender = DisruptorJmsMessageSenderFactory.create(
        responseSession(),
        responseMessageProducer(),
        new ArtemisMessageDtoDupMessageDetectStrategy(),
        ringBufferSize
    );

    Disruptor disruptor = disruptorJmsMessageSender.getDisruptor();

    BeanRegisterUtils.registerSingleton(
        applicationContext,
        "responseMessageSenderLifeCycleContainer",
        new DisruptorLifeCycleContainer("responseMessageSender", disruptor, Ordered.LOWEST_PRECEDENCE)
    );

    return disruptorJmsMessageSender;

  }

  private Session createSession() throws JMSException {

    SessionOption sessionOption = new SessionOption();
    sessionOption.setTransacted(false);
    sessionOption.setAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);

    return SessionFactory.createSession(connection, sessionOption);

  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

}
