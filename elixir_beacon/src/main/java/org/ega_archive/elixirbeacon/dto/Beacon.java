package org.ega_archive.elixirbeacon.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ega_archive.elixirbeacon.constant.BeaconConstants;
import org.ega_archive.elixirbeacon.convert.Operations;
import org.ega_archive.elixircore.constant.CoreConstants;
import org.joda.time.DateTime;

@Data
//@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Beacon {

  @JsonIgnore
  private String[] fields = {"id", "name", "apiVersion", "organization", "description", "version",
      "welcomeUrl", "alternativeUrl", "createDateTime", "updateDateTime", "datasets",
      "sampleAlleleRequests", "info"};

  private String id = BeaconConstants.BEACON_ID;

  private String name = BeaconConstants.BEACON_NAME;

  private String apiVersion = BeaconConstants.API;

  private Organization organization = new Organization();

  private String description = BeaconConstants.BEACON_DESCRIPTION;

  private String version = CoreConstants.API_VERSION;

  private String welcomeUrl = BeaconConstants.BEACON_HOMEPAGE;

  private String alternativeUrl = BeaconConstants.BEACON_ALTERNATIVE_URL;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = BeaconConstants.ISO8601_DATE_TIME_PATTERN)
  private DateTime createDateTime = BeaconConstants.BEACON_CREATED;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = BeaconConstants.ISO8601_DATE_TIME_PATTERN)
  private DateTime updateDateTime = BeaconConstants.BEACON_EDITED;

  private List<Dataset> datasets;

  private List<BeaconAlleleRequest> sampleAlleleRequests;

  private Map<String, Object> info;

  public Map<String, Object> toMap(AccessLevelResponse accessLevels, boolean isAuthenticated) {
    return Operations
        .convertToMap(this, this.fields, "beacon", accessLevels.getFields(), isAuthenticated);
  }

}
