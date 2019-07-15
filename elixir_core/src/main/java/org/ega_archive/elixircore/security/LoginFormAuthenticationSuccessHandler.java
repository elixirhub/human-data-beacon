package org.ega_archive.elixircore.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ega_archive.elixircore.dto.Base;
import org.ega_archive.elixircore.dto.auth.SessionUser;
import org.ega_archive.elixircore.service.HttpConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

public class LoginFormAuthenticationSuccessHandler
    extends SimpleUrlAuthenticationSuccessHandler {

  @Autowired
  private HttpConversionService httpConversionService;
  private RequestCache requestCache = new HttpSessionRequestCache();

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws ServletException,
                                                                            IOException {

    //REMOVE password //maybe is already not here, have to check
    Object principal = authentication.getPrincipal();
    Base<SessionUser> returnUser = new Base<SessionUser>((SessionUser) principal);
    httpConversionService.convert(returnUser, request, response);
  }

  public void setRequestCache(RequestCache requestCache) {
    this.requestCache = requestCache;
  }
}