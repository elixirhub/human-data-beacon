package org.ega_archive.elixirbeacon.service.csvs;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.babelomics.csvs.lib.models.DiseaseGroup;
import org.babelomics.csvs.lib.models.Variant;
import org.babelomics.csvs.lib.ws.QueryResponse;
import org.ega_archive.elixirbeacon.constant.BeaconConstants;
import org.ega_archive.elixirbeacon.dto.*;
import org.ega_archive.elixirbeacon.dto.Error;
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

  @Autowired
  private ParseResponse parseResponse;

  @Autowired
  private OntologyTermColumnCorrespondanceRepository ontologyTermColumnCorrRep;
  private static final int LIMIT_CSVS = 100;
  private static final int CSVS_BASED = 1;
  public static final String CSVS_ASSEMMBY_ID = "GRCh37";
  private static List<Dataset> LIST_DATASET = new ArrayList<>();

  @Override
  public List<Dataset> listDatasets(){
    if (LIST_DATASET.isEmpty()) {
      String url = "http://csvs.clinbioinfosspa.es:8080/csvs/rest/diseases/list";

      QueryResponse<DiseaseGroup> diseaseGroupQueryResponse = parseResponse
              .parseCsvsResponse(url, DiseaseGroup.class);

      List<Dataset> datasets = new ArrayList<>();
      for (DiseaseGroup disease : diseaseGroupQueryResponse.getResult()) {
        datasets.add(Dataset.builder()
                .id(String.valueOf(disease.getGroupId()))
                .name(disease.getName())
                .assemblyId(CSVS_ASSEMMBY_ID)
                .sampleCount((long) disease.getSamples())
                .variantCount((long) disease.getVariants())
                .build());
        LIST_DATASET = datasets;
      }
    }
    return LIST_DATASET;
  }


  @Override
  public BeaconGenomicSnpResponse queryBeaconGenomicSnp(List<String> datasetStableIds,
      String alternateBases, String referenceBases, String chromosome, Integer start,
      String referenceGenome, String includeDatasetResponses, List<String> filters) {

    // TODO: check referenceGenome is GRCh37 -> do it in checkParams
    // TODO: Add new endpoint to CSVS to return variants by dataset and use it here
    // TODO: Move all URLs to the properties file

    BeaconGenomicSnpResponse beaconGenomicSnpResponse = new BeaconGenomicSnpResponse();
    List beaconHandovers = new ArrayList();
    // TODO: fill the request  grg for disease in the info
    BeaconGenomicSnpRequest request = new BeaconGenomicSnpRequest();
    request.setAlternateBases(alternateBases);
    request.setReferenceBases(referenceBases);
    request.setReferenceName(chromosome);
    request.setStart(start);
    // TODO: grg Get data from question
    request.setAssemblyId(CSVS_ASSEMMBY_ID);
    //request.setAssemblyIds(referenceBases);
    request.setDatasetIds(datasetStableIds);
    request.setIncludeDatasetResponses(FilterDatasetResponse.parse(includeDatasetResponses));
    request.setFilters(filters);
    beaconGenomicSnpResponse.setRequest(request);

    boolean variantExists;
    Map<String, Object> info = null;


    List<Error> errors = checkParams(datasetStableIds, alternateBases, referenceBases, chromosome, start, referenceGenome, filters);
    errors.addAll(checkParamsSnp(alternateBases, referenceBases));
    if (!errors.isEmpty()){
      beaconGenomicSnpResponse.setError(
              Error.builder()
                      .errorCode(ErrorCode.GENERIC_ERROR)
                      .message(
                              errors.stream().map(error -> error.getMessage())
                                      .collect(Collectors.joining(", "))
                      )
                      .build()
      );
    } else {
      List<Variant> variants = findRegionVariants(chromosome, CSVS_BASED == 1 ? start + 1 : start, CSVS_BASED == 1 ? start + 2 : start + 1, referenceBases, alternateBases, datasetStableIds, filters);

      variantExists = !variants.isEmpty() && variants.size() == 1;
      if (variantExists) {
        info = new HashMap<>();
        info.put("stats", variants.get(0).getStats());
      }

      beaconGenomicSnpResponse.setExists(variantExists);
      if (variantExists) {
        String varSearch = String.join(":", (new String[]{variants.get(0).getChromosome(), String.valueOf(variants.get(0).getPosition()), variants.get(0).getReference(), variants.get(0).getAlternate()}));
        Map cellBaseResponse = callToCellBase(varSearch);
        beaconHandovers = parseCellBase(cellBaseResponse);

        // Get variant by subpopulations only get if have
        beaconGenomicSnpResponse.setDatasetAlleleResponses(getDatasetAlleleResponse(datasetStableIds, variants.get(0), filters, includeDatasetResponses));
        // Add rs of cellbase Note: No exists handover... add info
        //variantAnnotation.setDatasetAlleleResponses(datasetAlleleResponses);
        //variantAnnotation.setVariantHandover(parseCellBase(dataAnnotation));
        //variantAnnotations.add(variantAnnotation);
      }
    }

    // Links to downloads and contact
    beaconHandovers.addAll(genericHandover());
    beaconGenomicSnpResponse.setBeaconHandover(beaconHandovers);
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
    // TODO: Developer web services to get data from question
    request.setAssemblyId(CSVS_ASSEMMBY_ID);
    //request.setAssemblyIds(referenceBases);
    request.setDatasetIds(datasetStableIds);
    request.setIncludeDatasetResponses(FilterDatasetResponse.parse(includeDatasetResponses));
    request.setFilters(filters);

    response.setRequest(request);

    List<Error> errors = checkParams(datasetStableIds, null, referenceBases, chromosome, start, referenceGenome,   filters);

    boolean variantExists = false;
    List<VariantAnnotation> variantAnnotations = new ArrayList<>();
    Map<String, Object> info = new HashMap<>();

    List beaconHandovers = new ArrayList();
    if (!errors.isEmpty()) {
      response.setError(
              Error.builder()
                      .errorCode(ErrorCode.GENERIC_ERROR)
                      .message(
                              errors.stream().map(error -> error.getMessage())
                                      .collect(Collectors.joining(", "))
                      )
                      .build()
      );
    } else {
        // check X-based
        List<Variant> variants = findRegionVariants(chromosome, CSVS_BASED == 1 ? start + 1 : start, CSVS_BASED == 1 ? end + 1 : end, referenceBases, null, datasetStableIds, filters);


        variantExists = !variants.isEmpty() && variants.size() > 0;
        if (variantExists) {
          for (Variant variant : variants) {
            VariantAnnotation variantAnnotation = new VariantAnnotation();

            // Get list variant to search in cellbase
            String varSearch = String.join(":", (new String[]{variant.getChromosome(), String.valueOf(variant.getPosition()), variant.getReference(), variant.getAlternate()}));
            Map dataAnnotation = callToCellBase(varSearch);
            variantAnnotation.setCellBaseInfo(dataAnnotation);
            //beaconHandovers = parseCellBase(dataAnnotation);

            // Get info variant (all subpopulations)
            Map<String, Object> variantInfo = new HashMap<>();
            variantInfo.put("stats variant", variant.getStats());
            // Return variant 0-based
            if (CSVS_BASED == 1)
              varSearch = String.join(":", (new String[]{variant.getChromosome(), String.valueOf(variant.getPosition() - 1), variant.getReference(), variant.getAlternate()}));
            variantInfo.put("variant", varSearch);
            variantAnnotation.setInfo(variantInfo);

            // Get variant by subpopulations
            // TODO: Note: only get if have datasetStableIds --> use param
            List<DatasetAlleleResponse> datasetAlleleResponses = getDatasetAlleleResponse(datasetStableIds, variant, filters, includeDatasetResponses);

            variantAnnotation.setDatasetAlleleResponses(datasetAlleleResponses);
            variantAnnotation.setVariantHandover(parseCellBase(dataAnnotation));

            // TODO: grg import add "handover" rs of cellbase
            // beaconHandovers.addAll(genericHandover());

            variantAnnotations.add(variantAnnotation);
          }
        }

        info.put("variantCount", variants.size());
        info.put("variantLimit", "Only return the first " +  LIMIT_CSVS + " variants");
      }


    // Links to downloads and contact
    beaconHandovers.addAll(genericHandover());
    response.setBeaconHandover(beaconHandovers);
    response.setVariantAnnotation(variantAnnotations);
    response.setExists(variantExists);
    // TODO: Develop web service with filters in csvs
    response.setInfo(info);
    return response;
  }


  /**
   * Method to get generic handover about csvs (Link to download and contact)
   * @return
   */
  private Collection genericHandover() {
    List<Handover> genericHandover= new ArrayList<>();

    // Add handover link to download
    genericHandover.add(Handover.builder()
            .handoverType(HandoverType.builder()
                    .id("downloads")
                    .label("Download aggregated data corresponding to phenotypically healthy controls of MGP and the IBS population of 1000 genomes phase 3 as well as the pseudo-controls for each ICD10 category")
                    .build())
            .url("http://csvs.clinbioinfosspa.es/downloads")
            .note("Go to web http://csvs.clinbioinfosspa.es and accept terms and conditions in the tab 'Downloads'")
            .build());

    genericHandover.add(Handover.builder()
            .handoverType(HandoverType.builder()
                    .id("organization")
                    .label(BeaconConstants.ORGANIZATION_NAME)
                    .build())
            .url( BeaconConstants.ORGANIZATION_CONTACT )
            .note( BeaconConstants.BEACON_NAME + ", " + BeaconConstants.BEACON_HOMEPAGE)
            .build());

    return genericHandover;
  }


  /**
   * Get variant by subpopulations only get if have.
   * @param datasetStableIds Datasets search
   * @param variant to search
   * @param filters Filters used to search variant
   * @return
   */
  private List<DatasetAlleleResponse> getDatasetAlleleResponse(List<String>datasetStableIds, Variant variant,  List<String> filters, String includeDatasetResponses) {
    List<DatasetAlleleResponse> datasetAlleleResponses = new ArrayList<>();
    FilterDatasetResponse filterDatasetResponse = FilterDatasetResponse.parse(includeDatasetResponses);
    if (filterDatasetResponse.isIncludeDatasets()) {
      // Gets all ids Dataset that exists
      if (datasetStableIds == null || datasetStableIds.isEmpty())
        datasetStableIds = listDatasets().stream()
                .map(dataset -> dataset.getId())
                .collect(Collectors.toList());

      for (String datasetId : datasetStableIds) {

        String optionIncludeDatasetResponses = includeDatasetResponses.toLowerCase();
        // TODO: review if only return one
        DatasetAlleleResponse datasetAlleleResponse = new DatasetAlleleResponse();
        datasetAlleleResponse.setDatasetId(datasetId);

        List<Variant> variantsDataset = null;

        try {
          variantsDataset = findRegionVariants(variant.getChromosome(), variant.getPosition(), variant.getPosition() + 1, variant.getReference(), variant.getAlternate(), Arrays.asList(datasetId), filters);
        } catch(Exception e){
          optionIncludeDatasetResponses="null";
          datasetAlleleResponse.setError( Error.builder()
                  .errorCode(ErrorCode.GENERIC_ERROR)
                  .message(
                          e.getMessage()
                  )
                  .build());
        }

        if (variantsDataset != null) {
          if (!variantsDataset.isEmpty()) { //&& variantsDataset.size() == 1 ??
            datasetAlleleResponse.setExists(variantsDataset.get(0).getStats() != null);
            HashMap infoDataset = new HashMap();
            infoDataset.put("stats dataset", variantsDataset.get(0).getStats());
            datasetAlleleResponse.setInfo(infoDataset);

            // Number of variants matching the allele request in the dataset
            datasetAlleleResponse.setVariantCount((long) 1);
            // gt01 + gt11
            datasetAlleleResponse.setSampleCount((long) variantsDataset.get(0).getStats().getGt01() + (long) variantsDataset.get(0).getStats().getGt11());
            // AlFreq (Freq1)
            datasetAlleleResponse.setFrequency(BigDecimal.valueOf(variantsDataset.get(0).getStats().getAltFreq()));
          } else {
            datasetAlleleResponse.setExists(false);
          }
        }


        switch (optionIncludeDatasetResponses) {
          case "hit":
            if (!variantsDataset.isEmpty())
              datasetAlleleResponses.add(datasetAlleleResponse);
            break;
          case "miss":
            if (variantsDataset.isEmpty())
              datasetAlleleResponses.add(datasetAlleleResponse);
            break;
          case "all":
            datasetAlleleResponses.add(datasetAlleleResponse);
            break;
          case "null":
            datasetAlleleResponses.add(datasetAlleleResponse);
            break;
          default:
            break;
        }

      }
    }
    return datasetAlleleResponses;
  }




  /**
   * This function does a call to Cell Base.
   *
   * @param paramsVariantsCellbase Cellbace parameter of variants to search  chrom:pos:ref:alt,chrom:pos:ref:alt...
   */
  private Map<String, Object> callToCellBase(String paramsVariantsCellbase) {
    String urlCellBase = "http://cellbase.clinbioinfosspa.es/cb/webservices/rest/v4/hsapiens/genomic/variant/"+paramsVariantsCellbase + "/annotation";

    // TODO: ignore when ref=* and search alt=ALT when alt=ALT,*
    Map cellBaseResponse = null;
    try {
      cellBaseResponse = parseResponse.parseResponse(urlCellBase, null);

    } catch (Exception e){

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
        ArrayList<LinkedHashMap> variantArray = (ArrayList<LinkedHashMap>) cellBaseResponse.get("response");
        System.out.println(variantArray);
        if(variantArray != null){
         for (LinkedHashMap elem : variantArray) {

            ArrayList<LinkedHashMap> variantResults = (ArrayList<LinkedHashMap>) elem.get("result");
            if (variantResults != null) {
              for (LinkedHashMap variantElem : variantResults) {
                String rsIdElem = (String) variantElem.get("id");
                if (rsIdElem != null) {
                  rsIds.add(rsIdElem);
                  log.debug("rs ID: {}", rsIdElem);
                }
              }
            }
          }
        }
      }

      for (String rsId : rsIds) {
        handoverList.add(Handover.builder()
                .handoverType(HandoverType.builder()
                        .id("data_1106")
                        .label("dbSNP ID")
                        .build())
                .url("https://www.ncbi.nlm.nih.gov/snp/?term=" + rsId)
                .note("Link to dbSNP database")
                .build());

        handoverList.add(Handover.builder()
                .handoverType(HandoverType.builder()
                        .id("data_1106")
                        .label("dbSNP ID")
                        .build())
                .url("https://api.ncbi.nlm.nih.gov/variation/v0/beta/refsnp/" + rsId.replaceFirst("rs", ""))
                .note("Link to dbSNP API")
                .build());
      }
    }

    return handoverList;
  }

  /**
   * Get equivalence ontology with field to search.
   * @param ontology Name ontology
   * @param term Term to search
   * @return Term if not find
   */
  public String getIdOntology(String ontology, String term){
    Predicate query = QOntologyTermColumnCorrespondance.ontologyTermColumnCorrespondance.ontology.eq(ontology)
            .and(QOntologyTermColumnCorrespondance.ontologyTermColumnCorrespondance.term.eq(term));

    OntologyTermColumnCorrespondance ontologyTermColumnCorr = ontologyTermColumnCorrRep.findOne(query);

    return (ontologyTermColumnCorr != null) ? String.valueOf(ontologyTermColumnCorr.getSampleTableColumnName()) : term;
  }


  /**
   * Find list variants in the region.
   * @param chromosome Chromosome 1-22,X,Y,MT
   * @param start Position ini
   * @param end  Position end
   * @param referenceBases Reference
   * @param alternateBases Alternate
   * @param datasetStableIds Group subpopulation
   * @param filters  Filters (tecnology , disease(icd10) ...)
   * @return
   */
  private List<Variant> findRegionVariants(String chromosome, Integer start, Integer end,
      String referenceBases, String alternateBases, List<String> datasetStableIds,
      List<String> filters) {
    List<Variant> variantsResults = new ArrayList<>();

    String diseases = null;
    String technologyFilter = null;
    List <String> icd10FilterValues = null;
    if (null != filters) {
      // TODO: load this data to the ontology table and check it
      String technologyFilterValues = filters.stream()
              .filter(filter -> filter.startsWith("myDictionary.tech"))
              .map(filter -> filter.split(":", 2)[1])
              .collect(Collectors.joining(","));
      technologyFilter =
              StringUtils.isNotBlank(technologyFilterValues) ? "&technologies=" + technologyFilterValues
                      : null;

      // Convert to filter icd10
      icd10FilterValues = filters.stream()
              .filter(filter -> filter.startsWith("ICD-10"))
              .map(filter -> getIdOntology("ICD-10", filter.split(":", 2)[1]))
              .collect(Collectors.toList());
    }

    // Diseases intersect dataset and icd10
    if (datasetStableIds != null && !datasetStableIds.isEmpty() && icd10FilterValues != null && !icd10FilterValues.isEmpty()) {
      diseases = datasetStableIds.stream()
              .filter(icd10FilterValues::contains)
              .collect(Collectors.joining(","));
      // No search
      if (diseases == null || diseases.isEmpty())
        return variantsResults;
    } else {
      if (datasetStableIds != null && !datasetStableIds.isEmpty()){
        diseases = String.join(",", datasetStableIds);
      } else {
        if (icd10FilterValues != null && !icd10FilterValues.isEmpty())
            diseases = String.join(",", icd10FilterValues);
      }
    }

    String url = "http://csvs.clinbioinfosspa.es:8080/csvs/rest/variants/fetch?regions=";
    url = url + chromosome + ":" + start + "-" + end;
    url += "&limit=" + LIMIT_CSVS;

    if (StringUtils.isNotBlank(technologyFilter)) {
      url += technologyFilter;
    }

    if (StringUtils.isNotBlank(diseases)){
      url += "&diseases="+ diseases;
    }

    log.debug("url {}", url);
    QueryResponse<Variant> variantQueryResponse = parseResponse
        .parseCsvsResponse(url, Variant.class);
    log.debug("response: {}", variantQueryResponse);

    boolean isRegionQuery = StringUtils.isBlank(alternateBases) && end != null;

    boolean exists =
        variantQueryResponse.getNumTotalResults() > 0 && variantQueryResponse.getResult() != null
            && variantQueryResponse.getResult().get(0) != null;

    if (exists) {
      List<Variant> result = variantQueryResponse.getResult();
      for (Variant variant : result) {
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
   * @param referenceBases
   * @param reference
   * @param alternateBases
   * @param alternate
   * @param isRegionQuery
   * @return
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
   * @param alternateBases
   * @param referenceBases
   * @return
   */
  private List<Error> checkParamsSnp(String alternateBases, String referenceBases) {

    List<Error> errors = new ArrayList<>();

    if (StringUtils.isBlank(referenceBases)) {
      errors.add(Error.builder()
              .errorCode(ErrorCode.GENERIC_ERROR)
              .message("'referenceBases' is required")
              .build());
    }

    if (StringUtils.isBlank(alternateBases)) {
      errors.add(Error.builder()
              .errorCode(ErrorCode.GENERIC_ERROR)
              .message("'alternateBases' is required")
              .build());
    }
    return errors;
  }

    /**
     * Check values params generic.
     * @param datasetStableIds
     * @param alternateBases
     * @param referenceBases
     * @param chromosome
     * @param start
     * @param referenceGenome
     * @param filters
     * @return
     */
    private List<Error> checkParams(List<String> datasetStableIds,
                                 String alternateBases, String referenceBases, String chromosome, Integer start,
                                 String referenceGenome,  List<String> filters) {
    List<Error> errors = new ArrayList<>();

    if (StringUtils.isBlank(chromosome)) {
      errors.add(Error.builder().errorCode(ErrorCode.GENERIC_ERROR).message("'referenceName' is required").build());
    } else {
      if (!Pattern.matches("^([1-9][0-9]|[1-9]|X|Y|MT)$", chromosome))
        errors.add(Error.builder().errorCode(ErrorCode.GENERIC_ERROR).message("Invalid 'referenceName' parameter, accepted values are 1-22, X, Y, MT")
                .build());
    }

    if (!StringUtils.isBlank(referenceBases) && !Pattern.matches("[ACTG]+|N", alternateBases)) {
        errors.add(Error.builder().errorCode(ErrorCode.GENERIC_ERROR)
                .message("Invalid 'referenceBases' parameter, it must match the pattern [ACTG]+|N")
                .build());
    }


    if (StringUtils.isNotBlank(referenceGenome) && !CSVS_ASSEMMBY_ID.equals(referenceGenome)){
      errors.add(Error.builder().errorCode(ErrorCode.GENERIC_ERROR)
              .message("Invalid 'assemblyId' parameter, GRC notation required " + CSVS_ASSEMMBY_ID + ")")
              .build());
    }

    if (!StringUtils.isBlank(alternateBases) && !Pattern.matches("[ACTG]+|N", alternateBases)) {
        errors.add(Error.builder().errorCode(ErrorCode.GENERIC_ERROR)
                .message("Invalid 'referenceBases' parameter, it must match the pattern [ACTG]+|N")
                .build());
    }

    if (datasetStableIds != null) {
      // Remove empty/null strings
      datasetStableIds =
              datasetStableIds.stream().filter(s -> (StringUtils.isNotBlank(s)))
                      .collect(Collectors.toList());

      for (String datasetStableId : datasetStableIds) {
        Dataset dataset = listDatasets().stream().filter(filter -> String.valueOf(filter.getId()).equals(datasetStableId)).findAny().orElse(null);
        if (dataset == null) {
          errors.add(Error.builder()
                  .errorCode(ErrorCode.NOT_FOUND)
                  .message("Dataset not found")
                  .build());
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
   * @param filters List filters to check
   * @return
   */
  private List<Error> translateFilters(List<String> filters) {
    List<Error> errors =  new ArrayList<>();

    for (String filter : filters) {
      filter = filter.replaceAll("\\s+", "");
      String[] tokens = filter.split(":", 2);
      String ontology = tokens[0];
      String term = tokens[1];
      Predicate query = QOntologyTermColumnCorrespondance.ontologyTermColumnCorrespondance.ontology.eq(ontology)
              .and(
                      (QOntologyTermColumnCorrespondance.ontologyTermColumnCorrespondance.sampleTableColumnName.eq(term))
                              .or(QOntologyTermColumnCorrespondance.ontologyTermColumnCorrespondance.term.eq(term)));

      if (ontologyTermColumnCorrRep.findOne(query) == null) {
        errors.add(Error.builder()
                .errorCode(ErrorCode.GENERIC_ERROR)
                .message("Ontology (" + ontology + ") and/or term (" + term + ") not known in this Beacon. Remember that none operator are accepted")
                .build());
      }
    }
    return errors;
  }
}