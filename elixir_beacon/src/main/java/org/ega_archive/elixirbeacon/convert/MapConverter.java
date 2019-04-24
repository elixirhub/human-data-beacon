package org.ega_archive.elixirbeacon.convert;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.ega_archive.elixirbeacon.dto.AccessLevelResponse;
import org.ega_archive.elixirbeacon.dto.Beacon;
import org.ega_archive.elixirbeacon.dto.BeaconAlleleResponse;
import org.ega_archive.elixirbeacon.dto.BeaconGenomicRegionResponse;
import org.ega_archive.elixirbeacon.dto.BeaconGenomicSnpResponse;
import org.ega_archive.elixirbeacon.service.BeaconAccessLevelService;
import org.ega_archive.elixircore.exception.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MapConverter {

  @Value("${access.levels.default.yaml.filename}")
  private String defaultAccessLevelFileName;

  @Autowired
  private BeaconAccessLevelService accessLevelService;

  public Map<String, Object> convertToMap(Object response, boolean authorized) {
    return convertToMap(response, authorized, defaultAccessLevelFileName);
  }

  public Map<String, Object> convertToMap(Object response, boolean authorized, String fileName) {
    Map<String, Object> mapResponse = new HashMap<>();

    AccessLevelResponse accessLevelResponse = accessLevelService
        .listAccessLevels(null, null, null, true, true, fileName);

    if (response instanceof Beacon) {
      mapResponse = ((Beacon) response).toMap(accessLevelResponse, authorized);
    } else if (response instanceof BeaconAlleleResponse) {
      mapResponse = ((BeaconAlleleResponse) response).toMap(accessLevelResponse, authorized);
    } else if (response instanceof BeaconGenomicRegionResponse) {
      mapResponse = ((BeaconGenomicRegionResponse) response).toMap(accessLevelResponse, authorized);
    } else if (response instanceof BeaconGenomicSnpResponse) {
      mapResponse = ((BeaconGenomicSnpResponse) response).toMap(accessLevelResponse, authorized);
    } else {
      throw new NotImplementedException("Conversion to map not implemented yet for this type");
    }
    return mapResponse;
  }

}
