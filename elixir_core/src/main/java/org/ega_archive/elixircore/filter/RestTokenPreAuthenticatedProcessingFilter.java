package org.ega_archive.elixircore.filter;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ega_archive.elixircore.constant.CoreConstants;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RestTokenPreAuthenticatedProcessingFilter extends
                                                       AbstractPreAuthenticatedProcessingFilter {

  private final String TOKEN_HEADER = CoreConstants.TOKEN_HEADER;

  @Override
  protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
    String token = request.getHeader(TOKEN_HEADER);
    if (token == null) { //In this case, we do not found a token, so anonymous user is used
      return null;
    }
    return token;
  }

  @Override
  protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
    return "N/A";
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            AuthenticationException failed) throws IOException, ServletException {
    super.unsuccessfulAuthentication(request, response, failed);
    try {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, failed.getMessage());
    } catch (IOException e) {
      logger.error(e);
    }
  }
}
