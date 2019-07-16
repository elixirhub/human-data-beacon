package org.ega_archive.elixircore.enums;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

@Log4j
@Getter
@AllArgsConstructor
public enum ServiceAction {
  START("start"), STOP("stop"), PAUSE("pause"), RELOAD("reload");

  private String status;

  public static ServiceAction parse(String status) {
    ServiceAction serviceStatus = null;

    if (StringUtils.equalsIgnoreCase(status, START.status)) {
      serviceStatus = START;
    } else if (StringUtils.equalsIgnoreCase(status, STOP.status)) {
      serviceStatus = STOP;
    } else if (StringUtils.equalsIgnoreCase(status, PAUSE.status)) {
      serviceStatus = PAUSE;
    } else if (StringUtils.equalsIgnoreCase(status, RELOAD.status)) {
      serviceStatus = RELOAD;
    } else {
      log.error("Service status not valid: " + status);
    }

    return serviceStatus;
  }
}
