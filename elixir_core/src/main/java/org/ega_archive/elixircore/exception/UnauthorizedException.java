package org.ega_archive.elixircore.exception;

import org.springframework.security.core.AuthenticationException;

public class UnauthorizedException extends AuthenticationException {

  /**
   *
   */
  private static final long serialVersionUID = 4181844031715183711L;

  public UnauthorizedException() {
    super("Unauthorized");
  }

  public UnauthorizedException(String message) {
    super(message);
  }

  public UnauthorizedException(String message, Throwable t) {
    super(message, t);
  }

}
