package org.ega_archive.elixircore.helper;

import org.apache.log4j.Level;
import org.ega_archive.elixircore.constant.ParamName;
import org.springframework.data.domain.Sort;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import lombok.extern.log4j.Log4j;

@Log4j
public class CommonQueryHelper {

  //Todo add more params
  public static MultiValueMap<String, String> toMap(CommonQuery commonQuery) {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
    if(commonQuery == null) {
      return map;
    }
    map.add(ParamName.SKIP, commonQuery.getSkip().toString());
    map.add(ParamName.LIMIT, commonQuery.getLimit().toString());
    // TODO add sort
    return map;
  }

  public static CommonQuery parseQuery(Map<String, String> params, Sort sort) {
    CommonQuery commonQuery = parseQuery(params);
    if (sort != null) {
      commonQuery.setSort(sort);
    }
    return commonQuery;
  }

  public static CommonQuery parseQuery(Map<String, String> params) {

    CommonQuery commonQuery = new CommonQuery();

    if (params == null || params.isEmpty()) {
      return commonQuery;
    }

    commonQuery.setInclude(helper(params, "include", "inc"));
    commonQuery.setExclude(helper(params, "exclude", "exc"));

    // TODO check that include and exclude are coherent

    String limit = params.get("limit");
    if (limit != null) {
      Integer value = null;
      try {
        value = Integer.valueOf(limit);
      } catch (NumberFormatException ex) {
        value = 10;
      }
      commonQuery.setLimit(value);
    }

    String skip = params.get("skip") == null ? params.get("offset") : params.get("skip");
    if (skip != null) {
      Integer value = null;
      try {
        value = Integer.valueOf(skip);
      } catch (NumberFormatException ex) {
        value = 0;
      }
      commonQuery.setSkip(value);
    }

    if (log.getLevel() == Level.DEBUG) {
      log.debug("limit = " + commonQuery.getLimit());
      log.debug("skip = " + commonQuery.getSkip());
      log.debug("includes = " + commonQuery.getInclude());
      log.debug("exludes = " + commonQuery.getExclude());
    }

    return commonQuery;
  }

  private static List<String> helper(Map<String, String> params, String clude, String cludeAlias) {
    if (params.containsKey(clude) || params.containsKey(cludeAlias)) {
      String result = params.get(clude);
      if (result == null) {
        result = params.get(cludeAlias);
        if (result != null) {
          return Arrays.asList(result.split(","));
        }
      } else {
        return Arrays.asList(result.split(","));
      }
    }
    return null;
  }

}
