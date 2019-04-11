package org.ega_archive.elixirbeacon.service;

import java.util.List;
import org.ega_archive.elixirbeacon.dto.BeaconGenomicSnpResponse;

public interface GenomicQuery {

  BeaconGenomicSnpResponse queryBeaconGenomicSnp(List<String> datasetStableIds, String alternateBases, String referenceBases, String chromosome, Integer start, String referenceGenome, String includeDatasetResponses, List<String> filters);

}
