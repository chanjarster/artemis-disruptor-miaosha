package me.chanjar.jms.base.factory.session;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

public abstract class SessionFactory {

  private SessionFactory() {

  }

  public static Session createSession(Connection connection, SessionOption sessionOption) throws JMSException {
    return connection.createSession(sessionOption.isTransacted(), sessionOption.getAcknowledgeMode());
  }

}
