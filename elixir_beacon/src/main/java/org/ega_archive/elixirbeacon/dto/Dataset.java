package org.ega_archive.elixirbeacon.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ega_archive.elixirbeacon.constant.BeaconConstants;
import org.ega_archive.elixirbeacon.dto.datause.DataUseCondition;
import org.joda.time.DateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dataset {

  // Unique identifier of the dataset
  private String id;

  // Name of the dataset
  private String name;

  // Description of the dataset
  private String description;

  // Assembly identifier (GRC notation, e.g. `GRCh37`).
  private String assemblyId;

  // The time the dataset was created (ISO 8601 format)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = BeaconConstants.ISO8601_DATE_TIME_PATTERN)
  private DateTime createDateTime;

  // The time the dataset was updated in (ISO 8601 format)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = BeaconConstants.ISO8601_DATE_TIME_PATTERN)
  private DateTime updateDateTime;

  // Data use conditions for this dataset
  private DataUseCondition dataUseConditions;
  
  // Version of the dataset
  private String version;

  // Total number of variants in the dataset
  private BigInteger variantCount;

  // Total number of calls in the dataset
  private BigInteger callCount;

  // Total number of samples in the dataset
  private BigInteger sampleCount;

  // URL to an external system providing more dataset information (RFC 3986 format).
  private String externalUrl;

  // Additional structured metadata, key-value pairs.
  private Map<String, String> info;
  
//  public void addDataUseCondition(DataUseCondition condition) {
//    if(dataUseConditions == null) {
//      dataUseConditions = new ArrayList<DataUseCondition>();
//    }
//    dataUseConditions.add(condition);
//  }
  
}
