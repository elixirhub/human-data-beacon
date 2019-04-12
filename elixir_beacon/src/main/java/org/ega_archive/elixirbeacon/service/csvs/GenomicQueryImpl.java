package org.ega_archive.elixirbeacon.service.csvs;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.util.*;
import java.util.stream.Collectors;
import jdk.nashorn.internal.parser.JSONParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.ArrayStack;
import org.apache.commons.lang3.StringUtils;
import org.babelomics.csvs.lib.models.DiseaseCount;
import org.babelomics.csvs.lib.models.Variant;
import org.babelomics.csvs.lib.ws.QueryResponse;
import org.ega_archive.elixirbeacon.dto.*;
import org.ega_archive.elixirbeacon.service.GenomicQuery;
import org.ega_archive.elixirbeacon.utils.ParseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GenomicQueryImpl implements GenomicQuery {

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
      //Variant variant = variants.get(0);
      info.put("stats", variants.get(0).getStats());
      //String varSearch = String.join(":", (new String[] {variant.getChromosome(), String.valueOf(variant.getPosition()),variant.getReference(), variant.getAlternate()}));
    }

    BeaconGenomicSnpResponse beaconGenomicSnpResponse = new BeaconGenomicSnpResponse();
    beaconGenomicSnpResponse.setExists(variantExists);
    if (variantExists) {
      String varSearch = String.join(":", (new String[] {variants.get(0).getChromosome(), String.valueOf(variants.get(0).getPosition()),variants.get(0).getReference(), variants.get(0).getAlternate()}));
      String cellBaseResponse = callToCellBase(varSearch);
      beaconGenomicSnpResponse.setBeaconHandover(parseCellBase(cellBaseResponse));
    }

    // TODO: fill the request  grg for disease in the info
    BeaconGenomicSnpRequest request = new BeaconGenomicSnpRequest();
    request.setAlternateBases(alternateBases);
    request.setReferenceBases(referenceBases);
    request.setReferenceName(chromosome);
    request.setStart(start);
    //request.setAssemblyIds(referenceBases);
    // TODO: grg Get data from question
    request.setAssemblyId("GRCh37");
    request.setDatasetIds(datasetStableIds);
    // TODO: grg ??
    //request.setIncludeDatasetResponses(includeDatasetResponses);
    request.setFilters(filters);


    beaconGenomicSnpResponse.setRequest(request);
    beaconGenomicSnpResponse.setInfo(info);
    return beaconGenomicSnpResponse;
  }

  @Override
  public BeaconGenomicRegionResponse queryBeaconGenomicRegion(List<String> datasetStableIds,
      String referenceBases, String chromosome, Integer start, Integer end, String referenceGenome,
      String includeDatasetResponses, List<String> filters) {
    List<Variant> variants = findRegionVariants(chromosome, start, end, referenceBases, null, datasetStableIds, filters);
    List<VariantAnnotation> variantAnnotations= new ArrayList<>();

    String paramsVariantsCellbase = "";
    if (!variants.isEmpty()) {
      //info = new ArrayList<>();
      for (Variant variant: variants) {

        VariantAnnotation variantAnnotation = new VariantAnnotation();

        // TODO: grg group search in cellbase
        // Get list variant to search in cellbase
        String varSearch = String.join(":", (new String[] {variant.getChromosome(), String.valueOf(variant.getPosition()),variant.getReference(), variant.getAlternate()}));
        /* grg paramsVariantCellbase = !paramsVariantCellbase.isEmpty() ? paramsVariantCellbase.concat(","): paramsVariantCellbase;
        paramsVariantCellbase = paramsVariantCellbase.concat(varSearch);*/
        String dataAnnotation = callToCellBase(varSearch);
        variantAnnotation.setCellBaseInfo(dataAnnotation);

        // Get info variant (all subpopulations)
        Map<String, Object> variantInfo = new HashMap<>();
        //variantInfo.put("stats variant", variants.get(0).getStats());
        variantInfo.put("stats variant", variant.getStats());
        variantInfo.put("variant", varSearch);
        variantAnnotation.setInfo(variantInfo);

        // Get variant by subpopulations
        // TODO: Note: only get if have
        //private List<DatasetAlleleResponse> datasetAlleleResponses;
        List<DatasetAlleleResponse> datasetAlleleResponses = new ArrayList<>();
        if(datasetStableIds != null && !datasetStableIds.isEmpty()) {
          for (String datasetId : datasetStableIds) {
            List<Variant> variantsDataset = findRegionVariants(chromosome, variant.getPosition(), variant.getPosition() + 1, variant.getReference(), variant.getAlternate(), Arrays.asList(datasetId), filters);
            // TODO: review if only return one
            if (!variantsDataset.isEmpty() && variantsDataset.size() == 1) {
              DatasetAlleleResponse datasetAlleleResponse = new DatasetAlleleResponse();
              datasetAlleleResponse.setDatasetId(datasetId);
              datasetAlleleResponse.setExists(variantsDataset.get(0).getStats() != null);
              HashMap infoDataset = new HashMap();
              infoDataset.put("stats dataset", variantsDataset.get(0).getStats());
              datasetAlleleResponse.setInfo(infoDataset);
              //infoDataset = new HashMap<>();
              //DiseaseCount stats = variants.get(0).getStats();
              //infoDataset.put("stats dataset", stats);

              // TODO: grg check if variantCount = sumSample+totlaGts or only totalGts
              datasetAlleleResponse.setSampleCount((long) variantsDataset.get(0).getStats().getTotalGts());

              datasetAlleleResponses.add(datasetAlleleResponse);
            }
          }
        }
        variantAnnotation.setDatasetAlleleResponses(datasetAlleleResponses);
        variantAnnotation.setVariantHandover(parseCellBase(dataAnnotation));

        // TODO: grg import add "handover" rs of cellbase
        variantAnnotations.add(variantAnnotation);
      }
    }


    BeaconGenomicRegionResponse response = new BeaconGenomicRegionResponse();
    // TODO: fill the request
    BeaconGenomicRegionRequest request = new BeaconGenomicRegionRequest();
    request.setReferenceBases(referenceBases);
    request.setReferenceName(chromosome);
    request.setStart(start);
    request.setEnd(end);
    //request.setAssemblyIds(referenceBases);
    // TODO: grg Get data from question
    request.setAssemblyId("GRCh37");
    request.setDatasetIds(datasetStableIds);
    // TODO: grg ??
    //request.setIncludeDatasetResponses(includeDatasetResponses);
    request.setFilters(filters);

    response.setRequest(request);


    response.setVariantAnnotation(variantAnnotations);
    response.setExists(!variants.isEmpty() && variants.size()>0);
    Map<String, Object> info = new HashMap<>();
    info.put("variantCount", variants.size());
    response.setInfo(info);
    return response;
  }

  /**
   * This function does a call to Cell Base.
   *
   * @param beaconGenomicSnpResponse
   * @param variants
   */
  private String callToCellBase(String paramsVariantsCellbase) {
    String urlCellBase = "http://cellbase.clinbioinfosspa.es/cb/webservices/rest/v4/hsapiens/genomic/variant/"+paramsVariantsCellbase + "/annotation";
    String result = "SEarch in cellbase";

    String cellBaseResponse = parseResponse.parseResponse(urlCellBase, null);

    return cellBaseResponse;
  }



  /**
   * Parse response Cell Base with the variants to get the rs IDs and fills the Handover.
   *
   * @param beaconGenomicSnpResponse
   * @param variants
   */
  private List<Handover> parseCellBase(String cellBaseResponse) {
  //private List<Handover> callToCellBase( List<String> variants) {
    // Call to cellBase and get the rs ID
    /*String urlCellBase = "http://cellbase.clinbioinfosspa.es/cb/webservices/rest/v4/hsapiens/genomic/variant/";
    urlCellBase += variants.stream().collect(Collectors.joining(","));
    urlCellBase += "/annotation";
    String cellBaseResponse = parseResponse.parseResponse(urlCellBase, null);*/
    //String cellBaseResponse = findAnnotation(variants.stream().collect(Collectors.joining(",")));

    List<String> rsIds = new ArrayList<>();

    JsonParser parser = new JsonParser();
    JsonElement jsonTree = parser.parse(cellBaseResponse);
    JsonObject cellBaseObj = jsonTree.getAsJsonObject();
    JsonArray variantArray = cellBaseObj.getAsJsonArray("response");
    if(variantArray != null) {
      for (JsonElement elem : variantArray) {
        JsonObject object = elem.getAsJsonObject();
        JsonArray variantResults = object.getAsJsonArray("result");
        if(variantResults != null) {
          for (JsonElement variantElem : variantResults) {
            JsonObject variantObj = variantElem.getAsJsonObject();
              JsonElement rsIdElem = variantObj.get("id");
              if(rsIdElem != null) {
                rsIds.add(rsIdElem.getAsString());
                log.debug("rs ID: {}", rsIdElem.getAsString());
            }
          }
        }
      }
    }

    List<Handover> handoverList = new ArrayList<>();
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

    return handoverList;
  }


  private List<Variant> findRegionVariants(String chromosome, Integer start, Integer end,
      String referenceBases, String alternateBases, List<String> datasetStableIds,
      List<String> filters) {

    String technologyFilter = null;
    if (null != filters) {
      // TODO: load this data to the ontology table and check it
      String technologyFilterValues = filters.stream()
          .filter(filter -> filter.startsWith("myDictionary"))
          .map(filter -> filter.split(":", 2)[1])
          .collect(Collectors.joining(","));
      technologyFilter =
          StringUtils.isNotBlank(technologyFilterValues) ? "&technologies=" + technologyFilterValues
              : null;
    }

    String url = "http://csvs.clinbioinfosspa.es:8080/csvs/rest/variants/fetch?regions=";
    url = url + chromosome + ":" + start + "-" + end;

    if (datasetStableIds != null && !datasetStableIds.isEmpty()) {
      url += "&diseases=" + String.join(",", datasetStableIds);
    }
    if (StringUtils.isNotBlank(technologyFilter)) {
      url += technologyFilter;
    }

    log.debug("url {}", url);
    QueryResponse<Variant> variantQueryResponse = parseResponse
        .parseCsvsResponse(url, Variant.class);
    log.debug("response: {}", variantQueryResponse);

    boolean isRegionQuery = StringUtils.isBlank(alternateBases) && end != null;

    boolean exists =
        variantQueryResponse.getNumTotalResults() > 0 && variantQueryResponse.getResult() != null
            && variantQueryResponse.getResult().get(0) != null;
    //List<KeyValuePair> info = new ArrayList<>();
    int numVariants = 0;
    List<Variant> variantsResults = new ArrayList<>();
    if (exists) {
      List<Variant> result = variantQueryResponse.getResult();
      for (Variant variant : result) {
        if (checkParameters(referenceBases, variant.getReference(), alternateBases,
            variant.getAlternate(), isRegionQuery)) {
          numVariants++;
/*
          DiseaseCount stats = variant.getStats();
          info.add(new KeyValuePair("0/0", String.valueOf(stats.getGt00())));
          info.add(new KeyValuePair("0/1", String.valueOf(stats.getGt01())));
          info.add(new KeyValuePair("1/1", String.valueOf(stats.getGt11())));
          info.add(new KeyValuePair("./.", String.valueOf(stats.getGtmissing())));
          info.add(new KeyValuePair("0 Freq", String.valueOf(stats.getRefFreq())));
          info.add(new KeyValuePair("1 Freq", String.valueOf(stats.getAltFreq())));
          info.add(new KeyValuePair("MAF", String.valueOf(stats.getMaf())));
          // TODO: consider if we should show this info
//          info.add(new KeyValuePair("Num sample regions", String.valueOf(stats.getSumSampleRegions())));
//          info.add(new KeyValuePair("Total GTs", String.valueOf(stats.getTotalGts())));
*/
          variantsResults.add(variant);

        }
      }
    }
    return variantsResults;
    /*
    if (numVariants > 1) {
      info = new ArrayList<>();
    }
    if (numVariants > 0) {
      info.add(new KeyValuePair("variantCount", String.valueOf(numVariants)));
    }
    return info;
    */
  }

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
