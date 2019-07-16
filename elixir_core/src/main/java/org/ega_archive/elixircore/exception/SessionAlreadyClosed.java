package org.ega_archive.elixircore.exception;

import org.springframework.security.core.AuthenticationException;

public class SessionAlreadyClosed extends AuthenticationException {

  /**
   *
   */
  private static final long serialVersionUID = 709213087560512520L;

  public SessionAlreadyClosed(String message) {
    super(message);
  }

  public SessionAlreadyClosed(String message, Throwable t) {
    super(message, t);
  }

}
