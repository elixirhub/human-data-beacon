package org.ega_archive.elixirbeacon.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
  GENERIC_ERROR(0), // HTTP error code 400. To be used when the error is not among below.
  UNAUTHORIZED(1), // HTTP error code 401
  NOT_FOUND(2); // HTTP error code 404

  private int code;

}
