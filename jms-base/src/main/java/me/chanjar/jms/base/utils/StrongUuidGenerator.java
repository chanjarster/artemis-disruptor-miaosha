package me.chanjar.jms.base.utils;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

public class StrongUuidGenerator {

  private StrongUuidGenerator() {
    // make singleton
  }
  // different ProcessEngines on the same classloader share one generator.
  private static TimeBasedGenerator timeBasedGenerator = Generators.timeBasedGenerator(EthernetAddress.fromInterface());

  public static String getNextId() {
    return timeBasedGenerator.generate().toString();
  }

}
