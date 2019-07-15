package org.ega_archive.elixircore.exception;

public class ServiceNotFoundException extends RuntimeException {

  /**
   *
   */
  private static final long serialVersionUID = 5957546237487125710L;

  public ServiceNotFoundException(String message) {
    super(message);
  }

  public ServiceNotFoundException(String message, Throwable t) {
    super(message, t);
  }

}
