package me.chanjar.jms.server.config;

import com.lmax.disruptor.dsl.Disruptor;
import me.chanjar.jms.base.lifecycle.DisruptorLifeCycleContainer;
import me.chanjar.jms.server.StartupOrderConstants;
import me.chanjar.jms.server.command.infras.CommandDispatcher;
import me.chanjar.jms.server.command.infras.disruptor.*;
import me.chanjar.jms.server.command.order.OrderInsertCommand;
import me.chanjar.jms.server.command.order.OrderInsertCommandBuffer;
import me.chanjar.jms.server.command.order.OrderInsertCommandExecutor;
import me.chanjar.jms.server.command.order.OrderInsertCommandProcessor;
import me.chanjar.jms.base.utils.BeanRegisterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.concurrent.Executors;

@Configuration
@EnableConfigurationProperties(OrderInsertProcessorConfiguration.Conf.class)
public class OrderInsertProcessorConfiguration implements ApplicationContextAware {

  private static final Logger LOGGER = LoggerFactory.getLogger(OrderInsertProcessorConfiguration.class);

  @Autowired
  private Conf conf;

  @Autowired
  private CommandDispatcher commandDispatcher;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  private ApplicationContext applicationContext;

  @Bean
  public OrderInsertCommandProcessor courseTakeInsertCmdProcessor() {

    LOGGER.info("Configure OrderInsertCommandProcessor");

    CommandEventProducer<OrderInsertCommand>[] commandEventProducerList = new CommandEventProducer[conf.getNum()];

    for (int i = 0; i < conf.getNum(); i++) {

      OrderInsertCommandBuffer cmdBuffer = new OrderInsertCommandBuffer(conf.getSqlBufferSize());
      OrderInsertCommandExecutor cmdExecutor = new OrderInsertCommandExecutor(jdbcTemplate);

      Disruptor<CommandEvent<OrderInsertCommand>> disruptor = new Disruptor<>(
          new CommandEventFactory(),
          conf.getQueueSize(),
          Executors.defaultThreadFactory());

      disruptor
          .handleEventsWith(new CommandEventDbHandler(cmdBuffer, cmdExecutor))
          .then(new CommandEventGcHandler())
      ;
      // disruptor 的异常处理是这样的,
      // 不论这种形式 A->B, 还是这种形式 A,B->C,D, 只有抛出异常的那个handler会中断执行
      disruptor.setDefaultExceptionHandler(new CommandEventExceptionHandler());

      commandEventProducerList[i] = new CommandEventProducer<>(disruptor.getRingBuffer());

      BeanRegisterUtils.registerSingleton(
          applicationContext,
          "CommandEvent<OrderInsertCommand>_DisruptorLifeCycleContainer_" + i,
          new DisruptorLifeCycleContainer("CommandEvent<OrderInsertCommand>_Disruptor_" + i, disruptor,
              StartupOrderConstants.DISRUPTOR_ORDER_INSERT));

    }

    OrderInsertCommandProcessor cmdProcessor = new OrderInsertCommandProcessor(commandEventProducerList);

    commandDispatcher.registerCommandProcessor(cmdProcessor);

    return cmdProcessor;

  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  @ConfigurationProperties(prefix = "order-insert.proc")
  public static class Conf {

    /**
     * 处理器数量
     */
    private int num;

    /**
     * 单次执行的SQL条数 (将多条SQL放到一起执行比分多次执行效率高)
     */
    private int sqlBufferSize;

    /**
     * disruptor队列长度, 值必须是2的次方
     */
    private int queueSize;

    public int getNum() {
      return num;
    }

    public void setNum(int num) {
      this.num = num;
    }

    public int getSqlBufferSize() {
      return sqlBufferSize;
    }

    public void setSqlBufferSize(int sqlBufferSize) {
      this.sqlBufferSize = sqlBufferSize;
    }

    public int getQueueSize() {
      return queueSize;
    }

    public void setQueueSize(int queueSize) {
      this.queueSize = queueSize;
    }

  }

}
