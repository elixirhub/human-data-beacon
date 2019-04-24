package org.ega_archive.elixirbeacon.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ega_archive.elixirbeacon.convert.Operations;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VariantAnnotation {

  @JsonIgnore
  private String[] fields = {"cellBaseInfo", "datasetAlleleResponses", "info", "variantHandover"};

  private String cellBaseInfo;

  private List<DatasetAlleleResponse> datasetAlleleResponses;

  private Map<String, Object> info;

  private List<Handover> variantHandover;

  public Map<String, Object> toMap(Map<String, Object> accessLevelFields, boolean isAuthenticated) {
    return Operations
        .convertToMap(this, this.fields, "variantAnnotation", accessLevelFields, isAuthenticated);
  }

}
