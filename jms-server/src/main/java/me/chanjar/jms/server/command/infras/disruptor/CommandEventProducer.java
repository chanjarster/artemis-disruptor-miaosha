package me.chanjar.jms.server.command.infras.disruptor;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;
import me.chanjar.jms.server.command.infras.Command;

public class CommandEventProducer<T extends Command> {

  private final EventTranslatorOneArg<CommandEvent, T> TRANSLATOR =
      (event, sequence, command) -> event.setCommand(command);

  private final RingBuffer<CommandEvent<T>> ringBuffer;

  public CommandEventProducer(RingBuffer<CommandEvent<T>> ringBuffer) {
    this.ringBuffer = ringBuffer;
  }

  public void onData(T command) {

    ringBuffer.publishEvent((EventTranslatorOneArg) TRANSLATOR, command);

  }

}
