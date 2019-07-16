package org.ega_archive.elixircore.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

import org.apache.commons.lang3.StringUtils;

@Log4j
@Getter
@AllArgsConstructor
public enum SessionStatus {
  ACTIVE("active"), EXPIRED("expired"), ALL("all");

  private String status;
  
  public static SessionStatus parse(String sessionStatus) {
    SessionStatus status = null;
    
    if(StringUtils.equalsIgnoreCase(sessionStatus, ACTIVE.status)) {
      status = ACTIVE;
    } else if(StringUtils.equalsIgnoreCase(sessionStatus, EXPIRED.status)) {
      status = EXPIRED;
    } else if(StringUtils.equalsIgnoreCase(sessionStatus, ALL.status)) {
      status = ALL;
    } else {
      log.error("Session status does not exist: " + sessionStatus);
    }
    
    return status;
  }
  
}
