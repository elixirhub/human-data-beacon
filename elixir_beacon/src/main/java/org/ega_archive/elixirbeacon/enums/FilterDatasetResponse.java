package org.ega_archive.elixirbeacon.enums;

import org.apache.commons.lang3.StringUtils;
import org.ega_archive.elixircore.exception.TypeNotFoundException;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FilterDatasetResponse {

  ALL("all"), // All datasets
  HIT("hit"), // Only datasets where the allele was found
  MISS("miss"), // Only datasets where the allele was not found
  NULL("null"), // Only datasets where the allele was not found either found (e.g. an error happened)
  NONE("none"); // No dataset

  private String filter;

  public static FilterDatasetResponse parse(String filterStr) {
    FilterDatasetResponse filter = NONE; // Default value
    if (StringUtils.equalsIgnoreCase(filterStr, ALL.filter)) {
      filter = ALL;
    } else if (StringUtils.equalsIgnoreCase(filterStr, HIT.filter)) {
      filter = HIT;
    } else if (StringUtils.equalsIgnoreCase(filterStr, MISS.filter)) {
      filter = MISS;
    } else if (StringUtils.equalsIgnoreCase(filterStr, NONE.filter)) {
      filter = NONE;
    } else if(StringUtils.isNotBlank(filterStr)) {
      throw new TypeNotFoundException("Type not found: " + filterStr);
    }
    return filter;
  }

  public boolean isIncludeDatasets() {
    boolean includeDatasets = false;
    if (this != NONE) {
      includeDatasets = true;
    }
    return includeDatasets;
  }

}
