package org.ega_archive.elixirbeacon.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ega_archive.elixirbeacon.constant.BeaconConstants;
import org.ega_archive.elixirbeacon.convert.Operations;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Organization {

  @JsonIgnore
  private String[] fields = {"id", "name", "description", "address", "welcomeUrl", "contactUrl",
      "logoUrl", "info"};

  private String id = BeaconConstants.ORGANIZATION_ID;

  private String name = BeaconConstants.ORGANIZATION_NAME;

  private String description = BeaconConstants.ORGANIZATION_DESCRIPTION;

  private String address = BeaconConstants.ORGANIZATION_ADDRESS;

  private String welcomeUrl = BeaconConstants.ORGANIZATION_HOMEPAGE;

  private String contactUrl = BeaconConstants.ORGANIZATION_CONTACT;

  private String logoUrl = BeaconConstants.ORGANIZATION_LOGO;

  private Map<String, String> info;

  public Object toMap(Map<String, Object> accessLevelFields, boolean isAuthenticated) {
    return Operations
        .convertToMap(this, this.fields, "beaconOrganization", accessLevelFields, isAuthenticated);
  }

}
