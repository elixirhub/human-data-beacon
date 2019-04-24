package org.ega_archive.elixirbeacon.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ega_archive.elixirbeacon.constant.BeaconConstants;
import org.ega_archive.elixirbeacon.convert.Operations;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BeaconAlleleResponse {

  @JsonIgnore
  private String[] fields = {"beaconId", "exists", "error", "alleleRequest", "apiVersion",
      "datasetAlleleResponses"};

  private String beaconId = BeaconConstants.BEACON_ID;

  private boolean exists;

  private Error error;

  private BeaconAlleleRequest alleleRequest;

  private String apiVersion = BeaconConstants.API;

  private List<DatasetAlleleResponse> datasetAlleleResponses;

  public void addDatasetAlleleResponse(DatasetAlleleResponse datasetAlleleResponse) {
    if (this.datasetAlleleResponses == null) {
      this.datasetAlleleResponses = new ArrayList<DatasetAlleleResponse>();
    }
    this.datasetAlleleResponses.add(datasetAlleleResponse);
  }

  public Map<String, Object> toMap(AccessLevelResponse accessLevels, boolean isAuthenticated) {
    return Operations
        .convertToMap(this, this.fields, "beaconAlleleResponse", accessLevels.getFields(),
            isAuthenticated);
  }

}
