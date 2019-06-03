package org.ega_archive.elixirbeacon.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BeaconRequest {

  private String variantType;

  private String alternateBases;

  private String referenceBases;

  private String referenceName;

  private Integer start;

  private Integer startMin;

  private Integer startMax;

  private Integer end;

  private Integer endMin;

  private Integer endMax;

  private String assemblyId;

  private String info; // metadata field

  private String mcpdParamExample; // example mcpd field

  private List<String> datasetIds;

  private String includeDatasetResponses;

}
