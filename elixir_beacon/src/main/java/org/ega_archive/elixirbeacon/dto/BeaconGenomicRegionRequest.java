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
public class BeaconGenomicRegionRequest {

  @JsonIgnore
  private String[] fields = {"alternateBases", "referenceBases", "referenceName", "start", "end",
      "assemblyId", "datasetIds", "includeDatasetResponses", "filters"};

  private String referenceBases;

  private String referenceName;

  private Integer start;

  private Integer end;

  private String assemblyId;

  private List<String> datasetIds;

  private FilterDatasetResponse includeDatasetResponses;

  private List<String> filters;

  public Map<String, Object> toMap(Map<String, Object> accessLevelFields, boolean isAuthenticated,
      String fieldName) {
    Map<String, Object> accessLevelsFields = (Map<String, Object>) accessLevelFields
        .get(fieldName);

    return Operations
        .convertToMap(this, this.fields, null, accessLevelsFields, isAuthenticated);
  }

}
