package me.chanjar.jms.base.factory.session;

import javax.jms.Session;

public class SessionOption {

  private int acknowledgeMode = Session.AUTO_ACKNOWLEDGE;

  private boolean transacted = true;

  public int getAcknowledgeMode() {
    return acknowledgeMode;
  }

  /**
   * Default - {@link Session#AUTO_ACKNOWLEDGE}
   *
   * @param acknowledgeMode
   * @see Session#getAcknowledgeMode()
   */
  public void setAcknowledgeMode(int acknowledgeMode) {
    this.acknowledgeMode = acknowledgeMode;
  }

  public boolean isTransacted() {
    return transacted;
  }

  /**
   * Default - false.
   *
   * @param transacted
   * @see Session#getTransacted()
   */
  public void setTransacted(boolean transacted) {
    this.transacted = transacted;
  }
}
