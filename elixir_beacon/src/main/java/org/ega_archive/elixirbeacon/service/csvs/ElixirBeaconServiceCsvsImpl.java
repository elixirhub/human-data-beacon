package org.ega_archive.elixirbeacon.service.csvs;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.babelomics.csvs.lib.models.DiseaseGroup;
import org.babelomics.csvs.lib.ws.QueryResponse;
import org.ega_archive.elixirbeacon.dto.Beacon;
import org.ega_archive.elixirbeacon.dto.BeaconAlleleResponse;
import org.ega_archive.elixirbeacon.dto.BeaconRequest;
import org.ega_archive.elixirbeacon.dto.Dataset;
import org.ega_archive.elixirbeacon.dto.KeyValuePair;
import org.ega_archive.elixirbeacon.enums.VariantType;
import org.ega_archive.elixirbeacon.service.ElixirBeaconService;
import org.ega_archive.elixirbeacon.service.GenomicQuery;
import org.ega_archive.elixirbeacon.utils.ParseResponse;
import org.ega_archive.elixircore.exception.NotImplementedException;
import org.ega_archive.elixircore.helper.CommonQuery;
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

  @Override
  public Beacon listDatasets(CommonQuery commonQuery, String referenceGenome) {
    String url = "http://csvs.clinbioinfosspa.es:8080/csvs/rest/diseases/list";

    QueryResponse<DiseaseGroup> diseaseGroupQueryResponse = parseResponse
        .parseCsvsResponse(url, DiseaseGroup.class);

    Beacon beacon = new Beacon();

    List<Dataset> datasets = new ArrayList<>();
    for(DiseaseGroup disease : diseaseGroupQueryResponse.getResult()) {
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

    List<KeyValuePair> info = new ArrayList<>();
    info.add(new KeyValuePair("Number of individuals", numIndividuals.toString()));
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

      if(end == null && StringUtils.isNotBlank(alternateBases)) {
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
  public Object queryBeacon(BeaconRequest request) {
    return queryBeacon(request.getDatasetIds(), request.getVariantType(),
        request.getAlternateBases(), request.getReferenceBases(), request.getReferenceName(),
        request.getStart(), request.getStartMin(), request.getStartMax(), request.getEnd(),
        request.getEndMin(), request.getEndMax(), request.getAssemblyId(),
        request.getIncludeDatasetResponses(), request.getFilters());
  }
}
