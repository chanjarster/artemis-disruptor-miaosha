package me.chanjar;

import me.chanjar.jms.client.command.MiaoShaCommandService;
import me.chanjar.jms.msg.RequestDto;
import me.chanjar.jms.msg.ResponseDto;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 秒杀Rest Controller
 */
@RestController
public class MiaoshaRestController {

  private MiaoShaCommandService miaoshaCommandService;

  /**
   * 下单
   *
   * @param itemId
   * @return
   */
  @RequestMapping(value = "/order", method = RequestMethod.POST)
  public RequestDto order(@RequestParam("itemId") Long itemId) {

    RequestDto requestDto = new RequestDto(itemId, getUser());
    miaoshaCommandService.doRequest(requestDto);
    return requestDto;

  }

  /**
   * 获得下单结果
   *
   * @param requestId
   * @return
   */
  @RequestMapping(value = "/order-result", method = RequestMethod.GET)
  public ResponseDto getOrderResult(@RequestParam("requestId") String requestId) {

    return miaoshaCommandService.getResponse(requestId);

  }

  @Autowired
  public void setMiaoshaCommandService(MiaoShaCommandService miaoshaCommandService) {
    this.miaoshaCommandService = miaoshaCommandService;
  }

  /**
   * 假的，获得当前用户名的方法
   *
   * @return
   */
  private String getUser() {
    return RandomStringUtils.randomAlphabetic(5);
  }
}
