package org.ega_archive.elixirbeacon.enums.consent_code;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * This enumeration should contain the same values as the database table consent_code_category_table. 
 *
 */
@Getter
@AllArgsConstructor
public enum ConsentCodeCategory {

  PRIMARY("PRIMARY"), SECONDARY("SECONDARY"), REQUIREMENTS("REQUIREMENT");

  private String category;

  public static ConsentCodeCategory parse(String value) {
    ConsentCodeCategory category = null;
    if (StringUtils.equalsIgnoreCase(value, PRIMARY.category)) {
      category = PRIMARY;
    } else if (StringUtils.equalsIgnoreCase(value, SECONDARY.category)) {
      category = SECONDARY;
    } else if (StringUtils.equalsIgnoreCase(value, REQUIREMENTS.category)) {
      category = REQUIREMENTS;
    } else {
      throw new RuntimeException("Can't parse this category value " + value);
    }
    return category;
  }

}

