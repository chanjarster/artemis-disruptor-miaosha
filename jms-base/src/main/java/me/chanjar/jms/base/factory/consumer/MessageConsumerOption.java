package me.chanjar.jms.base.factory.consumer;

import javax.jms.Destination;

public class MessageConsumerOption {

  private String messageSelector;
  private Boolean noLocal;

  public String getMessageSelector() {
    return messageSelector;
  }

  /**
   * 可选, {@link javax.jms.Session#createConsumer(Destination, String)}
   *
   * @param messageSelector
   */
  public void setMessageSelector(String messageSelector) {
    this.messageSelector = messageSelector;
  }

  public Boolean getNoLocal() {
    return noLocal;
  }

  /**
   * 可选, {@link javax.jms.Session#createConsumer(Destination, String, boolean)}
   *
   * @param noLocal
   */
  public void setNoLocal(Boolean noLocal) {
    this.noLocal = noLocal;
  }
}
