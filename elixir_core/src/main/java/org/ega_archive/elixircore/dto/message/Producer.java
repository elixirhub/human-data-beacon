package org.ega_archive.elixircore.dto.message;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.ega_archive.elixircore.constant.CoreConstants;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Builder;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@EqualsAndHashCode
public class Producer {

  private String host;
  private String ip;
  private String application;
  private String processId;
  private String userId;

  public Producer() {

    this.application = CoreConstants.MICROSERVICE;

    //NetUtils?
    try {
      this.host = InetAddress.getLocalHost().getHostName();
      this.ip = InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      this.host = "unknown";
    }
    this.userId = System.getProperty("user.name");

    String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
    if (pid == null) {
      pid = "0";
    }
    this.processId = pid;

  }
}
