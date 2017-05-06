package me.chanjar.jms.base.config;

import me.chanjar.jms.base.lifecycle.ConnectionFactoryLifeCycleContainer;
import me.chanjar.jms.base.lifecycle.ConnectionLifeCycleContainer;
import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.apache.activemq.artemis.uri.ConnectionFactoryParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.Topic;

@Configuration
@EnableConfigurationProperties(ArtemisJmsConfiguration.ArtemisProperties.class)
public class ArtemisJmsConfiguration {

  @Autowired
  private ArtemisProperties artemisProperties;

  @Bean
  public ConnectionFactory defaultConnectionFactory() throws Exception {
    ConnectionFactoryParser parser = new ConnectionFactoryParser();
    return parser.newObject(parser.expandURI(artemisProperties.getUri()), "defaultConnectionFactory");
  }

  @Bean
  public ConnectionFactoryLifeCycleContainer defaultConnectionFactoryLifeCycleContainer() throws Exception {
    return new ConnectionFactoryLifeCycleContainer(defaultConnectionFactory());
  }

  @Bean
  public Connection defaultConnection() throws Exception {
    return defaultConnectionFactory()
        .createConnection(artemisProperties.getUsername(), artemisProperties.getPassword());
  }

  @Bean
  public ConnectionLifeCycleContainer defaultConnectionLifeCycleContainer() throws Exception {
    return new ConnectionLifeCycleContainer(defaultConnection());
  }

  @Bean
  public Queue defaultQueue() {
    return ActiveMQJMSClient.createQueue(artemisProperties.getQueue());
  }

  @Bean
  public Topic defaultTopic() {
    return ActiveMQJMSClient.createTopic(artemisProperties.getTopic());
  }

  @ConfigurationProperties(prefix = "artemis")
  public static class ArtemisProperties {

    private String uri;

    private String username;

    private String password;

    private String queue;

    private String topic;

    public String getUri() {
      return uri;
    }

    public void setUri(String uri) {
      this.uri = uri;
    }

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }

    public String getQueue() {
      return queue;
    }

    public void setQueue(String queue) {
      this.queue = queue;
    }

    public String getTopic() {
      return topic;
    }

    public void setTopic(String topic) {
      this.topic = topic;
    }
  }

}
