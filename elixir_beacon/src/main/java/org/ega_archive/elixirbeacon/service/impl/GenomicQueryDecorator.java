package org.ega_archive.elixirbeacon.service.impl;

import java.util.List;
import org.ega_archive.elixirbeacon.convert.MapConverter;
import org.ega_archive.elixirbeacon.service.GenomicQuery;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class GenomicQueryDecorator implements GenomicQuery {

  private GenomicQuery wrappedBeaconService;

  private MapConverter wrappedMapConverter;

  public GenomicQueryDecorator(
      @Qualifier("genomicQueryImpl") GenomicQuery wrappedBeaconService,
      MapConverter wrappedMapConverter) {
    this.wrappedBeaconService = wrappedBeaconService;
    this.wrappedMapConverter = wrappedMapConverter;
  }

  @Override
  public Object queryBeaconGenomicSnp(List<String> datasetStableIds,
      String alternateBases, String referenceBases, String chromosome, Integer start,
      String referenceGenome, String includeDatasetResponses, List<String> filters) {

    Object response = wrappedBeaconService
        .queryBeaconGenomicSnp(datasetStableIds, alternateBases, referenceBases, chromosome, start,
            referenceGenome, includeDatasetResponses, filters);
    return wrappedMapConverter.convertToMap(response, false);
  }

  @Override
  public Object queryBeaconGenomicRegion(List<String> datasetStableIds,
      String referenceBases, String chromosome, Integer start, Integer end, String referenceGenome,
      String includeDatasetResponses, List<String> filters) {

    Object response = wrappedBeaconService
        .queryBeaconGenomicRegion(datasetStableIds, referenceBases, chromosome, start, end,
            referenceGenome, includeDatasetResponses, filters);
    return wrappedMapConverter.convertToMap(response, false);
  }

  @Override
  public Object listDatasets() {
    Object response = wrappedBeaconService.listDatasets();
    return wrappedMapConverter.convertToMap(response, false);
  }
}
