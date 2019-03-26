package org.ega_archive.elixirbeacon.dto;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccessLevelResponse {

  private String beaconId;

  private Error error;

  private Map<String, Object> fields;

  private Map<String, Object> datasets;

}
