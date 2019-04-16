package org.ega_archive.elixirbeacon.service.csvs;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.babelomics.csvs.lib.models.Variant;
import org.babelomics.csvs.lib.ws.QueryResponse;
import org.ega_archive.elixirbeacon.constant.BeaconConstants;
import org.ega_archive.elixirbeacon.dto.*;
import org.ega_archive.elixirbeacon.enums.FilterDatasetResponse;
import org.ega_archive.elixirbeacon.service.GenomicQuery;
import org.ega_archive.elixirbeacon.utils.ParseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GenomicQueryImpl implements GenomicQuery {


  private static final int LIMIT_CSVS = 100 ;

  @Autowired
  private ParseResponse parseResponse;

  @Override
  public BeaconGenomicSnpResponse queryBeaconGenomicSnp(List<String> datasetStableIds,
      String alternateBases, String referenceBases, String chromosome, Integer start,
      String referenceGenome, String includeDatasetResponses, List<String> filters) {

    // TODO: check referenceGenome is GRCh37 -> do it in checkParams
    // TODO: Add new endpoint to CSVS to return variants by dataset and use it here
    // TODO: Move all URLs to the properties file

    List<Variant> variants = findRegionVariants(chromosome, start, start+1, referenceBases, alternateBases, datasetStableIds, filters);
    Map<String, Object> info = null;
    boolean variantExists = !variants.isEmpty() && variants.size() == 1;
    if (variantExists) {
      info = new HashMap<>();
      info.put("stats", variants.get(0).getStats());
    }

    BeaconGenomicSnpResponse beaconGenomicSnpResponse = new BeaconGenomicSnpResponse();
    List beaconHandovers = new ArrayList();
    beaconGenomicSnpResponse.setExists(variantExists);
    if (variantExists) {
      String varSearch = String.join(":", (new String[] {variants.get(0).getChromosome(), String.valueOf(variants.get(0).getPosition()),variants.get(0).getReference(), variants.get(0).getAlternate()}));
      Map cellBaseResponse = callToCellBase(varSearch);
      beaconHandovers = parseCellBase(cellBaseResponse);
    }

    // TODO: fill the request  grg for disease in the info
    BeaconGenomicSnpRequest request = new BeaconGenomicSnpRequest();
    request.setAlternateBases(alternateBases);
    request.setReferenceBases(referenceBases);
    request.setReferenceName(chromosome);
    request.setStart(start);
    // TODO: grg Get data from question
    request.setAssemblyId("GRCh37");
    //request.setAssemblyIds(referenceBases);
    request.setDatasetIds(datasetStableIds);
    request.setIncludeDatasetResponses(FilterDatasetResponse.parse(includeDatasetResponses));
    request.setFilters(filters);


    // Get variant by subpopulations only get if have
    if (variantExists) {
      beaconGenomicSnpResponse.setDatasetAlleleResponses(getDatasetAlleleResponse(datasetStableIds,  variants.get(0) ,filters));
      // Add rs of cellbase Note: No exists handover... add info
      //variantAnnotation.setDatasetAlleleResponses(datasetAlleleResponses);
      //variantAnnotation.setVariantHandover(parseCellBase(dataAnnotation));
      //variantAnnotations.add(variantAnnotation);
    }

    // Links to downloads and contact
    beaconHandovers.addAll(genericHandover());
    beaconGenomicSnpResponse.setBeaconHandover(beaconHandovers);
    beaconGenomicSnpResponse.setRequest(request);
    beaconGenomicSnpResponse.setInfo(info);
    return beaconGenomicSnpResponse;
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

  @Override
  public BeaconGenomicRegionResponse queryBeaconGenomicRegion(List<String> datasetStableIds,
      String referenceBases, String chromosome, Integer start, Integer end, String referenceGenome,
      String includeDatasetResponses, List<String> filters) {
    List<Variant> variants = findRegionVariants(chromosome, start, end, referenceBases, null, datasetStableIds, filters);
    List<VariantAnnotation> variantAnnotations= new ArrayList<>();

    List beaconHandovers = new ArrayList();
    if (!variants.isEmpty()) {
      for (Variant variant: variants) {
        VariantAnnotation variantAnnotation = new VariantAnnotation();

        // Get list variant to search in cellbase
        String varSearch = String.join(":", (new String[] {variant.getChromosome(), String.valueOf(variant.getPosition()), variant.getReference(), variant.getAlternate()}));
        Map dataAnnotation = callToCellBase(varSearch);
        variantAnnotation.setCellBaseInfo(dataAnnotation);
        //beaconHandovers = parseCellBase(dataAnnotation);

        // Get info variant (all subpopulations)
        Map<String, Object> variantInfo = new HashMap<>();
        variantInfo.put("stats variant", variant.getStats());
        variantInfo.put("variant", varSearch);
        variantAnnotation.setInfo(variantInfo);

        // Get variant by subpopulations
        // TODO: Note: only get if have datasetStableIds --> use param
        List<DatasetAlleleResponse> datasetAlleleResponses = getDatasetAlleleResponse(datasetStableIds, variant, filters);

        variantAnnotation.setDatasetAlleleResponses(datasetAlleleResponses);
        variantAnnotation.setVariantHandover(parseCellBase(dataAnnotation));

        // TODO: grg import add "handover" rs of cellbase
        // beaconHandovers.addAll(genericHandover());

        variantAnnotations.add(variantAnnotation);
      }
    }

    BeaconGenomicRegionResponse response = new BeaconGenomicRegionResponse();
    // Fill the request
    BeaconGenomicRegionRequest request = new BeaconGenomicRegionRequest();
    request.setReferenceBases(referenceBases);
    request.setReferenceName(chromosome);
    request.setStart(start);
    request.setEnd(end);
    // TODO: Developer web services to get data from question
    request.setAssemblyId("GRCh37");
    //request.setAssemblyIds(referenceBases);
    request.setDatasetIds(datasetStableIds);
    request.setIncludeDatasetResponses(FilterDatasetResponse.parse(includeDatasetResponses));
    request.setFilters(filters);

    response.setRequest(request);

    // Links to downloads and contact
    beaconHandovers.addAll(genericHandover());
    response.setBeaconHandover(beaconHandovers);
    response.setVariantAnnotation(variantAnnotations);
    response.setExists(!variants.isEmpty() && variants.size()>0);
    Map<String, Object> info = new HashMap<>();
    info.put("variantCount", variants.size());
    info.put("variantLimit", "Only return the first " +  LIMIT_CSVS + " variants");
    // TODO: Develop web service with filters in csvs
    response.setInfo(info);
    return response;
  }


  /**
   * Get variant by subpopulations only get if have.
   * @param datasetStableIds Datasets search
   * @param variant to search
   * @param filters Filters used to search variant
   * @return
   */
  private List<DatasetAlleleResponse> getDatasetAlleleResponse(List<String>datasetStableIds, Variant variant,  List<String> filters ) {
    List<DatasetAlleleResponse> datasetAlleleResponses = new ArrayList<>();
    if (datasetStableIds != null && !datasetStableIds.isEmpty()) {
      for (String datasetId : datasetStableIds) {
        List<Variant> variantsDataset = findRegionVariants(variant.getChromosome(), variant.getPosition(), variant.getPosition() + 1, variant.getReference(), variant.getAlternate(), Arrays.asList(datasetId), filters);
        // TODO: review if only return one
        if (!variantsDataset.isEmpty() && variantsDataset.size() == 1) {
          DatasetAlleleResponse datasetAlleleResponse = new DatasetAlleleResponse();
          datasetAlleleResponse.setDatasetId(datasetId);
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


          datasetAlleleResponses.add(datasetAlleleResponse);
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

      // Filter icd10
      icd10FilterValues = filters.stream()
              .filter(filter -> filter.startsWith("myDictionary.icd10"))
              .map(filter -> filter.split(":", 2)[1])
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

}