package org.ega_archive.elixircore.event.sender;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.io.IOUtils;
import org.ega_archive.elixircore.dto.Base;
import org.ega_archive.elixircore.exception.RestRuntimeException;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

import lombok.extern.log4j.Log4j;

@Log4j
public class RestEventErrorHandler implements ResponseErrorHandler {

  private ResponseErrorHandler errorHandler = new DefaultResponseErrorHandler();

  public boolean hasError(ClientHttpResponse response) throws IOException {
    return errorHandler.hasError(response);
  }

  public void handleError(ClientHttpResponse response) throws IOException {

    String responseBody = null;

    String message = response.getStatusText();
    String code = String.valueOf(response.getRawStatusCode());

    // try to convert the response to a body
    try {
      responseBody = IOUtils.toString(response.getBody());
    } catch (IOException e) {
      log.error("Could not convert body to string", e);
    }

    if (responseBody != null) {
      ObjectMapper mapper = new ObjectMapper();
      Base<?> result;
      try {
        result = mapper.readValue(responseBody, Base.class);
        message = result.getHeader().getUserMessage();
        code = result.getHeader().getCode();
      } catch (IOException e) {
        log.error("Could not deserialize body", e);
      }
    }

    throw new RestRuntimeException(code, message);
  }
}
