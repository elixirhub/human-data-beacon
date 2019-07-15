package org.ega_archive.elixircore.security;


import org.ega_archive.elixircore.dto.Base;
import org.ega_archive.elixircore.dto.auth.SessionUser;
import org.ega_archive.elixircore.enums.ServiceType;
import org.ega_archive.elixircore.event.sender.RestEventSender;
import org.ega_archive.elixircore.service.HttpConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CustomSimpleUrlLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

  @Autowired
  private RestEventSender restEventSender;

  @Autowired
  private HttpConversionService httpConversionService;

  public CustomSimpleUrlLogoutSuccessHandler() {
    this.setDefaultTargetUrl("/");
  }

  public CustomSimpleUrlLogoutSuccessHandler(String defaultTargetURL) {
    this.setDefaultTargetUrl(defaultTargetURL);
  }

  @Override
  public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                              Authentication authentication) throws
                                                             IOException,
                                                             ServletException {
    if (authentication != null && authentication.isAuthenticated()) {
      Object details = authentication.getDetails();
      if (details != null && details instanceof RestWebAuthenticationDetails) {
        String token = ((RestWebAuthenticationDetails) details).getToken();
        Base<SessionUser> sessionUser = restEventSender
            .sendMicroservice(ServiceType.SESSION, "/sessions/" + token, HttpMethod.DELETE,
                              null,
                              null, null, SessionUser.class, false);
        httpConversionService.convert(sessionUser, request, response);
      }
    }
    super.onLogoutSuccess(request, response, authentication);
  }
}
