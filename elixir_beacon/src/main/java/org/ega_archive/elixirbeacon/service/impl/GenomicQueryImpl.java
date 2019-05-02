package org.ega_archive.elixirbeacon.service.impl;

import com.querydsl.core.types.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.babelomics.csvs.lib.models.DiseaseCount;
import org.babelomics.csvs.lib.models.DiseaseGroup;
import org.babelomics.csvs.lib.ws.QueryResponse;
import org.ega_archive.elixirbeacon.constant.BeaconConstants;
import org.ega_archive.elixirbeacon.constant.CsvsConstants;
import org.ega_archive.elixirbeacon.dto.BeaconGenomicRegionRequest;
import org.ega_archive.elixirbeacon.dto.BeaconGenomicRegionResponse;
import org.ega_archive.elixirbeacon.dto.BeaconGenomicSnpRequest;
import org.ega_archive.elixirbeacon.dto.BeaconGenomicSnpResponse;
import org.ega_archive.elixirbeacon.dto.Dataset;
import org.ega_archive.elixirbeacon.dto.DatasetAlleleResponse;
import org.ega_archive.elixirbeacon.dto.Error;
import org.ega_archive.elixirbeacon.dto.Handover;
import org.ega_archive.elixirbeacon.dto.HandoverType;
import org.ega_archive.elixirbeacon.dto.Variant;
import org.ega_archive.elixirbeacon.dto.VariantDetail;
import org.ega_archive.elixirbeacon.enums.AccessLevel;
import org.ega_archive.elixirbeacon.enums.ErrorCode;
import org.ega_archive.elixirbeacon.enums.FilterDatasetResponse;
import org.ega_archive.elixirbeacon.model.elixirbeacon.OntologyTermColumnCorrespondance;
import org.ega_archive.elixirbeacon.model.elixirbeacon.QOntologyTermColumnCorrespondance;
import org.ega_archive.elixirbeacon.repository.elixirbeacon.OntologyTermColumnCorrespondanceRepository;
import org.ega_archive.elixirbeacon.service.GenomicQuery;
import org.ega_archive.elixirbeacon.utils.ParseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GenomicQueryImpl implements GenomicQuery {

  public static final String LINK_TO_DB_SNP_API = "Link to dbSNP API";
  @Autowired
  private ParseResponse parseResponse;

  @Autowired
  private OntologyTermColumnCorrespondanceRepository ontologyTermColumnCorrRep;

  private static List<Dataset> LIST_DATASET = new ArrayList<>();

  @Override
  public List<Dataset> listDatasets() {
    if (LIST_DATASET.isEmpty()) {
      String url = CsvsConstants.CSVS_URL + "/diseases/list";

      QueryResponse<DiseaseGroup> diseaseGroupQueryResponse = parseResponse
          .parseCsvsResponse(url, DiseaseGroup.class);

      // All the datasets are public -> harcode the "authorized" attribute
      Map<String, Object> info = new HashMap<>();
      info.put("authorized", true);
      info.put("accessType", AccessLevel.PUBLIC.getLevel());

      List<Dataset> datasets = new ArrayList<>();
      for (DiseaseGroup disease : diseaseGroupQueryResponse.getResult()) {
        Dataset dat = new Dataset();
        dat.setId(String.valueOf(disease.getGroupId()));
        dat.setName(disease.getName());
        dat.setDescription(disease.getName());
        dat.setAssemblyId(CsvsConstants.CSVS_ASSEMMBY_ID);
        dat.setSampleCount((long) disease.getSamples());
        dat.setVariantCount((long) disease.getVariants());
        dat.setInfo(info);
        datasets.add(dat);
        LIST_DATASET = datasets;
      }
    }
    return LIST_DATASET;
  }

  @Override
  public BeaconGenomicSnpResponse queryBeaconGenomicSnp(List<String> datasetStableIds,
      String alternateBases, String referenceBases, String chromosome, Integer start,
      String referenceGenome, String includeDatasetResponses, List<String> filters) {

    // TODO: Add new endpoint to CSVS to return variants by dataset and use it here
    // TODO: Param assembley in query in CSVS
    // TODO: Develop web service with filters in csvs

    BeaconGenomicSnpResponse beaconGenomicSnpResponse = new BeaconGenomicSnpResponse();
    List resultsHandover = new ArrayList();
    BeaconGenomicSnpRequest request = new BeaconGenomicSnpRequest();
    request.setAlternateBases(alternateBases);
    request.setReferenceBases(referenceBases);
    request.setReferenceName(chromosome);
    request.setStart(start);
    request.setAssemblyId(CsvsConstants.CSVS_ASSEMMBY_ID);
    request.setDatasetIds(datasetStableIds);
    FilterDatasetResponse parsedIncludeDatasetResponses = FilterDatasetResponse
        .parse(includeDatasetResponses);
    request.setIncludeDatasetResponses(parsedIncludeDatasetResponses);
    request.setFilters(filters);
    beaconGenomicSnpResponse.setRequest(request);

    boolean variantExists;
    Map<String, Object> info = null;

    List<Error> errors = checkParams(datasetStableIds, alternateBases, referenceBases, chromosome,
        start, referenceGenome, filters);
    errors.addAll(checkParamsSnp(alternateBases, referenceBases));
    if (!errors.isEmpty()) {
      Error error = new Error();
      error.setErrorCode(ErrorCode.GENERIC_ERROR);
      error.setMessage(
          errors.stream().map(err -> err.getMessage())
              .collect(Collectors.joining(", "))
      );
      beaconGenomicSnpResponse.setError(error);
    } else {
      List<org.babelomics.csvs.lib.models.Variant> variants = findRegionVariants(chromosome,
          CsvsConstants.CSVS_BASED == 1 ? start + 1 : start,
          CsvsConstants.CSVS_BASED == 1 ? start + 2 : start + 1,
          referenceBases, alternateBases, datasetStableIds, filters);

      variantExists = !variants.isEmpty() && variants.size() == 1;
      if (variantExists) {
        info = new HashMap<>();
        //info.put("stats", variants.get(0).getStats());
        info.put("stats", filterVariantStats(variants.get(0).getStats()));
      }

      beaconGenomicSnpResponse.setExists(variantExists);
      if (variantExists) {
        Map cellBaseResponse = callToCellBase(variants.get(0).getChromosome(),
            variants.get(0).getPosition(), variants.get(0).getReference(),
            variants.get(0).getAlternate());
        resultsHandover = parseCellBase(cellBaseResponse);

        // Get variant by subpopulations only get if have
        beaconGenomicSnpResponse.setDatasetAlleleResponses(
            getDatasetAlleleResponse(datasetStableIds, variants.get(0), filters,
                parsedIncludeDatasetResponses));
      }
    }
    beaconGenomicSnpResponse.setResultsHandover(resultsHandover);
    // Links to downloads and contact
    beaconGenomicSnpResponse.setBeaconHandover(genericHandover());
    beaconGenomicSnpResponse.setInfo(info);
    return beaconGenomicSnpResponse;
  }

  @Override
  public BeaconGenomicRegionResponse queryBeaconGenomicRegion(List<String> datasetStableIds,
      String referenceBases, String chromosome, Integer start, Integer end, String referenceGenome,
      String includeDatasetResponses, List<String> filters) {

    BeaconGenomicRegionResponse response = new BeaconGenomicRegionResponse();
    // Fill the request
    BeaconGenomicRegionRequest request = new BeaconGenomicRegionRequest();
    request.setReferenceBases(referenceBases);
    request.setReferenceName(chromosome);
    request.setStart(start);
    request.setEnd(end);
    request.setAssemblyId(CsvsConstants.CSVS_ASSEMMBY_ID);
    //request.setAssemblyIds(referenceBases);
    request.setDatasetIds(datasetStableIds);
    FilterDatasetResponse parsedIncludeDatasetResponses = FilterDatasetResponse
        .parse(includeDatasetResponses);
    request.setIncludeDatasetResponses(parsedIncludeDatasetResponses);
    request.setFilters(filters);

    response.setRequest(request);

    List<Error> errors = checkParams(datasetStableIds, null, referenceBases, chromosome, start,
        referenceGenome, filters);

    boolean variantExists = false;
    List<Variant> variantList = new ArrayList<>();
    Map<String, Object> info = new HashMap<>();

    List beaconHandovers = new ArrayList();
    if (!errors.isEmpty()) {
      Error error = new Error();
      error.setErrorCode(ErrorCode.GENERIC_ERROR);
      error.setMessage(
          errors.stream().map(err -> err.getMessage())
              .collect(Collectors.joining(", "))
      );
      response.setError(error);
    } else {
      // check X-based
      List<org.babelomics.csvs.lib.models.Variant> variants = findRegionVariants(chromosome,
          CsvsConstants.CSVS_BASED == 1 ? start + 1 : start,
          CsvsConstants.CSVS_BASED == 1 ? end + 1 : end,
          referenceBases, null, datasetStableIds, filters);

      variantExists = !variants.isEmpty() && variants.size() > 0;
      if (variantExists) {
        for (org.babelomics.csvs.lib.models.Variant variant : variants) {
          Variant beaconVariant = new Variant();

          VariantDetail variantDetail = new VariantDetail();
          variantDetail.setAlternateBases(variant.getAlternate());
          variantDetail.setReferenceBases(variant.getReference());
          variantDetail.setStart(variant.getPosition());
          variantDetail.setChromosome(variant.getChromosome());
          beaconVariant.setVariantDetails(variantDetail);

          // Get list variant to search in cellbase
          Map cellBaseMap = callToCellBase(variant.getChromosome(), variant.getPosition(),
              variant.getReference(), variant.getAlternate());
          //beaconVariant.setCellBaseInfo(cellBaseMap);
          // TODO call to dbsnp

          //beaconHandovers = parseCellBase(cellBaseMap);

          // Get info variant (all subpopulations)
          Map<String, Object> variantInfo = new HashMap<>();
          //variantInfo.put("stats variant", variant.getStats());
          variantInfo.put("stats variant", filterVariantStats(variant.getStats()));
          // Return variant 0-based
          String varSearch = String.join(":",
              (new String[]{variant.getChromosome(), String.valueOf(variant.getPosition()),
                  variant.getReference(), variant.getAlternate()}));
          if (CsvsConstants.CSVS_BASED == 1) {
            varSearch = String.join(":",
                (new String[]{variant.getChromosome(), String.valueOf(variant.getPosition() - 1),
                    variant.getReference(), variant.getAlternate()}));
          }
          variantInfo.put("variant", varSearch);
          beaconVariant.setInfo(variantInfo);

          // Get variant by subpopulations
          List<DatasetAlleleResponse> datasetAlleleResponses = getDatasetAlleleResponse(
              datasetStableIds, variant, filters, parsedIncludeDatasetResponses);

          beaconVariant.setDatasetAlleleResponses(datasetAlleleResponses);

          Map<String, Object> variantAnnotations = new LinkedHashMap<>();
          variantAnnotations.put("cellBase", cellBaseMap);

          List<Handover> variantHandover = parseCellBase(cellBaseMap);
          variantHandover.stream()
              .filter(handover -> StringUtils.isNotBlank(handover.getNote()))
              .filter(handover -> handover.getNote().equalsIgnoreCase(LINK_TO_DB_SNP_API))
              .forEach(handover -> {
                Map dbSnpResponse = parseResponse.parseResponse(handover.getUrl(), null);
                variantAnnotations.put("dbSNP", dbSnpResponse);
              });
          beaconVariant.setVariantHandover(variantHandover);
          beaconVariant.setVariantAnnotations(variantAnnotations);

          variantList.add(beaconVariant);
        }
      }

      info.put("variantCount", variants.size());
      info.put("variantLimit", "Only return the first " + CsvsConstants.CSVS_LIMIT + " variants");
    }

    // Links to downloads and contact
    beaconHandovers.addAll(genericHandover());
    response.setBeaconHandover(beaconHandovers);
    response.setVariantsFound(variantList);
    response.setExists(variantExists);

    response.setInfo(info);
    return response;
  }


  /**
   * Method to get generic handover about csvs (Link to download and contact)
   */
  private List<Handover> genericHandover() {
    List<Handover> genericHandover = new ArrayList<>();

    // Add handover link to download
    HandoverType handoverType = new HandoverType();
    handoverType.setId("CUSTOM");
    handoverType.setLabel("Download data");
    Handover handover = new Handover();
    handover.setHandoverType(handoverType);
    handover.setUrl(CsvsConstants.CSVS_URL_DOWNLOADS);
    handover.setNote(
        "Download aggregated data corresponding to phenotypically healthy controls of MGP and the IBS population of 1000 genomes phase 3 as well as the pseudo-controls for each ICD10 category. Go to web "
            + BeaconConstants.BEACON_HOMEPAGE
            + " and accept terms and conditions in the tab 'Downloads'");
    genericHandover.add(handover);

    handoverType = new HandoverType();
    handoverType.setId("CUSTOM");
    handoverType.setLabel("Contact to request data");
    handover = new Handover();
    handover.setHandoverType(handoverType);
    handover.setUrl(BeaconConstants.ORGANIZATION_CONTACT);
    handover.setNote(BeaconConstants.ORGANIZATION_NAME + ". " + BeaconConstants.BEACON_NAME + ", "
        + BeaconConstants.BEACON_HOMEPAGE);
    genericHandover.add(handover);

    return genericHandover;
  }

  /**
   * Get variant by subpopulations only get if have.
   *
   * @param datasetStableIds Datasets search
   * @param variant to search
   * @param filters Filters used to search variant
   */
  private List<DatasetAlleleResponse> getDatasetAlleleResponse(List<String> datasetStableIds,
      org.babelomics.csvs.lib.models.Variant variant, List<String> filters,
      FilterDatasetResponse includeDatasetResponses) {
    List<DatasetAlleleResponse> datasetAlleleResponses = new ArrayList<>();
    if (includeDatasetResponses.isIncludeDatasets()) {
      // Gets all ids Dataset that exists
      if (datasetStableIds == null || datasetStableIds.isEmpty()) {
        datasetStableIds = listDatasets().stream()
            .map(dataset -> dataset.getId())
            .collect(Collectors.toList());
      }

      for (String datasetId : datasetStableIds) {
        DatasetAlleleResponse datasetAlleleResponse = new DatasetAlleleResponse();
        datasetAlleleResponse.setDatasetId(datasetId);

        // Without filters
        List<org.babelomics.csvs.lib.models.Variant> variantsDataset = null;

        try {
          variantsDataset = findRegionVariants(variant.getChromosome(), variant.getPosition(),
              variant.getPosition() + 1, variant.getReference(), variant.getAlternate(),
              Arrays.asList(datasetId), null);
        } catch (Exception e) {
          Error error = new Error();
          error.setErrorCode(ErrorCode.GENERIC_ERROR);
          error.setMessage(e.getMessage());
          datasetAlleleResponse.setError(error);
        }

        if (variantsDataset != null) {
          if (!variantsDataset.isEmpty()) {
            datasetAlleleResponse.setExists(variantsDataset.get(0).getStats() != null);

            // Number of variants matching the allele request in the dataset (without filters)
            datasetAlleleResponse.setVariantCount((long) 1);
            // gt01 + gt11
            datasetAlleleResponse.setSampleCount(
                (long) variantsDataset.get(0).getStats().getGt01() + (long) variantsDataset.get(0)
                    .getStats().getGt11());
            // AlFreq (Freq1)
            datasetAlleleResponse
                .setFrequency(BigDecimal.valueOf(variantsDataset.get(0).getStats().getAltFreq()));

            HashMap infoDataset = new HashMap();
            // Info Without filters
            if (filters == null || filters.isEmpty()) {
              infoDataset
                  .put("stats dataset", filterVariantStats(variantsDataset.get(0).getStats()));
              datasetAlleleResponse.setInfo(infoDataset);
            } else { // Info wit filters
              List<org.babelomics.csvs.lib.models.Variant> variantsDatasetWithoutFilters = variantsDataset = findRegionVariants(
                  variant.getChromosome(), variant.getPosition(), variant.getPosition() + 1,
                  variant.getReference(), variant.getAlternate(), Arrays.asList(datasetId),
                  filters);
              if (variantsDatasetWithoutFilters != null && !variantsDatasetWithoutFilters
                  .isEmpty()) {
                filterVariantStats(variantsDatasetWithoutFilters.get(0).getStats());
                datasetAlleleResponse.setInfo(infoDataset);
              }
            }
          } else {
            datasetAlleleResponse.setExists(false);
          }
        }

        if (datasetAlleleResponse.getError() == null) {
          switch (includeDatasetResponses.getFilter()) {
            case "hit":
              if (!variantsDataset.isEmpty()) {
                datasetAlleleResponses.add(datasetAlleleResponse);
              }
              break;
            case "miss":
              if (variantsDataset.isEmpty()) {
                datasetAlleleResponses.add(datasetAlleleResponse);
              }
              break;
            case "all":
              datasetAlleleResponses.add(datasetAlleleResponse);
              break;
            default:
              break;
          }
        } else {
          datasetAlleleResponses.add(datasetAlleleResponse);
        }
      }
    }
    return datasetAlleleResponses;
  }

  private Map<String, String> filterVariantStats(DiseaseCount stats) {
    Map<String, String> statsDataset = new LinkedHashMap<>();
    statsDataset.put("gt00", Integer.toString(stats.getGt00()));
    statsDataset.put("gt01", Integer.toString(stats.getGt01()));
    statsDataset.put("gt11", Integer.toString(stats.getGt11()));
    statsDataset.put("gtmissing", Integer.toString(stats.getGtmissing()));
    statsDataset.put("maf", Float.toString(stats.getMaf()));
    statsDataset.put("refFreq", Float.toString(stats.getRefFreq()));
    statsDataset.put("altFreq", Float.toString(stats.getAltFreq()));
    statsDataset.put("sumSampleRegions", Integer.toString(stats.getSumSampleRegions()));
    statsDataset.put("totalGts", Integer.toString(stats.getTotalGts()));
    return statsDataset;
  }

  /**
   * call to Cell Base.
   */
  private Map<String, Object> callToCellBase(String chromosome, int position, String reference,
      String alternate) {
    Map cellBaseResponse = null;

    // Ignore when ref=* and search alt=ALT when alt=ALT,*
    String ref = reference.split(",")[0];
    String alt = alternate.split(",")[0];

    // Replace '*' and '' by '-'
    String paramsVariantsCellbase = String
        .join(":", (new String[]{chromosome, String.valueOf(position),
            ("*".equals(ref) || "".equals(ref)) ? "-" : ref,
            ("*".equals(alt) || "".equals(alt)) ? "-" : alt}));

    try {
      cellBaseResponse = parseResponse.parseResponse(
          CsvsConstants.CELLBASE_URL + "/genomic/variant/" + paramsVariantsCellbase + "/annotation",
          null);

    } catch (Exception e) {

    }

    return cellBaseResponse;
  }

  /**
   * Parse response Cell Base with the variants to get the rs IDs and fills the Handover.
   *
   * @param cellBaseResponse Response cellbase that convert to data Handover
   */
  private List<Handover> parseCellBase(Map<String, Object> cellBaseResponse) {
    List<String> rsIds = new ArrayList<>();
    List<Handover> handoverList = new ArrayList<>();

    if (cellBaseResponse != null && !cellBaseResponse.isEmpty()) {
      if (cellBaseResponse.get("response") != null) {
        ArrayList<LinkedHashMap> variantArray = (ArrayList<LinkedHashMap>) cellBaseResponse
            .get("response");
        System.out.println(variantArray);
        if (variantArray != null) {
          for (LinkedHashMap elem : variantArray) {

            ArrayList<LinkedHashMap> variantResults = (ArrayList<LinkedHashMap>) elem.get("result");
            if (variantResults != null) {
              for (LinkedHashMap variantElem : variantResults) {
                String rsIdElem = (String) variantElem.get("id");
                if (rsIdElem != null) {
                  rsIds.add(rsIdElem);
                  //log.debug("rs ID: {}", rsIdElem);
                }
              }
            }
          }
        }
      }

      for (String rsId : rsIds) {
        HandoverType handoverType = new HandoverType();
        handoverType.setId("data:1106");
        handoverType.setLabel("dbSNP ID");
        Handover handover = new Handover();
        handover.setHandoverType(handoverType);
        handover.setUrl(CsvsConstants.dbSNP_URL_DATABASE + "/?term=" + rsId);
        handover.setNote("Link to dbSNP database");
        handoverList.add(handover);

        handoverType = new HandoverType();
        handoverType.setId("data:1106");
        handoverType.setLabel("dbSNP ID");
        handover = new Handover();
        handover.setHandoverType(handoverType);
        handover.setUrl(CsvsConstants.dbSNP_URL_API + "/" + rsId.replaceFirst("rs", ""));
        handover.setNote(LINK_TO_DB_SNP_API);
        handoverList.add(handover);
      }
    }

    return handoverList;
  }

//<<<<<<< HEAD:elixir_beacon/src/main/java/org/ega_archive/elixirbeacon/service/impl/GenomicQueryImpl.java
//  private boolean findVariantCount(Map<String, Object> info) {
//    Integer variantCount = 0;
//    String value = (String) info.get("variantCount");
//    if (StringUtils.isNotBlank(value)) {
//      try {
//        variantCount = Integer.parseInt(value);
//      } catch (NumberFormatException ex) {
//        // Ignore exception
//      }
//    }
//    return variantCount > 0;
//  }
//
//  private Map<String, Object> findRegionVariants(String chromosome, Integer start, Integer end,
//=======

  /**
   * Get equivalence ontology with field to search.
   *
   * @param ontology Name ontology
   * @param term Term to search
   * @return Term if not find
   */
  public String getIdOntology(String ontology, String term) {
    Predicate query = QOntologyTermColumnCorrespondance.ontologyTermColumnCorrespondance.ontology
        .eq(ontology)
        .and(QOntologyTermColumnCorrespondance.ontologyTermColumnCorrespondance.term.eq(term));

    OntologyTermColumnCorrespondance ontologyTermColumnCorr = ontologyTermColumnCorrRep
        .findOne(query);

    return (ontologyTermColumnCorr != null) ? String
        .valueOf(ontologyTermColumnCorr.getSampleTableColumnName()) : term;
  }


  /**
   * Find list variants in the region.
   *
   * @param chromosome Chromosome 1-22,X,Y,MT
   * @param start Position ini
   * @param end Position end
   * @param referenceBases Reference
   * @param alternateBases Alternate
   * @param datasetStableIds Group subpopulation
   * @param filters Filters (tecnology , disease(icd10) ...)
   */
  private List<org.babelomics.csvs.lib.models.Variant> findRegionVariants(String chromosome,
      Integer start, Integer end,
      String referenceBases, String alternateBases, List<String> datasetStableIds,
      List<String> filters) {
    List<org.babelomics.csvs.lib.models.Variant> variantsResults = new ArrayList<>();

    String diseases = null;
    String technologyFilter = null;
    List<String> icd10FilterValues = null;
    if (null != filters) {
      String technologyFilterValues = filters.stream()
          .filter(filter -> filter.startsWith("csvs.tech"))
          .map(filter -> filter.split(":", 2)[1])
          .collect(Collectors.joining(","));
      technologyFilter =
          StringUtils.isNotBlank(technologyFilterValues) ? "&technologies=" + technologyFilterValues
              : null;

      // Convert to filter icd10
      icd10FilterValues = filters.stream()
          .filter(filter -> filter.startsWith("csvs.icd10"))
          .map(filter -> getIdOntology("csvs.icd10", filter.split(":", 2)[1]))
          .collect(Collectors.toList());
    }

    // Diseases intersect dataset and icd10
    if (datasetStableIds != null && !datasetStableIds.isEmpty() && icd10FilterValues != null
        && !icd10FilterValues.isEmpty()) {
      diseases = datasetStableIds.stream()
          .filter(icd10FilterValues::contains)
          .collect(Collectors.joining(","));
      // No search
      if (diseases == null || diseases.isEmpty()) {
        return variantsResults;
      }
    } else {
      if (datasetStableIds != null && !datasetStableIds.isEmpty()) {
        diseases = String.join(",", datasetStableIds);
      } else {
        if (icd10FilterValues != null && !icd10FilterValues.isEmpty()) {
          diseases = String.join(",", icd10FilterValues);
        }
      }
    }

    String url = CsvsConstants.CSVS_URL + "/variants/fetch?regions=";
    url = url + chromosome + ":" + start + "-" + end;
    url += "&limit=" + CsvsConstants.CSVS_LIMIT;

    if (StringUtils.isNotBlank(technologyFilter)) {
      url += technologyFilter;
    }

    if (StringUtils.isNotBlank(diseases)) {
      url += "&diseases=" + diseases;
    }

    log.debug("url {}", url);
    QueryResponse<org.babelomics.csvs.lib.models.Variant> variantQueryResponse = parseResponse
        .parseCsvsResponse(url, org.babelomics.csvs.lib.models.Variant.class);
    //log.debug("response: {}", variantQueryResponse);

    boolean isRegionQuery = StringUtils.isBlank(alternateBases) && end != null;

    boolean exists =
        variantQueryResponse.getNumTotalResults() > 0 && variantQueryResponse.getResult() != null
            && variantQueryResponse.getResult().get(0) != null;
    if (exists) {
      List<org.babelomics.csvs.lib.models.Variant> result = variantQueryResponse.getResult();
      for (org.babelomics.csvs.lib.models.Variant variant : result) {
        if (checkParameters(referenceBases, variant.getReference(), alternateBases,
            variant.getAlternate(), isRegionQuery)) {
          variantsResults.add(variant);
        }
      }
    }
    return variantsResults;
  }

  /**
   * Returns to check the parameters if it is a region.
   */
  private boolean checkParameters(String referenceBases, String reference, String alternateBases,
      String alternate, boolean isRegionQuery) {
    if (isRegionQuery) {
      return StringUtils.isBlank(referenceBases) || StringUtils
          .equalsIgnoreCase(referenceBases, reference);
    } else {
      return StringUtils.equalsIgnoreCase(referenceBases, reference)
          && (StringUtils.equalsIgnoreCase(alternateBases, "N")
          || StringUtils.equalsIgnoreCase(alternateBases, alternate));
    }
  }

  /**
   * Check values params from snp.
   */
  private List<Error> checkParamsSnp(String alternateBases, String referenceBases) {

    List<Error> errors = new ArrayList<>();

    if (StringUtils.isBlank(referenceBases)) {
      Error error = new Error();
      error.setErrorCode(ErrorCode.GENERIC_ERROR);
      error.setMessage("'referenceBases' is required");
      errors.add(error);
    }

    if (StringUtils.isBlank(alternateBases)) {
      Error error = new Error();
      error.setErrorCode(ErrorCode.GENERIC_ERROR);
      error.setMessage("'alternateBases' is required");
      errors.add(error);
    }
    return errors;
  }

  /**
   * Check values params generic.
   */
  private List<Error> checkParams(List<String> datasetStableIds,
      String alternateBases, String referenceBases, String chromosome, Integer start,
      String referenceGenome, List<String> filters) {
    List<Error> errors = new ArrayList<>();

    if (StringUtils.isBlank(chromosome)) {
      Error error = new Error();
      error.setErrorCode(ErrorCode.GENERIC_ERROR);
      error.setMessage("'referenceName' is required");
      errors.add(error);
    } else {
      if (!Pattern.matches("^([1-9][0-9]|[1-9]|X|Y|MT)$", chromosome.toUpperCase())) {
        Error error = new Error();
        error.setErrorCode(ErrorCode.GENERIC_ERROR);
        error.setMessage("Invalid 'referenceName' parameter, accepted values are 1-22, X, Y, MT");
        errors.add(error);
      }
    }

    if (!StringUtils.isBlank(referenceBases) && !Pattern
        .matches("[ACTG]+|N", referenceBases.toUpperCase())) {
      Error error = new Error();
      error.setErrorCode(ErrorCode.GENERIC_ERROR);
      error.setMessage("Invalid 'referenceBases' parameter, it must match the pattern [ACTG]+|N");
      errors.add(error);
    }

    if (StringUtils.isBlank(referenceGenome)) {
      Error error = new Error();
      error.setErrorCode(ErrorCode.GENERIC_ERROR);
      error.setMessage("'assemblyId' ir required  (" + CsvsConstants.CSVS_ASSEMMBY_ID + ")");
      errors.add(error);

    } else {
      if (!CsvsConstants.CSVS_ASSEMMBY_ID.equals(referenceGenome.toLowerCase())) {
        Error error = new Error();
        error.setErrorCode(ErrorCode.NOT_FOUND);
        error.setMessage("Assembly not found. Use " + CsvsConstants.CSVS_ASSEMMBY_ID);
        errors.add(error);
      }
    }

    if (!StringUtils.isBlank(alternateBases) && !Pattern
        .matches("[ACTG]+|N", alternateBases.toUpperCase())) {
      Error error = new Error();
      error.setErrorCode(ErrorCode.GENERIC_ERROR);
      error.setMessage("Invalid 'referenceBases' parameter, it must match the pattern [ACTG]+|N");
      errors.add(error);
    }

    if (datasetStableIds != null) {
      // Remove empty/null strings
      datasetStableIds =
          datasetStableIds.stream().filter(s -> (StringUtils.isNotBlank(s)))
              .collect(Collectors.toList());

      for (String datasetStableId : datasetStableIds) {
        Dataset dataset = listDatasets().stream()
            .filter(filter -> String.valueOf(filter.getId()).equals(datasetStableId)).findAny()
            .orElse(null);
        if (dataset == null) {
          Error error = new Error();
          error.setErrorCode(ErrorCode.NOT_FOUND);
          error.setMessage("Dataset not found");
          errors.add(error);
        }
      }
    }

    if (filters != null) {
      errors.addAll(translateFilters(filters));
    }

    return errors;
  }


  /**
   * Get list errors to checks filters
   *
   * @param filters List filters to check
   */
  private List<Error> translateFilters(List<String> filters) {
    List<Error> errors = new ArrayList<>();

    filters.stream()
        .filter(filter -> StringUtils.isNotBlank(filter))
        .forEach(filter -> {
          filter = filter.replaceAll("\\s+", "");
          String[] tokens = filter.split(":", 2);
          String ontology = tokens[0];
          String term = tokens[1];
          Predicate query = QOntologyTermColumnCorrespondance.ontologyTermColumnCorrespondance.ontology
              .eq(ontology)
              .and(
                  (QOntologyTermColumnCorrespondance.ontologyTermColumnCorrespondance.sampleTableColumnName
                      .eq(term))
                      .or(QOntologyTermColumnCorrespondance.ontologyTermColumnCorrespondance.term
                          .eq(term)));

          if (ontologyTermColumnCorrRep.findOne(query) == null) {
            Error error = new Error();
            error.setErrorCode(ErrorCode.GENERIC_ERROR);
            error.setMessage("Ontology (" + ontology + ") and/or term (" + term
                + ") not known in this Beacon. Remember that none operator are accepted");
            errors.add(error);
          }
        });

//    for (String filter : filters) {
//      filter = filter.replaceAll("\\s+", "");
//      String[] tokens = filter.split(":", 2);
//      String ontology = tokens[0];
//      String term = tokens[1];
//      Predicate query = QOntologyTermColumnCorrespondance.ontologyTermColumnCorrespondance.ontology
//          .eq(ontology)
//          .and(
//              (QOntologyTermColumnCorrespondance.ontologyTermColumnCorrespondance.sampleTableColumnName
//                  .eq(term))
//                  .or(QOntologyTermColumnCorrespondance.ontologyTermColumnCorrespondance.term
//                      .eq(term)));
//
//      if (ontologyTermColumnCorrRep.findOne(query) == null) {
//        Error error = new Error();
//        error.setErrorCode(ErrorCode.GENERIC_ERROR);
//        error.setMessage("Ontology (" + ontology + ") and/or term (" + term
//            + ") not known in this Beacon. Remember that none operator are accepted");
//        errors.add(error);
//      }
//    }
    return errors;
  }
}
