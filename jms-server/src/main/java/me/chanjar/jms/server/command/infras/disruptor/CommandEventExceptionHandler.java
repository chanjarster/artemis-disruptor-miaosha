package me.chanjar.jms.server.command.infras.disruptor;

import com.lmax.disruptor.ExceptionHandler;
import me.chanjar.jms.server.command.infras.Command;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandEventExceptionHandler<T extends Command> implements ExceptionHandler<CommandEvent<T>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(CommandEventExceptionHandler.class);

  @Override
  public void handleEventException(Throwable ex, long sequence, CommandEvent<T> event) {

    LOGGER.error("{} : {}. {} ", event.getCommand().getClass().getName(), event.getCommand().getId(), ExceptionUtils.getStackTrace(ex));

  }

  @Override
  public void handleOnStartException(Throwable ex) {
    LOGGER.error("Exception during onStart()", ex);
  }

  @Override
  public void handleOnShutdownException(Throwable ex) {
    LOGGER.error("Exception during onShutdown()", ex);
  }

}
