package org.ega_archive.elixircore.util;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;

public class StoredProcedureUtils {
  
  private static final String NULL = "null";

  @Deprecated
  public static <T> void registerParameter(StoredProcedureQuery storedProcedure, int position,
      Class<T> clazz, T value) {
    
    storedProcedure.registerStoredProcedureParameter(position, clazz, ParameterMode.IN);
    if (clazz.equals(String.class) && StringUtils.isBlank((String) value)) {
      storedProcedure.setParameter(position, NULL);
    } else {
      storedProcedure.setParameter(position, value);
    }
  }

  @Deprecated
  public static void addPagination(Pageable pageable, boolean isACount,
      StoredProcedureQuery storedProcedure, int position) {
    if (!isACount) {
      // Pagination parameters
      int skip = pageable == null ? 0 : pageable.getPageNumber();
      int limit = pageable == null ? 0 : pageable.getPageSize();

      registerParameter(storedProcedure, position, Integer.class, skip);
      position++;

      registerParameter(storedProcedure, position, Integer.class, limit);
      position++;
    }
  }
  
  public static String joinArrayOfInteger(List<Integer> list) {
    String joined = list.stream().map(id -> String.valueOf(id)).collect(Collectors.joining(","));
    return joined;
  }

  public static String joinArrayOfString(List<String> list, String delimiter) {
    if (StringUtils.isBlank(delimiter)) {
      delimiter = ",";
    }
    String joined = list.stream().map(id -> String.valueOf(id)).collect(Collectors.joining(delimiter));
    return joined;
  }
  
}
