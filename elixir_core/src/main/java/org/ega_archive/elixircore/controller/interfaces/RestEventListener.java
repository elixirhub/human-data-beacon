package org.ega_archive.elixircore.controller.interfaces;

import java.io.IOException;

import org.ega_archive.elixircore.dto.Base;
import org.ega_archive.elixircore.dto.message.ServiceMessage;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@RestController
// Can't use this because the BaseController class implements all the interfaces
// @RequestMapping("/events")
public interface RestEventListener {

  @RequestMapping(value = "/events", method = RequestMethod.POST)
  public Base<String> receive(
      @RequestBody ServiceMessage message)
      throws JsonParseException, JsonMappingException, IOException;

}
