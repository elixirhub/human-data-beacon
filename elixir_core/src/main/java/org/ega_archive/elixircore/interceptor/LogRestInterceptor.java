package org.ega_archive.elixircore.interceptor;

import java.io.IOException;

import lombok.extern.log4j.Log4j;

import org.apache.commons.lang3.StringUtils;
import org.ega_archive.elixircore.dto.auth.User;
import org.ega_archive.elixircore.util.UserUtils;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

@Log4j
public class LogRestInterceptor implements ClientHttpRequestInterceptor {

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body,
      ClientHttpRequestExecution execution) throws IOException {

    String userId = null;
    User user = UserUtils.getUserFromContext();
    if (user != null) {
      userId = user.getUserId();
    }

    String userInfo = "User[" + userId + "] ";
    String urlInfo = "URL[" + request.getURI().getPath() + "] ";
    String queryInfo = "Query[" + request.getURI().getQuery() + "] ";
    String headersInfo = "Headers[" + request.getHeaders() + "] ";
    
    String bodyStr = new String(body);
    String bodyInfo = "";
    if (user == null) {
      // It is a login request: only log username
      String[] tokens = bodyStr.split("&");
      for (String param : tokens) {
        if (StringUtils.startsWith(param, "username")) {
          bodyInfo = "Body (partial) [" + param + "] ";
        }
      }
    } else {
      bodyInfo = "Body [" + bodyStr + "] ";
    }
    log.info(userInfo + urlInfo + queryInfo + headersInfo + bodyInfo);

    return execution.execute(request, body);
  }

}
