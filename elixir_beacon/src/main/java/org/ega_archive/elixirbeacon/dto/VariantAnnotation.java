package org.ega_archive.elixirbeacon.dto;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VariantAnnotation {

  private String cellBaseInfo;

  private List<DatasetAlleleResponse> datasetAlleleResponses;

  private Map<String, Object> info;

  private List<Handover> variantHandover;

}
