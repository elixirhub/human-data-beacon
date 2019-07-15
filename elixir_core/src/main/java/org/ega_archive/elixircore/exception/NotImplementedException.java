package org.ega_archive.elixircore.exception;

public class NotImplementedException extends RuntimeException {

  /**
   *
   */
  private static final long serialVersionUID = -4210026963401610611L;

  public NotImplementedException(String message) {
    super(message);
  }

  public NotImplementedException(String message, Throwable t) {
    super(message, t);
  }

}
