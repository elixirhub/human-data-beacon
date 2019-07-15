package org.ega_archive.elixircore.exception;

public class ServiceUnavailableException extends RuntimeException {

  /**
   *
   */
  private static final long serialVersionUID = 5525760954771502906L;

  public ServiceUnavailableException(String message) {
    super(message);
  }

  public ServiceUnavailableException(String message, Throwable t) {
    super(message, t);
  }

}
