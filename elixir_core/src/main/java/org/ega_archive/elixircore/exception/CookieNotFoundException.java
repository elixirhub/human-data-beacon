package org.ega_archive.elixircore.exception;

public class CookieNotFoundException extends RuntimeException {

  /**
   *
   */
  private static final long serialVersionUID = -8297266754468381978L;

  public CookieNotFoundException(String message) {
    super(message);
  }

  public CookieNotFoundException(String message, Throwable t) {
    super(message, t);
  }

}
