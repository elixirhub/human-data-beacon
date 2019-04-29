package org.ega_archive.elixirbeacon.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ega_archive.elixirbeacon.constant.BeaconConstants;
import org.ega_archive.elixirbeacon.convert.Operations;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BeaconGenomicSnpResponse {

  @JsonIgnore
  private String[] fields = {"beaconId", "exists", "error", "request", "apiVersion",
      "datasetAlleleResponses", "info", "resultsHandover", "beaconHandover"};

  private String beaconId = BeaconConstants.BEACON_ID;

  private boolean exists;

  private Error error;

  private BeaconGenomicSnpRequest request;

  private String apiVersion = BeaconConstants.API;

  private List<DatasetAlleleResponse> datasetAlleleResponses;

  private Map<String, Object> info;

  private List<Handover> resultsHandover; // move dbsnp here in snp queries

  private List<Handover> beaconHandover;

  public Map<String, Object> toMap(AccessLevelResponse accessLevels, boolean isAuthenticated) {
    return Operations
        .convertToMap(this, this.fields, "beaconGenomicSnpResponse", accessLevels.getFields(),
            isAuthenticated);
  }

}
