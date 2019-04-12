package org.ega_archive.elixircore.interceptor;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class AuthorizationHeaderInterceptor implements ClientHttpRequestInterceptor {

  @Autowired
  private HttpServletRequest incomingRequest;

  @Override
  public ClientHttpResponse intercept(HttpRequest outgoingRequest, byte[] body,
      ClientHttpRequestExecution execution) throws IOException {

    HttpHeaders headers = outgoingRequest.getHeaders();
    if (headers == null) {
      headers = new HttpHeaders();
    }

    if (incomingRequest != null && !StringUtils.isBlank(incomingRequest.getHeader("Authorization"))) {
      headers.add("Authorization", incomingRequest.getHeader("Authorization"));
//      headers.add("X-My-Super-Token", "myValue");
    }
    return execution.execute(outgoingRequest, body);
  }

}
