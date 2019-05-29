package org.ega_archive.elixirbeacon.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ega_archive.elixirbeacon.convert.Operations;
import org.ega_archive.elixirbeacon.enums.ErrorCode;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Error {

  @JsonIgnore
  private String[] fields = {"errorCode", "errorMessage"};

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
  private String errorMessage;

  public Error(ErrorCode errorCode, String message) {
    this.errorCode = errorCode;
    this.errorMessage = message;
  }

  public Object toMap(Map<String, Object> accessLevelFields, boolean isAuthenticated) {
    return Operations
        .convertToMap(this, this.fields, "beaconError", accessLevelFields, isAuthenticated);
  }

}
