package org.ega_archive.elixircore.enums;

import org.apache.commons.lang3.StringUtils;
import org.ega_archive.elixircore.exception.NotFoundException;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CacheTypes {
  
  SERVICE_LOCATION("service_location");
  
  private String name;
  
  public static CacheTypes parse(String name) {
    CacheTypes cacheName = null;
    if(StringUtils.equalsIgnoreCase(name, SERVICE_LOCATION.name)) {
      cacheName = SERVICE_LOCATION;
    } else {
      throw new NotFoundException("Cache name not valid", name);
    }
    return cacheName;
  }

}
