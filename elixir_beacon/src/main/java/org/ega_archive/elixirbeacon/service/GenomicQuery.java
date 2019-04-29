package org.ega_archive.elixirbeacon.service;

import java.util.List;

public interface GenomicQuery {

  Object queryBeaconGenomicSnp(List<String> datasetStableIds, String alternateBases,
      String referenceBases, String chromosome, Integer start, String referenceGenome,
      String includeDatasetResponses, List<String> filters);

  Object queryBeaconGenomicRegion(List<String> datasetStableIds, String referenceBases,
      String chromosome, Integer start, Integer end, String referenceGenome,
      String includeDatasetResponses, List<String> filters);

  Object listDatasets();

}
