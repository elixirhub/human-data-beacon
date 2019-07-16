package org.ega_archive.elixircore.interceptor;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import lombok.extern.log4j.Log4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.MDC;
import org.ega_archive.elixircore.correlation.CorrelationIdHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

@Log4j
public class CorrelationIdInterceptor implements ClientHttpRequestInterceptor {

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body,
      ClientHttpRequestExecution execution) throws IOException {

    HttpHeaders headers = request.getHeaders();
    if (headers == null) {
      headers = new HttpHeaders();
    }
    
    String currentCorrId = null;
    List<String> values = headers.get(CorrelationIdHolder.CORRELATION_ID_HEADER);
    if(values != null && !values.isEmpty()) {
      currentCorrId = values.get(0);
    }

    if (StringUtils.isBlank(currentCorrId)) {
      String correlationId = CorrelationIdHolder.getId();
      if(StringUtils.isBlank(correlationId)) {
        correlationId = System.getProperty("service.name") + "-" + UUID.randomUUID().toString();
        log.trace("No correlationId in Holder. Generated : " + correlationId);
        CorrelationIdHolder.setId(correlationId);
        MDC.put("correlationId", correlationId);
      }
      headers.set(CorrelationIdHolder.CORRELATION_ID_HEADER, correlationId);
      log.trace("Added correlation Id: " + correlationId + " to rest request");
    }
    
    return execution.execute(request, body);
  }

}
