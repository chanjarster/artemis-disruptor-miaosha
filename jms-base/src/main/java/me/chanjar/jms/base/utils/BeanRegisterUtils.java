package me.chanjar.jms.base.utils;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;

public abstract class BeanRegisterUtils {

  private BeanRegisterUtils() {
  }

  public static void registerSingleton(ApplicationContext applicationContext, String beanName, Object singletonObject) {

    AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
    if (!SingletonBeanRegistry.class.isAssignableFrom(beanFactory.getClass())) {
      throw new IllegalArgumentException(
          "ApplicationContext: " + applicationContext.getClass().toString()
              + " doesn't implements SingletonBeanRegistry, cannot register JMS connection at runtime");
    }

    SingletonBeanRegistry beanDefinitionRegistry = (SingletonBeanRegistry) beanFactory;
    beanDefinitionRegistry.registerSingleton(beanName, singletonObject);

  }

  public static void registerSingleton(BeanDefinitionRegistry registry, String beanName, Object singletonObject) {

    if (!SingletonBeanRegistry.class.isAssignableFrom(registry.getClass())) {
      throw new IllegalArgumentException(
          "BeanDefinitionRegistry: " + registry.getClass().toString()
              + " doesn't implements SingletonBeanRegistry, cannot register JMS connection at runtime");
    }

    SingletonBeanRegistry beanDefinitionRegistry = (SingletonBeanRegistry) registry;
    beanDefinitionRegistry.registerSingleton(beanName, singletonObject);

  }

}
