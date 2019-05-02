package org.ega_archive.elixirbeacon.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
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

  private List<Handover> resultHandover;

  private List<Handover> beaconHandover;

  public BeaconAlleleResponse(BeaconGenomicSnpResponse genomicResponse) {
    BeaconAlleleRequest alleleRequest = new BeaconAlleleRequest();
    alleleRequest.setAssemblyId(genomicResponse.getRequest().getAssemblyId());
    alleleRequest.setStart(genomicResponse.getRequest().getStart());
    alleleRequest.setReferenceBases(genomicResponse.getRequest().getReferenceBases());
    alleleRequest.setAlternateBases(genomicResponse.getRequest().getAlternateBases());
    alleleRequest.setDatasetIds(genomicResponse.getRequest().getDatasetIds());
    alleleRequest.setReferenceName(genomicResponse.getRequest().getReferenceName());
    alleleRequest.setIncludeDatasetResponses(genomicResponse.getRequest().getIncludeDatasetResponses());
    this.alleleRequest = alleleRequest;

    this.beaconId = genomicResponse.getBeaconId();
    this.apiVersion = genomicResponse.getApiVersion();
    this.datasetAlleleResponses = genomicResponse.getDatasetAlleleResponses();
    this.error = genomicResponse.getError();
    this.exists = genomicResponse.isExists();
    this.beaconHandover = genomicResponse.getBeaconHandover();
  }

  public BeaconAlleleResponse(BeaconGenomicRegionResponse genomicResponse) {
    BeaconAlleleRequest alleleRequest = new BeaconAlleleRequest();
    alleleRequest.setAssemblyId(genomicResponse.getRequest().getAssemblyId());
    alleleRequest.setStart(genomicResponse.getRequest().getStart());
    alleleRequest.setReferenceBases(genomicResponse.getRequest().getReferenceBases());
    //alleleRequest.setAlternateBases(genomicResponse.getRequest().getAlternateBases());
    alleleRequest.setDatasetIds(genomicResponse.getRequest().getDatasetIds());
    alleleRequest.setReferenceName(genomicResponse.getRequest().getReferenceName());
    alleleRequest.setIncludeDatasetResponses(genomicResponse.getRequest().getIncludeDatasetResponses());
    this.alleleRequest = alleleRequest;

    this.beaconId = genomicResponse.getBeaconId();
    this.apiVersion = genomicResponse.getApiVersion();
    //this.datasetAlleleResponses = genomicResponse.getDatasetAlleleResponses();
    this.error = genomicResponse.getError();
    this.exists = genomicResponse.isExists();
    this.beaconHandover = genomicResponse.getBeaconHandover();
  }

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
