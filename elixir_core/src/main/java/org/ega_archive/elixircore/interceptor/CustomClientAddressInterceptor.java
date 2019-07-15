package org.ega_archive.elixircore.interceptor;

import java.io.IOException;
import java.net.InetAddress;

import javax.servlet.http.HttpServletRequest;

import org.ega_archive.elixircore.util.NetUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class CustomClientAddressInterceptor implements ClientHttpRequestInterceptor {

  private HttpServletRequest request;

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body,
      ClientHttpRequestExecution execution) throws IOException {
    
    String value = null;
    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    if (requestAttributes != null) {
      this.request = ((ServletRequestAttributes) requestAttributes).getRequest();
      value = this.request.getHeader(NetUtils.X_CLIENT_IP_ADDRESS);
    } else {
      // We are outside a web request
      InetAddress ip = InetAddress.getLocalHost();
      if (ip != null) {
        value = ip.getHostAddress();
      }
    }

    HttpHeaders headers = request.getHeaders();
    if (headers == null) {
      headers = new HttpHeaders();
    }
    headers.add(NetUtils.X_CLIENT_IP_ADDRESS, value);

    return execution.execute(request, body);
  }

}
