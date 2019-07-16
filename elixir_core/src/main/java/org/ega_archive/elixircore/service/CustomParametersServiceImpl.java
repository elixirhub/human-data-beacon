package org.ega_archive.elixircore.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;

@Service
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.INTERFACES)
public class CustomParametersServiceImpl implements CustomParametersService {

  @Autowired
  private HttpServletRequest request;
  private boolean DEFAULT_DEBUG_INCLUDED = false;

  @Override
  public boolean isDebugRequest() {
    if (request != null) {
      String parameter = request.getParameter("debug");
      if (parameter == null) {
        return DEFAULT_DEBUG_INCLUDED;
      } else {
        return !"false".equals(parameter.toLowerCase());
      }
    }
    return DEFAULT_DEBUG_INCLUDED;
  }
}
