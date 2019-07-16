package org.ega_archive.elixircore.security;

import org.ega_archive.elixircore.constant.ParamName;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RestAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

  public RestAuthenticationEntryPoint() {
    super(ParamName.LOGIN_ENDPOINT);
  }

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
                       AuthenticationException authException) throws IOException {
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
  }

}