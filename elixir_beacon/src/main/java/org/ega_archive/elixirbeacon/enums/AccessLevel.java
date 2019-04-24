package org.ega_archive.elixirbeacon.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.ega_archive.elixircore.exception.TypeNotFoundException;

@Getter
@AllArgsConstructor
public enum AccessLevel {
  PUBLIC("PUBLIC"), REGISTERED("REGISTERED"), CONTROLLED("CONTROLLED"), NOT_SUPPORTED(
      "NOT_SUPPORTED");

  private String level;

  public AccessLevel parseValue(String value) {
    AccessLevel accessLevel = null;
    if (StringUtils.equalsIgnoreCase(PUBLIC.level, value)) {
      accessLevel = PUBLIC;
    } else if (StringUtils.equalsIgnoreCase(REGISTERED.level, value)) {
      accessLevel = REGISTERED;
    } else if (StringUtils.equalsIgnoreCase(CONTROLLED.level, value)) {
      accessLevel = PUBLIC;
    } else if (StringUtils.equalsIgnoreCase(NOT_SUPPORTED.level, value)) {
      accessLevel = NOT_SUPPORTED;
    } else {
      throw new TypeNotFoundException("Access level not valid", value);
    }
    return accessLevel;
  }

}
