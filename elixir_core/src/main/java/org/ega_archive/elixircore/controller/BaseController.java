package org.ega_archive.elixircore.controller;

import java.io.IOException;
import java.util.Arrays;

import org.ega_archive.elixircore.constant.CoreConstants;
import org.ega_archive.elixircore.constant.ParamName;
import org.ega_archive.elixircore.controller.interfaces.Instrumentation;
import org.ega_archive.elixircore.controller.interfaces.Log;
import org.ega_archive.elixircore.controller.interfaces.Metrics;
import org.ega_archive.elixircore.controller.interfaces.RestEventListener;
import org.ega_archive.elixircore.dto.Base;
import org.ega_archive.elixircore.dto.message.ServiceMessage;
import org.ega_archive.elixircore.enums.CacheTypes;
import org.ega_archive.elixircore.enums.ServiceAction;
import org.ega_archive.elixircore.enums.Status;
import org.ega_archive.elixircore.exception.NotImplementedException;
import org.ega_archive.elixircore.exception.PreConditionFailed;
import org.ega_archive.elixircore.service.AppService;
import org.ega_archive.elixircore.service.LocatorService;
import org.ega_archive.elixircore.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

// @RestController
// Technically speaking this annotation is no needed, it is used for documentation purposes
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SYSTEM')")
public abstract class BaseController implements Instrumentation, Metrics, Log, RestEventListener {

  private static final String NOT_IMPLEMENTED = "Not implemented";
  private static final String ERROR_LEVEL_NOT_EXISTS = "The level does not exist";

  @Autowired
  private LogService logService;

  @Autowired
  private AppService appService;

  @Autowired
  private LocatorService locatorService;

  // LOG
  @Override
  @RequestMapping(value = "/logs", method = RequestMethod.PUT)
  public Base<String> setLogLevel(
      @RequestParam(value = ParamName.LEVEL, required = true) String level) {
    try {
      logService.setLoglevel(level);
    } catch (PreConditionFailed e) {
      throw new RuntimeException(ERROR_LEVEL_NOT_EXISTS);
    }
    return new Base<>(level);
  }

  @Override
  @RequestMapping(value = "/logs/{classname}", method = RequestMethod.PUT)
  public Base<String> setLogLevelForClass(
      @PathVariable(value = "classname") String classname,
      @RequestParam(value = "level", required = true) String level) {
    try {
      logService.setLoglevel(classname, level);
    } catch (PreConditionFailed e) {
      throw new RuntimeException(ERROR_LEVEL_NOT_EXISTS);
    }
    return new Base<>(level);
  }

  @Override
  @RequestMapping(value = "/logs/level", method = RequestMethod.GET)
  public Base<String> getLogLevel() {
    return new Base<>(logService.getLoglevel().toString());
  }

  @Override
  @RequestMapping(value = "/logs/levels", method = RequestMethod.GET)
  public Base<String> getLogLevels() {
    return new Base<>(logService.getLoglevels().toString());
  }

  // INSTRUMENTATION
  private Base<String> insStart() {
    appService.setStatus(Status.STARTED);
    return new Base<>(appService.getStatus().toString());
  }

  private Base<String> insStop() {
    if (appService.getContext() == null) {
      return new Base<>("No Context to stop");
    }
    try {
      appService.setStatus(Status.STOPPING);
      return new Base<>(Status.STOPPING.toString());
    } finally {
      new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            Thread.sleep(500L);
          } catch (InterruptedException ex) {
            // Swallow exception and continue
          }
          appService.stop();
        }
      }).start();
    }
  }

  private Base<String> insPause() {
    appService.setStatus(Status.PAUSED);
    return new Base<>(appService.getStatus().toString());
  }

  private Base<String> insReload() {
    throw new NotImplementedException(NOT_IMPLEMENTED);
  }

  @Override
  @RequestMapping(value = "/status", method = RequestMethod.GET)
  public Base<String> getStatus() {
    return new Base<>(appService.getStatus().toString());
  }

  @Override
  @RequestMapping(value = "/status/states", method = RequestMethod.GET)
  public Base<String> getStatusStates() {
    return new Base<>(Arrays.asList(Status.values()).toString());
  }

  @Override
  @RequestMapping(value = "/status", method = RequestMethod.PUT)
  public Base<String> setStatus(
      @RequestParam(value = ParamName.VALUE) String action) {
    ServiceAction serviceAction = ServiceAction.parse(action);
    switch (serviceAction) {
      case START:
        return insStart();
      case STOP:
        return insStop();
      case PAUSE:
        return insPause();
      case RELOAD:
        return insReload();
      default:
        throw new NotImplementedException(NOT_IMPLEMENTED);
    }
  }

  @Override
  @RequestMapping(value = "/status/commands", method = RequestMethod.GET)
  public Base<ServiceAction> getCommands() {
    return new Base<>(Arrays.asList(ServiceAction.values()));
  }

  @Override
  @RequestMapping(value = "/cache", method = RequestMethod.DELETE)
  public Base<String> deleteCache(@RequestParam(value = ParamName.CACHE_TYPE, required = true) String cacheName) {
    CacheTypes cacheNameParsed = CacheTypes.parse(cacheName);
    switch (cacheNameParsed) {
      case SERVICE_LOCATION:
        locatorService.resetAllEntries();
        break;
      default:
        break;
    }
    return new Base<>(CoreConstants.OK);
  }

  // EVENTS
  @Override
  @RequestMapping(value = "/events", method = RequestMethod.POST)
  public Base<String> receive(ServiceMessage message) throws JsonParseException,
      JsonMappingException, IOException {

    throw new NotImplementedException(NOT_IMPLEMENTED);
  }

  // METRICS
  @Override
  @RequestMapping(value = "/metrics/ega/reset", method = RequestMethod.PUT)
  public Base<String> reset() {
    throw new NotImplementedException(NOT_IMPLEMENTED);
  }

//  @Override
//  @RequestMapping(value = "/hello/{message}", method = RequestMethod.GET)
//  public Base<String> hello(@PathVariable(value = "message") String message) {
//    if (StringUtils.isBlank(message)) {
//      message = "hello";
//    }
//    return new Base<>(String.valueOf(message.hashCode()));
//  }
}
