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
public class HandoverType {

  @JsonIgnore
  private String[] fields = {"id", "label"};

  private String id;

  private String label;

  public Object toMap(Map<String, Object> accessLevelFields, boolean isAuthenticated) {
    return Operations
        .convertToMap(this, this.fields, "handoverType", accessLevelFields, isAuthenticated);
  }

}
