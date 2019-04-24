package org.ega_archive.elixirbeacon.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ega_archive.elixirbeacon.convert.Operations;
import org.ega_archive.elixircore.constant.CoreConstants;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DatasetAlleleResponse {

  @JsonIgnore
  private String[] fields = {"datasetId", "exists", "error", "frequency", "variantCount",
      "callCount", "sampleCount", "note", "externalUrl", "datasetHandover", "info"};

  private String datasetId;

  private boolean exists;

  private Error error;

  private BigDecimal frequency;

  private Long variantCount;

  private Long callCount;

  private Long sampleCount;

  private String note = CoreConstants.OK;

  private String externalUrl;

  private List<Handover> datasetHandover;

  private Map<String, Object> info;

  public Map<String, Object> toMap(Map<String, Object> accessLevelFields, boolean isAuthenticated) {
    return Operations
        .convertToMap(this, this.fields, "datasetAlleleResponse", accessLevelFields, isAuthenticated);
  }

}
