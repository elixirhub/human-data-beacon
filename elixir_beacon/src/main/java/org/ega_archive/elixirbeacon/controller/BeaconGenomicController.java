package org.ega_archive.elixirbeacon.controller;

import java.util.List;
import org.ega_archive.elixirbeacon.dto.BeaconGenomicRegionResponse;
import org.ega_archive.elixirbeacon.dto.BeaconGenomicSnpResponse;
import org.ega_archive.elixirbeacon.service.GenomicQuery;
import org.ega_archive.elixircore.constant.ParamName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/beacon")
public class BeaconGenomicController {

  @Autowired
  private GenomicQuery genomicQuery;

  @GetMapping(value = "/genomic_snp")
  public BeaconGenomicSnpResponse queryBeaconGenomicSnp(
      @RequestParam(value = ParamName.BEACON_DATASET_IDS, required = false) List<String> datasetStableIds,
      @RequestParam(value = ParamName.BEACON_ALTERNATE_BASES, required = false) String alternateBases,
      @RequestParam(value = ParamName.BEACON_REFERENCE_BASES, required = false) String referenceBases,
      @RequestParam(value = ParamName.BEACON_CHROMOSOME, required = false) String chromosome,
      @RequestParam(value = ParamName.BEACON_START, required = false) Integer start,
      @RequestParam(value = ParamName.BEACON_REFERENCE_GENOME, required = false) String referenceGenome,
      @RequestParam(value = ParamName.BEACON_INCLUDE_DATASET_RESPONSES, required = false) String includeDatasetResponses,
      @RequestParam(value = ParamName.BEACON_FILTERS, required = false) List<String> filters) {

    return genomicQuery.queryBeaconGenomicSnp(datasetStableIds, alternateBases, referenceBases,
        chromosome, start, referenceGenome, includeDatasetResponses, filters);
  }

  @GetMapping(value = "/genomic_region")
  public BeaconGenomicRegionResponse queryBeaconGenomicRegion(
      @RequestParam(value = ParamName.BEACON_DATASET_IDS, required = false) List<String> datasetStableIds,
      @RequestParam(value = ParamName.BEACON_REFERENCE_BASES, required = false) String referenceBases,
      @RequestParam(value = ParamName.BEACON_CHROMOSOME, required = false) String chromosome,
      @RequestParam(value = ParamName.BEACON_START, required = false) Integer start,
      @RequestParam(value = ParamName.BEACON_END, required = false) Integer end,
      @RequestParam(value = ParamName.BEACON_REFERENCE_GENOME, required = false) String referenceGenome,
      @RequestParam(value = ParamName.BEACON_INCLUDE_DATASET_RESPONSES, required = false) String includeDatasetResponses,
      @RequestParam(value = ParamName.BEACON_FILTERS, required = false) List<String> filters) {

    return genomicQuery
        .queryBeaconGenomicRegion(datasetStableIds, referenceBases, chromosome, start, end,
            referenceGenome, includeDatasetResponses, filters);
  }

}
