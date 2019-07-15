package org.ega_archive.elixircore.security;

import org.ega_archive.elixircore.constant.CoreConstants;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

public class RestWebAuthenticationDetails extends WebAuthenticationDetails {

  private final String token;

  public RestWebAuthenticationDetails(HttpServletRequest request) {
    super(request);
    this.token = request.getHeader(CoreConstants.TOKEN_HEADER);
  }

  public String getToken() {
    return token;
  }
}