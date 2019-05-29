package org.ega_archive.elixirbeacon.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
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
public class Variant {

  @JsonIgnore
  private String[] fields = {"variantDetails", "datasetAlleleResponses", "variantAnnotations",
      "variantHandover", "info"};

  private VariantDetail variantDetails;

  private List<DatasetAlleleResponse> datasetAlleleResponses;

  private Map<String, Object> variantAnnotations;

  private List<Handover> variantHandover;

  private Map<String, Object> info;

  public Map<String, Object> toMap(Map<String, Object> accessLevelFields, boolean isAuthenticated) {
    return Operations
        .convertToMap(this, this.fields, "variant", accessLevelFields, isAuthenticated);
  }

}
