package org.ega_archive.elixircore.exception;

public class ServerDownException extends RuntimeException {

  /**
   *
   */
  private static final long serialVersionUID = -8979329942799858133L;

  public ServerDownException(String message) {
    super(message);
  }

  public ServerDownException(String message, Throwable t) {
    super(message, t);
  }

}
