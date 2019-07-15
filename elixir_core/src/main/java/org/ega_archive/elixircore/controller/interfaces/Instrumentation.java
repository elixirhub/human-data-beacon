package org.ega_archive.elixircore.controller.interfaces;

import org.ega_archive.elixircore.constant.ParamName;
import org.ega_archive.elixircore.dto.Base;
import org.ega_archive.elixircore.enums.ServiceAction;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
// Can't use this because the BaseController class implements all the interfaces
// @RequestMapping("/status")
public interface Instrumentation {

  @RequestMapping(value = "/status", method = RequestMethod.GET)
  public Base<String> getStatus();

  @RequestMapping(value = "/status", method = RequestMethod.PUT)
  public Base<String> setStatus(
      @RequestParam(value = "value") String action);

  @RequestMapping(value = "/status/states", method = RequestMethod.GET)
  public Base<String> getStatusStates();

  @RequestMapping(value = "/status/commands", method = RequestMethod.GET)
  public Base<ServiceAction> getCommands();
  
  @RequestMapping(value = "/cache", method = RequestMethod.DELETE)
  public Base<String> deleteCache(
      @RequestParam(value = ParamName.CACHE_TYPE, required = true) String cacheName);
  
}
