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
public class BeaconGenomicRegionResponse {

  @JsonIgnore
  private String[] fields = {"beaconId", "apiVersion", "exists", "error", "request",
      "variantsFound", "info", "resultsHandover", "beaconHandover"};

  private String beaconId = BeaconConstants.BEACON_ID;

  private String apiVersion = BeaconConstants.API;

  private boolean exists;

  private Error error;

  private BeaconGenomicRegionRequest request;

  private List<Variant> variantsFound;

  private Map<String, Object> info;

  private List<Handover> resultsHandover;

  private List<Handover> beaconHandover;

  public Map<String, Object> toMap(AccessLevelResponse accessLevels, boolean isAuthenticated) {
    return Operations
        .convertToMap(this, this.fields, "beaconGenomicRegionResponse", accessLevels.getFields(),
            isAuthenticated);
  }

}
