package org.ega_archive.elixircore.exception;


import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

public class TypeNotFoundException extends RuntimeException implements Serializable {

  private static final long serialVersionUID = -3247069889499867687L;
  
  private String type;

  public TypeNotFoundException(String message) {
    super(message);
  }
  
  public TypeNotFoundException(String message, String type) {
    super(message);
    this.type = type;
  }

  public TypeNotFoundException(String message, Throwable t) {
    super(message, t);
  }

  @Override
  public String toString() {
    String s = getClass().getName();
    String message = getLocalizedMessage();
    if (StringUtils.isNotBlank(type)) {
      message += " (" + type + ")";
    }
    return (message != null) ? (s + ": " + message) : s;
  }
  
}
