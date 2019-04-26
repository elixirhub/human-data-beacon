package org.ega_archive.elixirbeacon.service.impl;

import java.io.IOException;
import java.util.List;
import javassist.NotFoundException;
import org.ega_archive.elixirbeacon.convert.MapConverter;
import org.ega_archive.elixirbeacon.dto.Beacon;
import org.ega_archive.elixirbeacon.dto.BeaconAlleleResponse;
import org.ega_archive.elixirbeacon.enums.VariantType;
import org.ega_archive.elixirbeacon.service.ElixirBeaconService;
import org.ega_archive.elixircore.helper.CommonQuery;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ElixirBeaconServiceDecorator implements ElixirBeaconService {

  private ElixirBeaconService wrappedBeaconService;

  private MapConverter wrappedMapConverter;

  public ElixirBeaconServiceDecorator(
      @Qualifier("elixirBeaconServiceCsvsImpl") ElixirBeaconService wrappedBeaconService,
      MapConverter wrappedMapConverter) {
    this.wrappedBeaconService = wrappedBeaconService;
    this.wrappedMapConverter = wrappedMapConverter;
  }

  @Override
  public Beacon listDatasets(CommonQuery commonQuery, String referenceGenome)
      throws NotFoundException {
    Object response = wrappedBeaconService.listDatasets(commonQuery, referenceGenome);
    return null;
  }

  @Override
  public Object queryBeacon(List<String> datasetStableIds, String variantType,
      String alternateBases, String referenceBases, String chromosome, Integer start,
      Integer startMin, Integer startMax, Integer end, Integer endMin, Integer endMax,
      String referenceGenome, String includeDatasetResponses, List<String> filters) {

    Object response = wrappedBeaconService
        .queryBeacon(datasetStableIds, variantType, alternateBases, referenceBases, chromosome,
            start, startMin, startMax, end, endMin, endMax, referenceGenome,
            includeDatasetResponses, filters);
    return wrappedMapConverter.convertToMap(response, false);
  }

  @Override
  public List<Integer> checkParams(BeaconAlleleResponse result, List<String> datasetStableIds,
      VariantType type, String alternateBases, String referenceBases, String chromosome,
      Integer start, Integer startMin, Integer startMax, Integer end, Integer endMin,
      Integer endMax, String referenceGenome, List<String> filters,
      List<String> translatedFilters) {
    return null;
  }

  @Override
  public Object queryBeacon(String body) throws IOException {
    return null;
  }
}
