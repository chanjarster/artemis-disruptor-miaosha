package me.chanjar.jms.server.command.order;

import me.chanjar.jms.server.command.infras.CommandProcessor;
import me.chanjar.jms.server.command.infras.disruptor.CommandEventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderInsertCommandProcessor implements CommandProcessor<OrderInsertCommand> {

  private static final Logger LOGGER = LoggerFactory.getLogger(OrderInsertCommandProcessor.class);

  private final CommandEventProducer<OrderInsertCommand>[] commandEventProducerList;

  private final int producerCount;

  public OrderInsertCommandProcessor(
      CommandEventProducer<OrderInsertCommand>[] commandEventProducerList) {
    this.commandEventProducerList = commandEventProducerList;
    this.producerCount = commandEventProducerList.length;
  }

  @Override
  public Class<OrderInsertCommand> getMatchClass() {
    return OrderInsertCommand.class;
  }

  @Override
  public void process(OrderInsertCommand command) {

    // 根据商品ID去模
    int index = (int) (command.getItemId() % (long) this.producerCount);
    commandEventProducerList[index].onData(command);

  }

}
