package me.chanjar.jms.server.command.item;

import me.chanjar.jms.server.command.infras.CommandProcessor;
import me.chanjar.jms.server.command.infras.disruptor.CommandEventProducer;

/**
 * Created by qianjia on 16/5/17.
 */
public class ItemAmountUpdateCommandProcessor implements CommandProcessor<ItemAmountUpdateCommand> {

  private final CommandEventProducer<ItemAmountUpdateCommand>[] commandEventProducerList;

  private final int producerCount;

  public ItemAmountUpdateCommandProcessor(
      CommandEventProducer<ItemAmountUpdateCommand>[] commandEventProducerList) {
    this.commandEventProducerList = commandEventProducerList;
    this.producerCount = commandEventProducerList.length;
  }

  @Override
  public Class<ItemAmountUpdateCommand> getMatchClass() {
    return ItemAmountUpdateCommand.class;
  }

  @Override
  public void process(ItemAmountUpdateCommand command) {

    int index = (int) (command.getItemId() % (long) this.producerCount);
    commandEventProducerList[index].onData(command);

  }

}
