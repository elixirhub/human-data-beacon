package org.ega_archive.elixircore.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import java.util.ArrayList;
import java.util.List;
import org.ega_archive.elixircore.ApplicationContextProvider;
import org.ega_archive.elixircore.event.sender.RestEventErrorHandler;
import org.ega_archive.elixircore.interceptor.CorrelationIdInterceptor;
import org.ega_archive.elixircore.interceptor.CustomClientAddressInterceptor;
import org.ega_archive.elixircore.interceptor.CustomTokenInterceptor;
import org.ega_archive.elixircore.interceptor.LogRestInterceptor;
import org.ega_archive.elixircore.security.MySessionToken;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
@Import({Cache.class, Web.class})
public class App {

  @Bean
  public ApplicationContextProvider applicationContextProvider() {
    return new org.ega_archive.elixircore.ApplicationContextProvider();
  }

  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    // This module serializes/deserializes DateTime values using the milliseconds form
    objectMapper.registerModule(new JodaModule());
    objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

    objectMapper.configure(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN, true);

    return objectMapper;
  }

  @Bean
  public RestTemplate restTemplate() {
    RestTemplate restTemplate = new RestTemplate();

    List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
    if (messageConverters == null) {
      messageConverters = new ArrayList<HttpMessageConverter<?>>();
    }
    boolean found = false;
    for (HttpMessageConverter<?> converter : messageConverters) {
      if (converter instanceof MappingJackson2HttpMessageConverter) {
        found = true;
        // To ensure that the Jackson converter uses an object mapper with Joda module
        ((MappingJackson2HttpMessageConverter) converter).setObjectMapper(objectMapper());
      }
    }
    if (!found) {
      messageConverters.add(addJacksonConverter());
    }
    restTemplate.setErrorHandler(new RestEventErrorHandler());
    addInterceptors(restTemplate);
    return restTemplate;
  }

  private MappingJackson2HttpMessageConverter addJacksonConverter() {
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setObjectMapper(objectMapper());
    return converter;
  }

  private void addInterceptors(RestTemplate restTemplate) {
    List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
    interceptors.add(new CustomClientAddressInterceptor());
    interceptors.add(new CustomTokenInterceptor());
    interceptors.add(new CorrelationIdInterceptor());
    interceptors.add(new LogRestInterceptor());
    restTemplate.setInterceptors(interceptors);
  }

  /**
   * This bean is used for storing the service's system user token.
   * 
   * @return
   */
  @Bean
  public MySessionToken mySessionToken() {
    return new MySessionToken(null);
  }

}
