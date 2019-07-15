package org.ega_archive.elixircore.controller.interfaces;

import org.ega_archive.elixircore.dto.Base;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
// Can't use this because the BaseController class implements all the interfaces
// @RequestMapping("/metrics/ega/")
public interface Metrics {

  @RequestMapping(value = "/metrics/ega/reset", method = RequestMethod.PUT)
  public Base<String> reset();

}
