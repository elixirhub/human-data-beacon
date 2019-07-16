package org.ega_archive.elixircore.security;

import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

public class CustomSimpleUrlFailureHandler extends SimpleUrlAuthenticationFailureHandler {

  @Override
  public void onAuthenticationFailure(javax.servlet.http.HttpServletRequest request,
                                      javax.servlet.http.HttpServletResponse response,
                                      org.springframework.security.core.AuthenticationException exception)
      throws java.io.IOException, javax.servlet.ServletException {
    super.onAuthenticationFailure(request, response, exception);
  }

}
