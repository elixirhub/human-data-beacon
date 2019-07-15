package org.ega_archive.elixircore.exception;

import org.springframework.security.core.AuthenticationException;

public class SessionInvalidException extends AuthenticationException {


  /**
   *
   */
  private static final long serialVersionUID = 982239373247524413L;

  public SessionInvalidException(String message) {
    super(message);
  }

  public SessionInvalidException(String message, Throwable t) {
    super(message, t);
  }

}