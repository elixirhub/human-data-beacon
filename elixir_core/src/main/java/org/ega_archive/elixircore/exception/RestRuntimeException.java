package org.ega_archive.elixircore.exception;

import lombok.Getter;

public class RestRuntimeException extends RuntimeException {

  /**
   *
   */
  @Getter
  private static final long serialVersionUID = 1L;

  private String code;

  public RestRuntimeException(String code) {
    super();
    this.code = code;
  }

  public RestRuntimeException(String code, String message) {
    super(message);
    this.code = code;
  }

  public RestRuntimeException(String code, String message, Throwable t) {
    super(message, t);
    this.code = code;
  }

  public String getCode() {
    return this.code;
  }

}
