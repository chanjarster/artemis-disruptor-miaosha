package me.chanjar;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * 秒杀商品页面Controller
 */
@Controller
public class MiaoshaController {

  @RequestMapping(path = { "", "/" }, method = RequestMethod.GET)
  public ModelAndView index() {

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("index");
    return modelAndView;

  }

}
