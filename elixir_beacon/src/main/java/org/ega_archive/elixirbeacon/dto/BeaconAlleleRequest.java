package org.ega_archive.elixirbeacon.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ega_archive.elixirbeacon.convert.Operations;
import org.ega_archive.elixirbeacon.enums.FilterDatasetResponse;

@Data
//@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BeaconAlleleRequest {

  @JsonIgnore
  private String[] fields = {"alternateBases", "referenceBases", "referenceName", "start",
      "startMin", "startMax", "end", "endMin", "endMax", "variantType", "assemblyId", "datasetIds",
      "includeDatasetResponses", "filters"};

  private String alternateBases;

  private String referenceBases;

  private String referenceName;

  private Integer start;

  private Integer startMin;

  private Integer startMax;

  private Integer end;

  private Integer endMin;

  private Integer endMax;

  private String variantType;

  private String assemblyId;

  private List<String> datasetIds;

  private FilterDatasetResponse includeDatasetResponses;

  private List<String> filters;

  public Map<String, Object> toMap(Map<String, Object> accessLevelFields, String fieldName,
      boolean isAuthenticated) {
    return Operations
        .convertToMap(this, this.fields, "beaconAlleleRequest", accessLevelFields, isAuthenticated);
  }

}
