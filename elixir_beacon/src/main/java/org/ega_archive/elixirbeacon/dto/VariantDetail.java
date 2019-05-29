package org.ega_archive.elixirbeacon.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ega_archive.elixirbeacon.convert.Operations;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VariantDetail {

  @JsonIgnore
  private String[] fields = {"referenceBases", "alternateBases", "variantType",
      "start", "end"};

  private String chromosome;

  private String referenceBases;

  private String alternateBases;

  private String variantType;

  private Integer start;

  private Integer end;

  public Map<String, Object> toMap(Map<String, Object> accessLevelFields, boolean isAuthenticated) {
    return Operations
        .convertToMap(this, this.fields, "variantDetail", accessLevelFields, isAuthenticated);
  }

}
