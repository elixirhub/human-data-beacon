package org.ega_archive.elixircore.helper;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class CustomRequestMatcher implements RequestMatcher {
  
  private final List<String> matchingUrls;

  private final HttpMethod method;

  public CustomRequestMatcher(List<String> matchingUrls, HttpMethod method) {
    this.matchingUrls = matchingUrls;
    this.method = method;
  }

  @Override
  public boolean matches(HttpServletRequest request) {
    String uri = request.getRequestURI();
    int pathParamIndex = uri.indexOf(';');

    if (pathParamIndex > 0) {
      // strip everything after the first semi-colon
      uri = uri.substring(0, pathParamIndex);
    }

    boolean matchesHttpMethod = StringUtils.equals(request.getMethod(), method.toString());

    if ("".equals(request.getContextPath())) {
      boolean matchesUrl = false;
      for(String url : matchingUrls) {
        if(uri.endsWith(url)) {
          matchesUrl = true;
          break;
        }
      }
      return matchesUrl && matchesHttpMethod;
    }

    boolean matchesUrl = false;
    for(String url : matchingUrls) {
      if(uri.endsWith(request.getContextPath() + url)) {
        matchesUrl = true;
        break;
      }
    }
    return matchesUrl && matchesHttpMethod;
  }

}
