package me.chanjar.jms.client.config;

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
import me.chanjar.jms.base.sender.simple.SimpleJmsMessageSenderFactory;
import me.chanjar.jms.base.utils.ArtemisMessageDtoDupMessageDetectStrategy;
import me.chanjar.jms.client.command.ResponseCache;
import me.chanjar.jms.client.command.ResponseJmsMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.*;

@Configuration
public class JmsClientConfiguration {

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
  public JmsMessageSender requestMessageSender() throws JMSException {

    return SimpleJmsMessageSenderFactory.create(
        requestSession(),
        requestMessageProducer(),
        new ArtemisMessageDtoDupMessageDetectStrategy()
    );

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

}
