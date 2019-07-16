package org.ega_archive.elixircore.exception;

public class EventTypeException extends RuntimeException {

  /**
   *
   */
  private static final long serialVersionUID = 8397611544223878723L;

  public EventTypeException(String message) {
    super(message);
  }

  public EventTypeException(String message, Throwable t) {
    super(message, t);
  }
}
