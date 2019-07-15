package org.ega_archive.elixirbeacon.dto;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.ega_archive.elixircore.constant.CoreConstants;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DatasetAlleleResponse {

  // Identifier of the dataset, as defined in `BeaconDataset`
  private String datasetId;

  // Indicator of whether the given allele was observed in the dataset. This should be non-null,
  // unless there was an error, in which case `error` has to be not null.
  private boolean exists;

  // Dataset-specific error. This should be non-null in exceptional situations only, in which case
  // `exists` has to be null.
  private Error error;

  // Frequency of this allele in the dataset. Between 0 and 1, inclusive.
  private BigDecimal frequency;

  // Number of variants matching the allele request in the dataset.
  private Long variantCount;

  // Number of calls matching the allele request in the dataset.
  private Long callCount;

  // Number of samples matching the allele request in the dataset.
  private Long sampleCount;

  // Additional note or description of the response.
  private String note = CoreConstants.OK;

  // URL to an external system, such as a secured beacon or a system providing
  // more information about a given allele (RFC 3986 format).
  private String externalUrl;

  // Additional structured metadata, key-value pairs
  private Map<String, String> info;

}
