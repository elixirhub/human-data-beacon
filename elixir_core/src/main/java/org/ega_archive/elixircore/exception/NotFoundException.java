package org.ega_archive.elixircore.exception;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {

  private static final long serialVersionUID = -8540636701618280855L;

  private String resourceId;

  public NotFoundException(String message, String resourceId) {
    super(message);
    this.resourceId = resourceId;
  }

  public NotFoundException(String message) {
    super(message);
  }

  public NotFoundException(String message, Throwable t) {
    super(message, t);
  }

  @Override
  public String toString() {
    String s = getClass().getName();
    String message = getLocalizedMessage();
    if (StringUtils.isNotBlank(resourceId)) {
      message += " (" + resourceId + ")";
    }
    return (message != null) ? (s + ": " + message) : s;
  }
  
}
