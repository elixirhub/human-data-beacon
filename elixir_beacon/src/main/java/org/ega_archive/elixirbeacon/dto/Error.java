package org.ega_archive.elixirbeacon.dto;

import org.ega_archive.elixirbeacon.enums.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Error {

  // Numeric status code
  private ErrorCode errorCode;

  // Error message.
  // Accepted values:
  // - HTTP error code 400: Generic error.
  // - HTTP error code 401: Unauthenticated users cannot access this dataset
  // - HTTP error code 404: Dataset not found
  // - HTTP error code 400: Missing mandatory parameters: referenceName,
  // position/start and/or assemblyId
  // - HTTP error code 400: The reference genome of this dataset X does not
  // match the provided value
  // - HTTP error code 400: Invalid alternateBases parameter, it can only be [ACTG]+
  // - HTTP error code 400: Invalid referenceBases parameter, it can only be [ACTG]+
  private String message;

}
