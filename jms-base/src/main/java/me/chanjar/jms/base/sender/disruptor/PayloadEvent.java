package me.chanjar.jms.base.sender.disruptor;

public class PayloadEvent {

  private Object payload;

  public Object getPayload() {
    return payload;
  }

  public void setPayload(Object payload) {
    this.payload = payload;
  }

}
