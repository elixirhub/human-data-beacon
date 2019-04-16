package org.ega_archive.elixirbeacon.service.impl.csvs;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.babelomics.csvs.lib.models.DiseaseGroup;
import org.babelomics.csvs.lib.ws.QueryResponse;
import org.ega_archive.elixirbeacon.dto.Beacon;
import org.ega_archive.elixirbeacon.dto.BeaconAlleleResponse;
import org.ega_archive.elixirbeacon.dto.BeaconGenomicSnpRequest;
import org.ega_archive.elixirbeacon.dto.Dataset;
import org.ega_archive.elixirbeacon.enums.VariantType;
import org.ega_archive.elixirbeacon.service.ElixirBeaconService;
import org.ega_archive.elixirbeacon.service.GenomicQuery;
import org.ega_archive.elixirbeacon.utils.ParseResponse;
import org.ega_archive.elixircore.exception.NotImplementedException;
import org.ega_archive.elixircore.helper.CommonQuery;
import org.ega_archive.elixircore.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Slf4j
@Primary
@Service
public class ElixirBeaconServiceCsvsImpl implements ElixirBeaconService {

  @Autowired
  private GenomicQuery genomicQuery;

  @Autowired
  private ParseResponse parseResponse;

  @Autowired
  private ObjectMapper objectMapper;

  @Override
  public Beacon listDatasets(CommonQuery commonQuery, String referenceGenome) {
    String url = "http://csvs.clinbioinfosspa.es:8080/csvs/rest/diseases/list";

    QueryResponse<DiseaseGroup> diseaseGroupQueryResponse = parseResponse
        .parseCsvsResponse(url, DiseaseGroup.class);

    Beacon beacon = new Beacon();

    List<Dataset> datasets = new ArrayList<>();
    for (DiseaseGroup disease : diseaseGroupQueryResponse.getResult()) {
      datasets.add(Dataset.builder()
          .id(String.valueOf(disease.getGroupId()))
          .name(disease.getName())
          .assemblyId("GRCh37")
          .sampleCount((long) disease.getSamples())
          .variantCount((long) disease.getVariants())
          .build());
    }
    beacon.setDatasets(datasets);

    url = "http://csvs.clinbioinfosspa.es:8080/csvs/rest/files/samples";
    QueryResponse<Integer> integerQueryResponse = parseResponse
        .parseCsvsResponse(url, Integer.class);
    Integer numIndividuals = integerQueryResponse.getResult().get(0);

    Map<String, Object> info = new HashMap<>();
    info.put("Number of individuals", numIndividuals.toString());
    beacon.setInfo(info);

    return beacon;
  }

  @Override
  public Object queryBeacon(List<String> datasetStableIds, String variantType,
      String alternateBases, String referenceBases, String chromosome, Integer start,
      Integer startMin, Integer startMax, Integer end, Integer endMin, Integer endMax,
      String referenceGenome, String includeDatasetResponses, List<String> filters) {

    if (StringUtils.isNotBlank(referenceBases)
        && StringUtils.isNotBlank(chromosome) && start != null && StringUtils.isBlank(variantType)
        && startMin == null && startMax == null && endMin == null && endMax == null) {

      if (end == null && StringUtils.isNotBlank(alternateBases)) {
        return genomicQuery
            .queryBeaconGenomicSnp(datasetStableIds, alternateBases, referenceBases, chromosome,
                start, referenceGenome, includeDatasetResponses, filters);
      } else if (end != null && StringUtils.isBlank(alternateBases)) {
        return genomicQuery
            .queryBeaconGenomicRegion(datasetStableIds, referenceBases, chromosome, start, end,
                referenceGenome, includeDatasetResponses, filters);
      } else {
        throw new NotImplementedException("Query not implemented");
      }
    }
    throw new NotImplementedException("Query not implemented");
  }

  @Override
  public List<Integer> checkParams(BeaconAlleleResponse result, List<String> datasetStableIds,
      VariantType type, String alternateBases, String referenceBases, String chromosome,
      Integer start, Integer startMin, Integer startMax, Integer end, Integer endMin,
      Integer endMax, String referenceGenome, List<String> filters,
      List<String> translatedFilters) {

    // TODO check params are valid

    return null;
  }

  @Override
  public Object queryBeacon(String body) throws IOException {

    BeaconGenomicSnpRequest request = JsonUtils
        .jsonToObject(body, BeaconGenomicSnpRequest.class, objectMapper);

    String includeDatasetResponses =
        request.getIncludeDatasetResponses() != null ? request.getIncludeDatasetResponses()
            .getFilter() : null;

    return queryBeacon(request.getDatasetIds(), null,
        request.getAlternateBases(), request.getReferenceBases(), request.getReferenceName(),
        request.getStart(), null, null, null,
        null, null, request.getAssemblyId(),
        includeDatasetResponses, request.getFilters());
  }
}
