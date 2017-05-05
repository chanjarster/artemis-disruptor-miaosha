package me.chanjar.jms.server.request;

import com.lmax.disruptor.EventHandler;
import me.chanjar.jms.server.command.infras.Command;
import me.chanjar.jms.server.command.infras.CommandDispatcher;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * 将结果RequestDtoEvent的结果输出到数据库
 */
public class RequestDtoEventDbOutputer implements EventHandler<RequestDtoEvent> {


  private CommandDispatcher commandDispatcher;

  @Override
  public void onEvent(RequestDtoEvent event, long sequence, boolean endOfBatch) throws Exception {

    if (event.hasErrorOrException()) {
      return;
    }

    List<Command> commandList = event.getCommandCollector().getCommandList();
    if (CollectionUtils.isEmpty(commandList)) {
      return;
    }

    for (Command command : commandList) {
      commandDispatcher.dispatch(command);
    }

  }

  public void setCommandDispatcher(CommandDispatcher commandDispatcher) {
    this.commandDispatcher = commandDispatcher;
  }
}
