package org.ega_archive.elixircore.event.sender;

import java.util.List;

import org.ega_archive.elixircore.dto.Base;
import org.ega_archive.elixircore.enums.ServiceType;
import org.ega_archive.elixircore.interfaces.EventsSender;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;


public interface RestEventSender extends EventsSender<String, HttpMethod, HttpHeaders> {

  /**
   *
   * @param url
   * @param method
   * @param params
   * @param urlParams TODO
   * @param headers
   * @param type
   * @return
   */
  public <T> T send(String url, HttpMethod method, MultiValueMap<String, String> params,
      MultiValueMap<String, String> urlParams, HttpHeaders headers, Class<T> type);

  public <T> T sendSpecificInstance(String serviceId, String partialUrl, HttpMethod method,
      MultiValueMap<String, String> params, HttpHeaders headers, Class<T> type);

  public <T> T send(ServiceType serviceType, String partialUrl, HttpMethod method,
      MultiValueMap<String, String> params, MultiValueMap<String, String> urlParams,
      HttpHeaders headers, Class<T> typeclass);
  
  public <T> T send(ServiceType serviceType, String partialUrl, HttpMethod method,
      Class<T> typeclass);

  /**
   * This short signature sends a CommonQuery object with skip=0 and limit=0;
   * 
   * @param serviceType
   * @param partialUrl
   * @param method
   * @param typeclass
   * @return
   */
  public <T> Base<T> sendMicroservice(ServiceType serviceType, String partialUrl,
      HttpMethod method, Class<T> typeclass);

  /**
   * @param params : parameters sent in the body.
   * @param urlParams : parameters that must be send in the URL.
   * @param isJsonObject : true if params is a JSON object; false otherwise.
   */

  public <T, V, Y, Z> Base<T> sendMicroservice(String url, HttpMethod method, V params,
      MultiValueMap<Y, Z> urlParams, HttpHeaders headers, Class<T> typeclass, boolean isJsonObject);

  public <T, V, Y, Z> Base<T> sendMicroservice(String url, HttpMethod method, V params,
      MultiValueMap<Y, Z> urlParams, HttpHeaders headers, Class<T> typeclass,
      MediaType contentType, MediaType acceptType);

  public <T, V, Y, Z> Base<T> sendMicroservice(ServiceType serviceType, String partialUrl,
      HttpMethod method, V params, MultiValueMap<Y, Z> urlParams, HttpHeaders headers,
      Class<T> typeclass, boolean isJsonObject);

  public <T, V, Y, Z> Base<T> sendMicroservice(ServiceType serviceType, String partialUrl,
      HttpMethod method, V params, MultiValueMap<Y, Z> urlParams, HttpHeaders headers,
      Class<T> typeclass, MediaType contentType, MediaType acceptType);

  public <T, V, Y, Z> List<T> sendMicroserviceObject(ServiceType serviceType, String partialUrl,
      HttpMethod method, V params, MultiValueMap<Y, Z> urlParams, HttpHeaders headers,
      Class<T> typeclass, boolean isJsonObject);
  
  public <T> List<T> sendMicroserviceObject(ServiceType serviceType, String partialUrl,
      HttpMethod method, Class<T> typeclass);

  /**
   * Rest call to a specific instance of one service.
   * 
   * @param serviceId : serviceId of that instance
   * @param partialUrl
   * @param method
   * @param params
   * @param urlParams
   * @param headers
   * @param typeclass
   * @param isJsonObject
   * @return
   */
  public <T, V, Y, Z> Base<T> sendMicroserviceSpecificInstance(String serviceId, String partialUrl,
      HttpMethod method, V params, MultiValueMap<Y, Z> urlParams, HttpHeaders headers,
      Class<T> typeclass, boolean isJsonObject);

  public <T> Base<T> sendMicroserviceSpecificInstance(String serviceId, String partialUrl,
      HttpMethod method, Class<T> typeclass);

}
