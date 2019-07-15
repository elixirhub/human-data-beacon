package org.ega_archive.elixircore.controller.interfaces;

import org.ega_archive.elixircore.dto.Base;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
// Can't use this because the BaseController class implements all the interfaces
// @RequestMapping("/logs")
public interface Log {

  @RequestMapping(value = "/logs", method = RequestMethod.PUT)
  public Base<String> setLogLevel(
      @RequestParam(value = "level", required = true) String level);

  @RequestMapping(value = "/logs/{classname}", method = RequestMethod.PUT)
  public Base<String> setLogLevelForClass(
      @PathVariable(value = "classname") String classname,
      @RequestParam(value = "level", required = true) String level);

  @RequestMapping(value = "/logs/level", method = RequestMethod.GET)
  public Base<String> getLogLevel();

  @RequestMapping(value = "/logs/levels", method = RequestMethod.GET)
  public Base<String> getLogLevels();

}
