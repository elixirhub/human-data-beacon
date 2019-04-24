package org.ega_archive.elixirbeacon.service;

import java.util.List;
import org.ega_archive.elixirbeacon.dto.AccessLevelResponse;

public interface BeaconAccessLevelService {

  AccessLevelResponse listAccessLevels(List<String> fields,
      List<String> datasetStableIds, String level, boolean includeFieldDetails,
      boolean includeDatasetDetails);

  AccessLevelResponse listAccessLevels(List<String> fields,
      List<String> datasetStableIds, String level, boolean includeFieldDetails,
      boolean includeDatasetDetails, String fileName);

}
