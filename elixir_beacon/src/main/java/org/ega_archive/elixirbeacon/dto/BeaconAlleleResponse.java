package org.ega_archive.elixirbeacon.dto;

import java.util.ArrayList;
import java.util.List;

import org.ega_archive.elixirbeacon.constant.BeaconConstants;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BeaconAlleleResponse {

  // Identifier of the beacon, as defined in `Beacon`.
  private String beaconId = BeaconConstants.BEACON_ID;

  /*
   * Indicator of whether the beacon observed the given allele. The value of this field should be
   * null if `error` is not null and true if and only if at least one of the datasets responded
   * true, i.e. at least one of the `exists` fields nested in `datasetAlleleResponses` is true.
   */
  private boolean exists;

  /*
   * Beacon-specific error. This should be non-null in exceptional situations only, in which case
   * `exists` has to be null.
   */
  private Error error;

  // Allele request as interpreted by the beacon.
  private BeaconAlleleRequest alleleRequest;

  // Version of the API provided by the beacon.
  private String apiVersion = BeaconConstants.API;

  // Indicator of whether the beacon has observed the allele.
  // This should be non-null if `includeDatasetResponses` in the corresponding
  // `BeaconAlleleRequest` is not `NONE`, and null otherwise.
  private List<DatasetAlleleResponse> datasetAlleleResponses;

  public void addDatasetAlleleResponse(DatasetAlleleResponse datasetAlleleResponse) {
    if (this.datasetAlleleResponses == null) {
      this.datasetAlleleResponses = new ArrayList<DatasetAlleleResponse>();
    }
    this.datasetAlleleResponses.add(datasetAlleleResponse);
  }

}
