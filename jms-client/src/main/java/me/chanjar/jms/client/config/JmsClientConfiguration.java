package me.chanjar.jms.client.config;

import me.chanjar.jms.base.factory.consumer.MessageConsumerFactory;
import me.chanjar.jms.base.factory.consumer.MessageConsumerOption;
import me.chanjar.jms.base.factory.producer.MessageProducerFactory;
import me.chanjar.jms.base.factory.producer.MessageProducerOption;
import me.chanjar.jms.base.factory.session.SessionFactory;
import me.chanjar.jms.base.factory.session.SessionOption;
import me.chanjar.jms.base.lifecycle.DisruptorLifeCycleContainer;
import me.chanjar.jms.base.lifecycle.MessageConsumerLifeCycleContainer;
import me.chanjar.jms.base.lifecycle.MessageProducerLifeCycleContainer;
import me.chanjar.jms.base.lifecycle.SessionLifeCycleContainer;
import me.chanjar.jms.base.sender.JmsMessageSender;
import me.chanjar.jms.base.sender.disruptor.DisruptorJmsMessageSender;
import me.chanjar.jms.base.sender.disruptor.DisruptorJmsMessageSenderFactory;
import me.chanjar.jms.base.utils.ArtemisMessageDtoDupMessageDetectStrategy;
import me.chanjar.jms.base.utils.BeanRegisterUtils;
import me.chanjar.jms.client.command.ResponseCache;
import me.chanjar.jms.client.command.ResponseJmsMessageListener;
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
public class JmsClientConfiguration implements ApplicationContextAware {

  @Autowired
  private ResponseCache responseCache;

  @Autowired
  @Qualifier("defaultConnection")
  private Connection connection;

  @Autowired
  @Qualifier("defaultQueue")
  private Queue queue;

  @Autowired
  @Qualifier("defaultTopic")
  private Topic topic;

  private ApplicationContext applicationContext;

  @Bean
  public Session requestSession() throws JMSException {

    return createSession();

  }

  @Bean
  public SessionLifeCycleContainer requestSessionLifeCycleContainer() throws JMSException {
    return new SessionLifeCycleContainer(requestSession());
  }

  @Bean
  public MessageProducer requestMessageProducer() throws JMSException {

    return MessageProducerFactory
        .createMessageProducer(requestSession(), queue, new MessageProducerOption());

  }

  @Bean
  public MessageProducerLifeCycleContainer requestMessageProducerLifeCycleContainer() throws JMSException {
    return new MessageProducerLifeCycleContainer(requestMessageProducer());
  }

  @Bean
  public JmsMessageSender requestMessageSender(@Value("${jms-sender.ring-buffer-size}") int ringBufferSize) throws JMSException {

    DisruptorJmsMessageSender messageSender = DisruptorJmsMessageSenderFactory.create(
        requestSession(),
        requestMessageProducer(),
        new ArtemisMessageDtoDupMessageDetectStrategy(),
        ringBufferSize
    );


    BeanRegisterUtils.registerSingleton(
        applicationContext,
        "RequestDtoEventDisruptorLifeCycleContainer",
        new DisruptorLifeCycleContainer("RequestDtoEventDisruptor", messageSender.getDisruptor(),
            Ordered.HIGHEST_PRECEDENCE));

    return messageSender;

  }

  @Bean
  public Session responseSession() throws JMSException {

    return createSession();

  }

  @Bean
  public ResponseJmsMessageListener responseJmsMessageListener() {

    ResponseJmsMessageListener responseJmsMessageListener = new ResponseJmsMessageListener();
    responseJmsMessageListener.setResponseCache(responseCache);
    return responseJmsMessageListener;

  }

  @Bean
  public SessionLifeCycleContainer responseSessionLifeCycleContainer() throws JMSException {
    return new SessionLifeCycleContainer(responseSession());
  }

  @Bean
  public MessageConsumer responseMessageConsumer() throws JMSException {

    return MessageConsumerFactory.createMessageConsumer(
        responseSession(),
        topic,
        new MessageConsumerOption(),
        responseJmsMessageListener()
    );

  }

  @Bean
  public MessageConsumerLifeCycleContainer responseMessageConsumerLifeCycleContainer() throws JMSException {
    return new MessageConsumerLifeCycleContainer(responseMessageConsumer());
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
