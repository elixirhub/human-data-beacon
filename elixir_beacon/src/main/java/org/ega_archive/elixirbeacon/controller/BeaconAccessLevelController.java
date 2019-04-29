package org.ega_archive.elixirbeacon.controller;

import java.util.List;
import org.ega_archive.elixirbeacon.dto.AccessLevelResponse;
import org.ega_archive.elixirbeacon.service.BeaconAccessLevelService;
import org.ega_archive.elixircore.constant.ParamName;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/beacon")
public class BeaconAccessLevelController {

  private BeaconAccessLevelService beaconAccessLevelService;

  public BeaconAccessLevelController(BeaconAccessLevelService beaconAccessLevelService) {
    this.beaconAccessLevelService = beaconAccessLevelService;
  }

  @GetMapping(value = "/access_levels")
  public AccessLevelResponse getAccessLevels(
      @RequestParam(value = ParamName.BEACON_FIELDS, required = false) List<String> fields,
      @RequestParam(value = ParamName.BEACON_DATASET_IDS, required = false) List<String> datasetStableIds,
      @RequestParam(value = ParamName.BEACON_LEVEL, required = false) String level,
      @RequestParam(value = ParamName.BEACON_INCLUDE_FIELD_DETAILS, required = false, defaultValue = "false") boolean includeFieldDetails,
      @RequestParam(value = ParamName.BEACON_INCLUDE_DATASET_DIFFERENCES, required = false, defaultValue = "false") boolean includeDatasetDetails
  ) {

    return beaconAccessLevelService.listAccessLevels(fields, datasetStableIds, level, includeFieldDetails, includeDatasetDetails);
  }

}
