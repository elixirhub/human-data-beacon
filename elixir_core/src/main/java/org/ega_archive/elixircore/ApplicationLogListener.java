package org.ega_archive.elixircore;

import org.apache.log4j.MDC;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
public class ApplicationLogListener
    implements
    org.springframework.context.ApplicationListener<ApplicationStartedEvent> {

  @Override
  public void onApplicationEvent(ApplicationStartedEvent event) {
    MDC.put("username", System.getProperty("user.name"));
    MDC.put("correlationId", "NA");
    try {
      MDC.put("host", InetAddress.getLocalHost().getHostName());
    } catch (UnknownHostException e) {
      MDC.put("host", "unknown");
    }
  }
}
