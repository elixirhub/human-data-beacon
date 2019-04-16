package org.ega_archive.elixirbeacon.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.babelomics.csvs.lib.ws.QueryResponse;
import org.ega_archive.elixircore.exception.RestRuntimeException;
import org.ega_archive.elixircore.exception.ServerDownException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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
    return (QueryResponse<T>) parseResponse(url, QueryResponse.class, clazz);
  }

  private ResponseEntity runTheCall(String url, String token) {
    ResponseEntity response = null;
    try {
      MultiValueMap<String, String> header = null;
      if (StringUtils.isNotBlank(token)) {
        header = new LinkedMultiValueMap<>();
        header.add("token", token);
      }
      response = this.restTemplate
          .exchange(url, HttpMethod.GET, new HttpEntity(null, header), String.class, new Object[0]);
    } catch (ResourceAccessException ex) {
      throw new ServerDownException(ex.getMessage());
    }
    return response;
  }

  /**
   * Parses the response using the parametrized class sent as an argument.
   * @param url
   * @param token
   * @param clazzT: a parametrized class (i.e. List<U>).
   * @param clazzU: class to be used to parametrize class T (i.e. T<String>).
   * @param <T>
   * @param <U>
   * @return
   */
  public <T, U> Object parseResponse(String url, String token, Class<T> clazzT, Class<U> clazzU) {
    ResponseEntity response = runTheCall(url, token);

    JavaType type = this.objectMapper.getTypeFactory()
        .constructParametricType(clazzT, new Class[]{clazzU});
    Object basicDTO = null;
    try {
      basicDTO = this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue((String) response.getBody(), type);
    } catch (IOException ex) {
      throw new RestRuntimeException("500",
          "Exception deserializing object: " + response.getBody() + "\n" + ex.getMessage());
    }
    log.debug("response: {}", basicDTO);
    return basicDTO;
  }

  public <T, U> Object parseResponse(String url, Class<T> clazzT, Class<U> clazzU) {
    return parseResponse(url, null, clazzT, clazzU);
  }

  /**
   * Returns the response as a HashMap.
   * @param url
   * @param token
   * @return
   */
  public Map<String, Object> parseResponse(String url, String token) {
    ResponseEntity response = runTheCall(url, token);

    Map<String, Object> basicDTO = null;
    try {
      basicDTO = this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue((String) response.getBody(), HashMap.class);
    } catch (IOException ex) {
      throw new RestRuntimeException("500",
              "Exception deserializing object: " + response.getBody() + "\n" + ex.getMessage());
    }
    log.debug("response: {}", basicDTO);

    return basicDTO;
  }
}