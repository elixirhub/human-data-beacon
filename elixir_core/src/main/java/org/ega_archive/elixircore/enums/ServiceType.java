package org.ega_archive.elixircore.enums;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

@Log4j
@Getter
@AllArgsConstructor
public enum ServiceType {
  CONFIG("config"), AUTH("auth"), SESSION("session"), EVENTING("eventing"), NOTIFICATION(
      "notification"), PROCESS("process"), CATALOG("catalog"), ARCHIVE("archive"), REQUEST(
      "request"), WF_HANDLER("wf_handler"), MONITORING("monitoring"), EGA_BEACON(
      "ega_beacon"), SUBMITTER_PORTAL("submitter_portal"), REQUESTER_PORTAL("requester_portal"),
  MANAGER_PORTAL("manager_portal"), WORKER("worker"), DBOX("dbox");

  private String value;

  public static ServiceType parse(String value) {
    ServiceType type = null;
    if (StringUtils.equalsIgnoreCase(value, CONFIG.value)) {
      type = CONFIG;
    } else if (StringUtils.equalsIgnoreCase(value, AUTH.value)) {
      type = AUTH;
    } else if (StringUtils.equalsIgnoreCase(value, SESSION.value)) {
      type = SESSION;
    } else if (StringUtils.equalsIgnoreCase(value, EVENTING.value)) {
      type = EVENTING;
    } else if (StringUtils.equalsIgnoreCase(value, NOTIFICATION.value)) {
      type = NOTIFICATION;
    } else if (StringUtils.equalsIgnoreCase(value, PROCESS.value)) {
      type = PROCESS;
    } else if (StringUtils.equalsIgnoreCase(value, CATALOG.value)) {
      type = CATALOG;
    } else if (StringUtils.equalsIgnoreCase(value, ARCHIVE.value)) {
      type = ARCHIVE;
    } else if (StringUtils.equalsIgnoreCase(value, REQUEST.value)) {
      type = REQUEST;
    } else if (StringUtils.equalsIgnoreCase(value, WF_HANDLER.value)) {
      type = WF_HANDLER;
    } else if (StringUtils.equalsIgnoreCase(value, MONITORING.value)) {
      type = MONITORING;
    } else if (StringUtils.equalsIgnoreCase(value, EGA_BEACON.value)) {
      type = EGA_BEACON;
    } else if (StringUtils.equalsIgnoreCase(value, WORKER.value)) {
      type = WORKER;
    } else if (StringUtils.equalsIgnoreCase(value, DBOX.value)) {
      type = DBOX;
    } else if (StringUtils.equalsIgnoreCase(value, SUBMITTER_PORTAL.value)) {
      type = SUBMITTER_PORTAL;
    } else if (StringUtils.equalsIgnoreCase(value, REQUESTER_PORTAL.value)) {
      type = REQUESTER_PORTAL;
    } else if (StringUtils.equalsIgnoreCase(value, MANAGER_PORTAL.value)) {
      type = MANAGER_PORTAL;
    } else {
      log.error("Service type not valid!");
    }
    return type;
  }

}
