package org.ega_archive.elixirbeacon.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ega_archive.elixirbeacon.convert.Operations;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Handover {

  @JsonIgnore
  private String[] fields = {"handoverType", "note", "url"};

  private HandoverType handoverType;

  private String note;

  private String url;

  public Map<String, Object> toMap(Map<String, Object> accessLevelFields, String key, boolean isAuthenticated) {
    return Operations
        .convertToMap(this, this.fields, key, accessLevelFields, isAuthenticated);
  }

}
