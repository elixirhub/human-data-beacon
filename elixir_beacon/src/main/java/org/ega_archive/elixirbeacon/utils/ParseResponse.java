package org.ega_archive.elixirbeacon.utils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.babelomics.csvs.lib.models.DiseaseGroup;
import org.babelomics.csvs.lib.ws.QueryResponse;
import org.ega_archive.elixircore.exception.RestRuntimeException;
import org.ega_archive.elixircore.exception.ServerDownException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class ParseResponse {

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  public <T> QueryResponse<T> parseCsvsResponse(String url, Class<T> clazz) {
    ResponseEntity response = null;
    try {
      response = this.restTemplate
          .exchange(url, HttpMethod.GET, new HttpEntity(null, null), String.class, new Object[0]);
    } catch (
        ResourceAccessException var15) {
      throw new ServerDownException(var15.getMessage());
    }

    JavaType type = this.objectMapper.getTypeFactory()
        .constructParametricType(QueryResponse.class, new Class[]{clazz});
    QueryResponse basicDTO = null;
    try {
      basicDTO = this.objectMapper.readValue((String) response.getBody(), type);
    } catch (
        IOException var14) {
      throw new RestRuntimeException("500",
          "Exception deserializing object: " + (String) response.getBody() + "\n" + var14
              .getMessage());
    }
    log.debug("response: {}", basicDTO);

    return basicDTO;
  }

}
