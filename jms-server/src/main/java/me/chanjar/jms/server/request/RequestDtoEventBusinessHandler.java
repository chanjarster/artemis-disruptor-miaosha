package me.chanjar.jms.server.request;

import com.lmax.disruptor.EventHandler;
import me.chanjar.jms.msg.RequestDto;
import me.chanjar.jms.msg.ResponseDto;
import me.chanjar.jms.server.command.item.ItemAmountUpdateCommand;
import me.chanjar.jms.server.command.order.OrderInsertCommand;
import me.chanjar.jms.server.memdb.Item;
import me.chanjar.jms.server.memdb.ItemRepository;

/**
 * 处理业务逻辑
 */
public class RequestDtoEventBusinessHandler implements EventHandler<RequestDtoEvent> {

  private ItemRepository itemRepository;

  @Override
  public void onEvent(RequestDtoEvent event, long sequence, boolean endOfBatch) throws Exception {

    if (event.hasErrorOrException()) {
      return;
    }

    RequestDto requestDto = event.getRequestDto();
    Item item = itemRepository.get(requestDto.getItemId());

    ResponseDto responseDto = new ResponseDto(requestDto.getId());

    if (item == null) {

      responseDto.setSuccess(false);
      responseDto.setErrorMessage("内存中还未缓存商品数据");

    } else if (item.decreaseAmount()) {

      responseDto.setSuccess(true);

      event.getCommandCollector().addCommand(
          new ItemAmountUpdateCommand(requestDto.getId(), item.getId(), item.getAmount())
      );
      event.getCommandCollector().addCommand(
          new OrderInsertCommand(requestDto.getId(), item.getId(), requestDto.getUserId())
      );

    } else {

      responseDto.setSuccess(false);
      responseDto.setErrorMessage("库存不足");

    }

    event.setResponseDto(responseDto);


  }

  public void setItemRepository(ItemRepository itemRepository) {
    this.itemRepository = itemRepository;
  }

}
