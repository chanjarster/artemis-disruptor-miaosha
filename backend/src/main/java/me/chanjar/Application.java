package me.chanjar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = { ArtemisAutoConfiguration.class })
@EnableScheduling
public class Application  {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

}
