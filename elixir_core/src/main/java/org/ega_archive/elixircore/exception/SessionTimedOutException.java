package org.ega_archive.elixircore.exception;

import org.springframework.security.core.AuthenticationException;

public class SessionTimedOutException extends AuthenticationException {

  /**
   *
   */
  private static final long serialVersionUID = 4181844031715183711L;

  public SessionTimedOutException() {
    super("Session Timed out");
  }

  public SessionTimedOutException(String message) {
    super(message);
  }

  public SessionTimedOutException(String message, Throwable t) {
    super(message, t);
  }

}
