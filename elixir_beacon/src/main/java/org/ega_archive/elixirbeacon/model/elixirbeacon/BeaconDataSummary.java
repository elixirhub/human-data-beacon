package org.ega_archive.elixirbeacon.model.elixirbeacon;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class BeaconDataSummary {

  private static final long serialVersionUID = 1L;

  private String id;

  private Integer datasetId;

  private Integer variantCnt;

  private Integer callCnt;

  private Integer sampleCnt;

  private BigDecimal frequency;

  private Integer numVariants;


}
