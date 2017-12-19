package me.chanjar.jms.server.command.infras;

/**
 * Command缓冲溢出异常
 */
public class CommandBufferOverflowException extends RuntimeException {

  private static final long serialVersionUID = -408555980971903979L;

  /**
   * Constructs an instance of this class.
   */
  public CommandBufferOverflowException() {
    // do nothing
  }

}
