package org.ega_archive.elixircore.exception;

public class PreConditionFailed extends RuntimeException {

  /**
   *
   */
  private static final long serialVersionUID = 7603937020284912266L;

  public PreConditionFailed(String message) {
    super(message);
  }

  public PreConditionFailed(String message, Throwable t) {
    super(message, t);
  }

}
