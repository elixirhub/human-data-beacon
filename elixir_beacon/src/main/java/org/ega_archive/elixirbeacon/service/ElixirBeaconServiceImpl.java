package org.ega_archive.elixirbeacon.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.ega_archive.elixirbeacon.constant.BeaconConstants;
import org.ega_archive.elixirbeacon.convert.Operations;
import org.ega_archive.elixirbeacon.dto.Beacon;
import org.ega_archive.elixirbeacon.dto.BeaconAlleleRequest;
import org.ega_archive.elixirbeacon.dto.BeaconAlleleResponse;
import org.ega_archive.elixirbeacon.dto.BeaconRequest;
import org.ega_archive.elixirbeacon.dto.Dataset;
import org.ega_archive.elixirbeacon.dto.DatasetAlleleResponse;
import org.ega_archive.elixirbeacon.dto.Error;
import org.ega_archive.elixirbeacon.dto.KeyValuePair;
import org.ega_archive.elixirbeacon.enums.ErrorCode;
import org.ega_archive.elixirbeacon.enums.FilterDatasetResponse;
import org.ega_archive.elixirbeacon.enums.VariantType;
import org.ega_archive.elixirbeacon.model.elixirbeacon.BeaconDataSummary;
import org.ega_archive.elixirbeacon.model.elixirbeacon.BeaconDataset;
import org.ega_archive.elixirbeacon.model.elixirbeacon.BeaconDatasetConsentCode;
import org.ega_archive.elixirbeacon.properties.SampleRequests;
import org.ega_archive.elixirbeacon.repository.elixirbeacon.BeaconDatasetConsentCodeRepository;
import org.ega_archive.elixirbeacon.repository.elixirbeacon.BeaconDatasetRepository;
import org.ega_archive.elixirbeacon.repository.elixirbeacon.BeaconSummaryDataRepository;
import org.ega_archive.elixircore.enums.DatasetAccessType;
import org.ega_archive.elixircore.helper.CommonQuery;
import org.ega_archive.elixircore.util.StoredProcedureUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

// for updated Info field specification
import java.util.Map;
import java.util.HashMap;
// for handling JSON string in info field
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
// For the new Request/Response pair
import org.ega_archive.elixirbeacon.dto.BeaconPlantRequest;
import org.ega_archive.elixirbeacon.dto.BeaconPlantResponse;
import org.ega_archive.elixirbeacon.dto.DatasetPlantResponse;


@Slf4j
@Service
public class ElixirBeaconServiceImpl implements ElixirBeaconService {

  @Autowired
  private SampleRequests sampleRequests;

  @Autowired
  private BeaconDatasetRepository beaconDatasetRepository;

  @Autowired
  private BeaconSummaryDataRepository beaconDataRepository;

  @Autowired
  private BeaconDatasetConsentCodeRepository beaconDatasetConsentCodeRepository;

  @Override
  public Beacon listDatasets(CommonQuery commonQuery, String referenceGenome)
      throws NotFoundException {

    commonQuery.setSort(new Sort(new Order(Direction.ASC, "id")));

    List<Dataset> convertedDatasets = new ArrayList<Dataset>();

    Page<BeaconDataset> allDatasets = null;
    if (StringUtils.isNotBlank(referenceGenome)) {
      referenceGenome = StringUtils.lowerCase(referenceGenome);
      allDatasets =
          beaconDatasetRepository.findByReferenceGenome(referenceGenome, commonQuery.getPageable());
    } else {
      allDatasets = beaconDatasetRepository.findAll(commonQuery);
    }

    BigInteger size = BigInteger.valueOf(0L);
    for (BeaconDataset dataset : allDatasets) {
      DatasetAccessType accessType = DatasetAccessType.parse(dataset.getAccessType());
      boolean authorized = false;
      if (accessType == DatasetAccessType.PUBLIC) {
        authorized = true;
      }
      List<BeaconDatasetConsentCode> ccDataUseConditions =
          beaconDatasetConsentCodeRepository.findByDatasetId(dataset.getId());

      convertedDatasets.add(Operations.convert(dataset, authorized, ccDataUseConditions));

      size = dataset.getVariantCnt().add(size);
    }

    // changed from KeyValuePair to <String Object>HashMap :
    Map<String,Object> info = new HashMap<String,Object>();

    info.put(BeaconConstants.SIZE, size.toString());
    Beacon response = new Beacon();
    response.setDatasets(convertedDatasets);
    response.setInfo(info);
    response.setSampleAlleleRequests(getSampleAlleleRequests());
    return response;
  }

  private List<BeaconAlleleRequest> getSampleAlleleRequests() {
    List<BeaconAlleleRequest> sampleAlleleRequests = new ArrayList<BeaconAlleleRequest>();
    sampleAlleleRequests.add(BeaconAlleleRequest.builder()
        .assemblyId(sampleRequests.getAssemblyId1())
        .start(sampleRequests.getStart1())
        .startMin(sampleRequests.getStartMin1())
        .startMax(sampleRequests.getStartMax1())
        .end(sampleRequests.getEnd1())
        .endMin(sampleRequests.getEndMin1())
        .endMax(sampleRequests.getEndMax1())
        .referenceName(sampleRequests.getReferenceName1())
        .referenceBases(sampleRequests.getReferenceBases1())
        .alternateBases(StringUtils.isBlank(sampleRequests.getAlternateBases1()) ? null : sampleRequests.getAlternateBases1())
        .datasetIds(sampleRequests.getDatasetIds1().isEmpty() ? null : sampleRequests.getDatasetIds1())
        .build());
    sampleAlleleRequests.add(BeaconAlleleRequest.builder()
        .assemblyId(sampleRequests.getAssemblyId2())
        .start(sampleRequests.getStart2())
        .startMin(sampleRequests.getStartMin2())
        .startMax(sampleRequests.getStartMax2())
        .end(sampleRequests.getEnd2())
        .endMin(sampleRequests.getEndMin2())
        .endMax(sampleRequests.getEndMax2())
        .referenceName(sampleRequests.getReferenceName2())
        .referenceBases(sampleRequests.getReferenceBases2())
        .alternateBases(StringUtils.isBlank(sampleRequests.getAlternateBases2()) ? null : sampleRequests.getAlternateBases2())
        .datasetIds(sampleRequests.getDatasetIds2().isEmpty() ? null : sampleRequests.getDatasetIds2())
        .build());
    sampleAlleleRequests.add(BeaconAlleleRequest.builder()
        .assemblyId(sampleRequests.getAssemblyId3())
        .start(sampleRequests.getStart3())
        .startMin(sampleRequests.getStartMin3())
        .startMax(sampleRequests.getStartMax3())
        .end(sampleRequests.getEnd3())
        .endMin(sampleRequests.getEndMin3())
        .endMax(sampleRequests.getEndMax3())
        .referenceBases(sampleRequests.getReferenceBases3())
        .referenceName(sampleRequests.getReferenceName3())
        .alternateBases(StringUtils.isBlank(sampleRequests.getAlternateBases3()) ? null : sampleRequests.getAlternateBases3())
        .datasetIds(sampleRequests.getDatasetIds3().isEmpty() ? null : sampleRequests.getDatasetIds3())
        .build());
    return sampleAlleleRequests;
  }

  @Override
  public BeaconAlleleResponse queryBeacon(List<String> datasetStableIds, String variantType,
      String alternateBases, String referenceBases, String chromosome, Integer start,
      Integer startMin, Integer startMax, Integer end, Integer endMin, Integer endMax,
      String referenceGenome, String includeDatasetResponses) {

    BeaconAlleleResponse result = new BeaconAlleleResponse();

    alternateBases = StringUtils.upperCase(alternateBases);
    referenceBases = StringUtils.upperCase(referenceBases);

    BeaconAlleleRequest request = BeaconAlleleRequest.builder()
        .alternateBases(alternateBases)
        .referenceBases(referenceBases)
        .referenceName(chromosome)
        .datasetIds(datasetStableIds)
        .start(start)
        .startMin(startMin)
        .startMax(startMax)
        .end(end)
        .endMin(endMin)
        .endMax(endMax)
        .variantType(variantType)
        .assemblyId(referenceGenome)
        .includeDatasetResponses(FilterDatasetResponse.parse(includeDatasetResponses))
        .build();
    result.setAlleleRequest(request);

    VariantType type = VariantType.parse(variantType);

    List<Integer> datasetIds =
        checkParams(result, datasetStableIds, type, alternateBases, referenceBases, chromosome,
            start, startMin, startMax, end, endMin, endMax, referenceGenome);

    boolean globalExists = false;
    if (result.getError() == null) {
      globalExists = queryDatabase(datasetIds, type, referenceBases, alternateBases, chromosome,
          start, startMin, startMax, end, endMin, endMax, referenceGenome, result);
    }
    result.setExists(globalExists);
    return result;
  }

  @Override
  public List<Integer> checkParams(BeaconAlleleResponse result, List<String> datasetStableIds,
      VariantType type, String alternateBases, String referenceBases, String chromosome,
      Integer start, Integer startMin, Integer startMax, Integer end, Integer endMin,
      Integer endMax, String referenceGenome) {

    List<Integer> datasetIds = new ArrayList<>();

    if (StringUtils.isBlank(chromosome) || StringUtils.isBlank(referenceGenome) || StringUtils.isBlank(referenceBases)) {
      Error error = Error.builder()
          .errorCode(ErrorCode.GENERIC_ERROR)
          .message("All 'referenceName', 'referenceBases' and/or 'assemblyId' are required")
          .build();
      result.setError(error);
      return datasetIds;
    }
    //if (StringUtils.isNotBlank(referenceGenome)){
      //boolean matches = Pattern.matches("^grch[1-9]{2}$", StringUtils.lowerCase(referenceGenome));
      //if (!matches) {
      //  Error error = Error.builder().errorCode(ErrorCode.GENERIC_ERROR)
      //      .message("Invalid 'assemblyId' parameter, GRC notation required (e.g. GRCh37)")
      //      .build();
      //  result.setError(error);
      //  return datasetIds;
      //}
    //}
    if (StringUtils.isNotBlank(chromosome)){
      boolean matches = Pattern.matches("^([1-9][0-9]|[1-9]|X|Y|MT|PLTD)$", chromosome);
      if (!matches) {
        Error error = Error.builder().errorCode(ErrorCode.GENERIC_ERROR)
            .message("Invalid 'referenceName' parameter, accepted values are 1-22, X, Y, MT, PLTD")
            .build();
        result.setError(error);
        return datasetIds;
      }
    }

    if (type == null && StringUtils.isBlank(alternateBases)) {
      Error error = Error.builder()
          .errorCode(ErrorCode.GENERIC_ERROR)
          .message("Either 'alternateBases' or 'variantType' is required")
          .build();
      result.setError(error);
    } else if (type != null && StringUtils.isNotBlank(alternateBases)
        && !StringUtils.equalsIgnoreCase(alternateBases, "N")) {
      Error error = Error.builder().errorCode(ErrorCode.GENERIC_ERROR)
          .message(
              "If 'variantType' is provided then 'alternateBases' must be empty or equal to 'N'")
          .build();
      result.setError(error);
      return datasetIds;
    }

    if (start == null) {
      if(end != null) {
        Error error = Error.builder()
            .errorCode(ErrorCode.GENERIC_ERROR)
            .message("'start' is required if 'end' is provided")
            .build();
        result.setError(error);
        return datasetIds;
      } else if (startMin == null && startMax == null && endMin == null && endMax == null) {
        Error error = Error.builder()
            .errorCode(ErrorCode.GENERIC_ERROR)
            .message("Either 'start' or all of 'startMin', 'startMax', 'endMin' and 'endMax' are required")
            .build();
        result.setError(error);
        return datasetIds;
      } else if (startMin == null || startMax == null || endMin == null || endMax == null) {
        Error error = Error.builder()
            .errorCode(ErrorCode.GENERIC_ERROR)
            .message("All of 'startMin', 'startMax', 'endMin' and 'endMax' are required")
            .build();
        result.setError(error);
        return datasetIds;
      }
    } else if (startMin != null || startMax != null || endMin != null || endMax != null) {
      Error error = Error.builder()
          .errorCode(ErrorCode.GENERIC_ERROR)
          .message("'start' cannot be provided at the same time as 'startMin', 'startMax', 'endMin' and 'endMax'")
          .build();
      result.setError(error);
      return datasetIds;
    } else if (end == null && StringUtils.equalsIgnoreCase(referenceBases, "N")) {
      Error error = Error.builder()
          .errorCode(ErrorCode.GENERIC_ERROR)
          .message("'referenceBases' cannot be 'N' if 'start' is provided and 'end' is missing")
          .build();
      result.setError(error);
      return datasetIds;
    }

    if (datasetStableIds != null) {
      // Remove empty/null strings
      datasetStableIds =
          datasetStableIds.stream().filter(s -> (StringUtils.isNotBlank(s)))
              .collect(Collectors.toList());

      for (String datasetStableId : datasetStableIds) {
        // 1) Dataset exists
        BeaconDataset dataset = beaconDatasetRepository.findByStableId(datasetStableId);
        if (dataset == null) {
          Error error = Error.builder()
              .errorCode(ErrorCode.NOT_FOUND)
              .message("Dataset not found")
              .build();
          result.setError(error);
          return datasetIds;
        } else {
          datasetIds.add(dataset.getId());
        }

        DatasetAccessType datasetAccessType = DatasetAccessType.parse(dataset.getAccessType());
        if (datasetAccessType != DatasetAccessType.PUBLIC) {
          Error error = Error.builder()
              .errorCode(ErrorCode.UNAUTHORIZED)
              .message("Unauthenticated users cannot access this dataset")
              .build();
          result.setError(error);
          return datasetIds;
        }

        // Check that the provided reference genome matches the one specified in the DB for this
        // dataset
        if (!StringUtils.equalsIgnoreCase(dataset.getReferenceGenome(), referenceGenome)) {
          Error error = Error.builder()
              .errorCode(ErrorCode.GENERIC_ERROR)
              .message("The assemblyId of this dataset (" + dataset.getReferenceGenome()
                  + ") and the provided value (" + referenceGenome + ") do not match")
              .build();
          result.setError(error);
          return datasetIds;
        }
      }
    }
    // Allele has a valid value
    if (StringUtils.isNotBlank(alternateBases)) {
      boolean matches = Pattern.matches("[ACTG]+|N", alternateBases);
      if (!matches) {
        Error error = Error.builder().errorCode(ErrorCode.GENERIC_ERROR)
            .message("Invalid 'alternateBases' parameter, it must match the pattern [ACTG]+|N")
            .build();
        result.setError(error);
        return datasetIds;
      }
    }
    if (StringUtils.isNotBlank(referenceBases)) {
      boolean matches = Pattern.matches("[ACTG]+|N", referenceBases);
      if (!matches) {
        Error error = Error.builder().errorCode(ErrorCode.GENERIC_ERROR)
            .message("Invalid 'referenceBases' parameter, it must match the pattern [ACTG]+|N").build();
        result.setError(error);
        return datasetIds;
      }
    }
    //    if (type != null && type != VariantType.SNP && type != VariantType.INSERTION
    //        && type != VariantType.DELELETION && type != VariantType.DUPLICATION) {
    //      Error error = Error.builder().errorCode(ErrorCode.GENERIC_ERROR)
    //          .message("Invalid 'variantType' parameter").build();
    //      result.setError(error);
    //      return datasetIds;
    //    }

    //    if (type != VariantType.SNP && type != VariantType.INSERTION && type != VariantType.DELELETION
    //        && type != VariantType.DUPLICATION) {
    //      Error error = Error.builder()
    //          .errorCode(ErrorCode.GENERIC_ERROR)
    //          .message("Invalid alternateBases parameter")
    //          .build();
    //      result.setError(error);
    //      return datasetIds;
    //    }

    return datasetIds;
  }

  private boolean queryDatabase(List<Integer> datasetIds, VariantType type, String referenceBases,
      String alternateBases, String chromosome, Integer start, Integer startMin, Integer startMax,
      Integer end, Integer endMin, Integer endMax, String referenceGenome,
      BeaconAlleleResponse result) {

    if (datasetIds == null || datasetIds.isEmpty()) {
      // Limit the query to only the authorized datasets
      datasetIds = findAuthorizedDatasets(referenceGenome);
    }

    long numResults = 0L;
    boolean globalExists = false;
    String variantType = type != null ? type.getType() : null;
    log.debug(
        "Calling query with params: variantType={}, start={}, startMin={}, startMax={}, end={}, "
            + "endMin={}, endMax={}, chrom={}, reference={}, alternate={}, assemblyId={}, "
            + "datasetIds={}", variantType, start, startMin, startMax, end, endMin, endMax,
        chromosome, referenceBases, alternateBases, referenceGenome, datasetIds);

    List<BeaconDataSummary> dataList = beaconDataRepository
        .searchForVariantsQuery(variantType, start,
            startMin, startMax, end, endMin, endMax, chromosome, referenceBases, alternateBases,
            referenceGenome, StoredProcedureUtils.joinArray(datasetIds));
    numResults = dataList.size();
    globalExists = numResults > 0;

    for (BeaconDataSummary data : dataList) {
      if (result.getAlleleRequest().getIncludeDatasetResponses() == FilterDatasetResponse.ALL
          || result.getAlleleRequest().getIncludeDatasetResponses() == FilterDatasetResponse.HIT) {
        DatasetAlleleResponse datasetResponse = new DatasetAlleleResponse();
        BeaconDataset dataset = beaconDatasetRepository.findOne(data.getDatasetId());
        datasetResponse.setDatasetId(dataset.getStableId());
        datasetResponse.setExists(true);
        datasetResponse.setFrequency(data.getFrequency());
        datasetResponse.setVariantCount(data.getVariantCnt());
        datasetResponse.setCallCount(data.getCallCnt());
        datasetResponse.setSampleCount(data.getSampleCnt());

        // MODIFIED below
        // get the info field's content (a Json-compatible String)
        String metadataString = dataset.getInfo();
        // Initialize a JSON parser and an object to store the JSON in.
        JSONParser parser = new JSONParser();
        JSONObject jsonMapping = new JSONObject();
        // Map the JSON string into a proper Json Map
        try {
          jsonMapping = (JSONObject) parser.parse(metadataString);
        } catch (Exception e) {
          e.printStackTrace();
        }


        Map<String,Object> info = new HashMap<String,Object>(); // MODIFIED
        // Given that the JSONObject and Map are derivative structures, we can assign the JSONObject's contents to the Map.
        // thus eliminating further use of the library.
        info = jsonMapping;
        datasetResponse.setInfo(info);
        // MODIFIED ^

        result.addDatasetAlleleResponse(datasetResponse);




      }
    }

    Set<Integer> datasetIdsWithData =
        dataList.stream().map(data -> data.getDatasetId()).collect(Collectors.toSet());

    // Check that all requested datasets are present in the response
    // (maybe some of them are not present because they have no data for this query)
    @SuppressWarnings("unchecked")
    Collection<Integer> missingDatasets =
        CollectionUtils.disjunction(datasetIds, datasetIdsWithData);

    if (!missingDatasets.isEmpty() && (result.getAlleleRequest()
        .getIncludeDatasetResponses() == FilterDatasetResponse.MISS
        || result.getAlleleRequest().getIncludeDatasetResponses() == FilterDatasetResponse.ALL)) {
      for (Integer datasetId : missingDatasets) {
        DatasetAlleleResponse datasetResponse = new DatasetAlleleResponse();
        BeaconDataset dataset = beaconDatasetRepository.findOne(datasetId);
        datasetResponse.setDatasetId(dataset.getStableId());
        datasetResponse.setExists(false);
        result.addDatasetAlleleResponse(datasetResponse);
      }
    }
    return globalExists;
  }

  private List<Integer> findAuthorizedDatasets(String referenceGenome) {
    referenceGenome = StringUtils.lowerCase(referenceGenome);
    List<Integer> publicDatasets = beaconDatasetRepository
        .findReferenceGenomeAndAccessType(referenceGenome, DatasetAccessType.PUBLIC.getType());
    return publicDatasets;
  }

  @Override
  public BeaconAlleleResponse queryBeacon(BeaconRequest request) {

    return queryBeacon(request.getDatasetIds(), request.getVariantType(),
        request.getAlternateBases(), request.getReferenceBases(), request.getReferenceName(),
        request.getStart(), request.getStartMin(), request.getStartMax(), request.getEnd(),
        request.getEndMin(), request.getEndMax(), request.getAssemblyId(),
        request.getIncludeDatasetResponses());
  }






// New Plant query related set of functions.
// For the Elixir All Hands Meeting in June 2019 it will be changed to
// a small set of 3-5 parameters that a plant researcher might wish to filter on.

private boolean queryPlantDatabase(List<Integer> datasetIds, VariantType type, String referenceBases,
    String alternateBases, String chromosome, Integer start, Integer startMin, Integer startMax,
    Integer end, Integer endMin, Integer endMax, String referenceGenome, String puid, String accenumb,
    String ancest, String cropname, String sampletype, String tissue, String age,
    BeaconPlantResponse result) {

  if (datasetIds == null || datasetIds.isEmpty()) {
    // Limit the query to only the authorized datasets
    datasetIds = findAuthorizedDatasets(referenceGenome);
  }

  long numResults = 0L;
  boolean globalExists = false;
  String variantType = type != null ? type.getType() : null;
  log.debug(
      "Calling query with params: variantType={}, start={}, startMin={}, startMax={}, end={}, "
          + "endMin={}, endMax={}, chrom={}, reference={}, alternate={}, assemblyId={}, "
          + "datasetIds={}, puid={}, accenumb={}, ancest={}, cropname={}, sampletype={}, tissue={}, age={}"
          , variantType, start, startMin, startMax, end, endMin, endMax,
          chromosome, referenceBases, alternateBases, referenceGenome, datasetIds,
          puid, accenumb, ancest, cropname, sampletype, tissue, age);

  List<BeaconDataSummary> dataList = beaconDataRepository
      .searchForVariantsQuery(variantType, start,
          startMin, startMax, end, endMin, endMax, chromosome, referenceBases, alternateBases,
          referenceGenome, StoredProcedureUtils.joinArray(datasetIds));
  numResults = dataList.size();
  // globalExists = numResults > 0;
  // Now that a query can be wrong due to tests that need to be done datalist by datalist
  // globalExists can only be True if the number of datalists answering the above query
  // that ALSO have all the metadata fields correct, is > 1


  for (BeaconDataSummary data : dataList) {
    if (result.getPlantRequest().getIncludeDatasetResponses() == FilterDatasetResponse.ALL
        || result.getPlantRequest().getIncludeDatasetResponses() == FilterDatasetResponse.HIT) {
      DatasetPlantResponse datasetResponse = new DatasetPlantResponse();
      BeaconDataset dataset = beaconDatasetRepository.findOne(data.getDatasetId());
      datasetResponse.setDatasetId(dataset.getStableId());
      datasetResponse.setExists(true);
      datasetResponse.setFrequency(data.getFrequency());
      datasetResponse.setVariantCount(data.getVariantCnt());
      datasetResponse.setCallCount(data.getCallCnt());
      datasetResponse.setSampleCount(data.getSampleCnt());




      // get the info field's content (a Json-compatible String)
      String metadataString = dataset.getInfo();
      // Initialize a JSON parser and an object to store the JSON in.
      JSONParser parser = new JSONParser();
      JSONObject jsonMapping = new JSONObject();
      // Map the JSON string into a proper Json Map
      try {
        jsonMapping = (JSONObject) parser.parse(metadataString);
      } catch (Exception e) {
        log.debug("TEST");
        e.printStackTrace();
      }

      Map<String,Object> info = new HashMap<String,Object>(); // MODIFIED

      info = jsonMapping;
      datasetResponse.setInfo(info);

      // MCPD and BioSample tests
      // TODO: Discuss if it should return an error in case the Beacon Maintainer failed
      //  to fill out the necessary metadata for the Plant query, or still return a result.


      JSONObject mcpd = (JSONObject) info.get("mcpd");


      // PUID check


      Boolean puidMatch = false;

      // if user didn't ask for puid verification, set boolean to true.
      // Else, test for equality with Dataset value.
      // If dataset doesn't contain a puid value, log error and set boolean to false.

      String dataset_puid = (String) mcpd.get("puid");


        // check that the parameter was given. if it wasn't return TRUE for match.
      if(puid == null || puid.equals("")){
        puidMatch = true;
        // first; check that the DB contains the puid field and is not null.
        // if it doesn't, go to next statement, if it does, evaluate second condition.
      } else if ( !(dataset_puid == null) && (dataset_puid.equals(puid)) ) {
        puidMatch = true;
      } else {
        puidMatch = false;
      }


      datasetResponse.setPuid(puidMatch);


      // accession number check


      Boolean accenumbMatch = false;

      // same logic as above

      String dataset_accenumb = (String) mcpd.get("accenumb");
      if(accenumb == null || accenumb.equals("")){
        accenumbMatch = true;
      } else if ( (dataset_accenumb != null) && dataset_accenumb.equals(accenumb)) {
        accenumbMatch = true;
      } else {
        accenumbMatch = false;
      }


      datasetResponse.setAccenumb(accenumbMatch);


      // ancestral data check


      Boolean ancestMatch = false;

      // same logic as above, but check for user's value inside the other, total match not needed.

      String dataset_ancest = (String) mcpd.get("ancest");
      if(ancest == null || ancest.equals("")){
        ancestMatch = true;
      } else if ((dataset_ancest != null) && (dataset_ancest.toLowerCase().contains(ancest.toLowerCase())) ) {
        ancestMatch = true;
      } else {
        ancestMatch = false;
      }

      datasetResponse.setAncest(ancestMatch);


      // crop name check


      Boolean cropnameMatch = false;

      // same logic as above

      String dataset_cropname = (String) mcpd.get("cropname");
      if(cropname == null || cropname.equals("")){
        cropnameMatch = true;
      } else if ((dataset_cropname != null) && dataset_cropname.toLowerCase().contains(cropname.toLowerCase())) {
        cropnameMatch = true;
      } else {
        cropnameMatch = false;
      }


      datasetResponse.setCropname(cropnameMatch);


      // BioSamples
      JSONObject bio = (JSONObject) info.get("biosamples");
      // Sample Type check


      Boolean sampletypeMatch = false;
      // same logic as puid

      String dataset_sampletype = (String) bio.get("sampletype");

      if(sampletype == null || sampletype.equals("")){
        sampletypeMatch = true;
      } else if ((dataset_sampletype != null) && dataset_sampletype.equals(sampletype)) {
        sampletypeMatch = true;
      } else {
        sampletypeMatch = false;
      }

      datasetResponse.setSampletype(sampletypeMatch);


      // Tissue type check


      Boolean tissueMatch = false;

      // same logic as puid

      String dataset_tissue = (String) bio.get("tissue");
      if(tissue == null || tissue.equals("")){
        tissueMatch = true;
      } else if ((dataset_tissue != null) && dataset_tissue.equals(tissue)) {
        tissueMatch = true;
      } else {
        tissueMatch = false;
      }

      datasetResponse.setTissue(tissueMatch);


      // Age check


      Boolean ageMatch = false;

      // Web service is set so the user:
      // Chooses an option between ">=" or "<="
      // enters an age (1.5, 20, 3...) values
      // resulting in a string: ">= 10 Days"
      // "<= 1.5 Hours"

      // TODO: Verify why this isn't working AFTER the Elixir All Hands meeting presentation.

      String dataset_age = (String) bio.get("age");
      log.debug(age,dataset_age);
      if(age == null || age.equals("")){
        ageMatch = true;
        log.debug(age,dataset_age);
      } else {
        //log.debug(age,dataset_age);
        //String[] splitAge = age.split(" ");
        //String comparator = splitAge[0];
        //String value = splitAge[1];
        // check if value is <= or >= to the dataset's age value. set variable accordingly
        //if ( (dataset_age != null) &&  (((comparator.equals(">=") ) && (Double.parseDouble(value) >= Double.parseDouble(dataset_age))) || ((comparator.equals("<=") ) && (Double.parseDouble(value) <= Double.parseDouble(dataset_age)))) ) {
        if ( (dataset_age != null) && (Double.parseDouble(age) == Double.parseDouble(dataset_age))){
          ageMatch = true;
        } else {
          ageMatch = false;
        }
        }

      datasetResponse.setAge(ageMatch);

      // Now that we have the True/False comparison values,
      // reset Exists to False if one of our new parameters doesn't match:
      if (!(ageMatch && tissueMatch && sampletypeMatch && cropnameMatch && ancestMatch && accenumbMatch && puidMatch)) {
        datasetResponse.setExists(false);
        datasetResponse.setNote("Metadata Mistmatch.");
        // If the dataset doesn't have all parameters correct, decrement the valid results by one:
        numResults = numResults - 1;
      }




      result.addDatasetPlantResponse(datasetResponse);




    }


  }
  globalExists = numResults > 0;



  Set<Integer> datasetIdsWithData =
      dataList.stream().map(data -> data.getDatasetId()).collect(Collectors.toSet());

  // Check that all requested datasets are present in the response
  // (maybe some of them are not present because they have no data for this query)
  @SuppressWarnings("unchecked")
  Collection<Integer> missingDatasets =
      CollectionUtils.disjunction(datasetIds, datasetIdsWithData);

  if (!missingDatasets.isEmpty() && (result.getPlantRequest()
      .getIncludeDatasetResponses() == FilterDatasetResponse.MISS
      || result.getPlantRequest().getIncludeDatasetResponses() == FilterDatasetResponse.ALL)) {
    for (Integer datasetId : missingDatasets) {
      DatasetPlantResponse datasetResponse = new DatasetPlantResponse();
      BeaconDataset dataset = beaconDatasetRepository.findOne(datasetId);
      datasetResponse.setDatasetId(dataset.getStableId());
      datasetResponse.setExists(false);
      result.addDatasetPlantResponse(datasetResponse);
    }
  }
  return globalExists;
}


  @Override
  public BeaconPlantResponse queryPlantBeacon(List<String> datasetStableIds, String variantType,
      String alternateBases, String referenceBases, String chromosome, Integer start,
      Integer startMin, Integer startMax, Integer end, Integer endMin, Integer endMax,
      String referenceGenome, String puid, String accenumb, String ancest, String cropname,
       String sampletype, String tissue, String age, String includeDatasetResponses) {

    BeaconPlantResponse result = new BeaconPlantResponse();

    alternateBases = StringUtils.upperCase(alternateBases);
    referenceBases = StringUtils.upperCase(referenceBases);

    BeaconPlantRequest request = BeaconPlantRequest.builder()
        .alternateBases(alternateBases)
        .referenceBases(referenceBases)
        .referenceName(chromosome)
        .datasetIds(datasetStableIds)
        .start(start)
        .startMin(startMin)
        .startMax(startMax)
        .end(end)
        .endMin(endMin)
        .endMax(endMax)
        .variantType(variantType)
        .assemblyId(referenceGenome)
        .puid(puid)
        .accenumb(accenumb)
        .ancest(ancest)
        .cropname(cropname)
        .sampletype(sampletype)
        .tissue(tissue)
        .age(age)
        .includeDatasetResponses(FilterDatasetResponse.parse(includeDatasetResponses))
        .build();
    result.setPlantRequest(request);

    VariantType type = VariantType.parse(variantType);

    List<Integer> datasetIds =
        checkPlantParams(result, datasetStableIds, type, alternateBases, referenceBases, chromosome,
            start, startMin, startMax, end, endMin, endMax, puid, accenumb, ancest,
            cropname, sampletype, tissue, age, referenceGenome);

    boolean globalExists = false;
    if (result.getError() == null) {
      globalExists = queryPlantDatabase(datasetIds, type, referenceBases, alternateBases, chromosome,
          start, startMin, startMax, end, endMin, endMax, referenceGenome, puid, accenumb,
          ancest, cropname, sampletype, tissue, age, result);

    }
    result.setExists(globalExists);
    return result;
  }

  @Override
  public List<Integer> checkPlantParams(BeaconPlantResponse result, List<String> datasetStableIds,
      VariantType type, String alternateBases, String referenceBases, String chromosome,
      Integer start, Integer startMin, Integer startMax, Integer end, Integer endMin,
      Integer endMax, String puid, String accenumb, String ancest,
      String cropname, String sampletype, String tissue, String age, String referenceGenome) {

    List<Integer> datasetIds = new ArrayList<>();

    if (StringUtils.isBlank(chromosome) || StringUtils.isBlank(referenceGenome) || StringUtils.isBlank(referenceBases)) {
      Error error = Error.builder()
          .errorCode(ErrorCode.GENERIC_ERROR)
          .message("All 'referenceName', 'referenceBases' and/or 'assemblyId' are required")
          .build();
      result.setError(error);
      return datasetIds;
    }
    //if (StringUtils.isNotBlank(referenceGenome)){
    //  boolean matches = Pattern.matches("^grch[1-9]{2}$", StringUtils.lowerCase(referenceGenome));
    //  if (!matches) {
    //    Error error = Error.builder().errorCode(ErrorCode.GENERIC_ERROR)
    //        .message("Invalid 'assemblyId' parameter, GRC notation required (e.g. GRCh37)")
    //        .build();
    //    result.setError(error);
    //    return datasetIds;
    //  }
    //}
    if (StringUtils.isNotBlank(chromosome)){
      boolean matches = Pattern.matches("^([1-9][0-9]|[1-9]|X|Y|MT|PLTD)$", chromosome);
      if (!matches) {
        Error error = Error.builder().errorCode(ErrorCode.GENERIC_ERROR)
            .message("Invalid 'referenceName' parameter, accepted values are 1-22, X, Y, MT, PLTD")
            .build();
        result.setError(error);
        return datasetIds;
      }
    }

    if (type == null && StringUtils.isBlank(alternateBases)) {
      Error error = Error.builder()
          .errorCode(ErrorCode.GENERIC_ERROR)
          .message("Either 'alternateBases' or 'variantType' is required")
          .build();
      result.setError(error);
    } else if (type != null && StringUtils.isNotBlank(alternateBases)
        && !StringUtils.equalsIgnoreCase(alternateBases, "N")) {
      Error error = Error.builder().errorCode(ErrorCode.GENERIC_ERROR)
          .message(
              "If 'variantType' is provided then 'alternateBases' must be empty or equal to 'N'")
          .build();
      result.setError(error);
      return datasetIds;
    }

    if (start == null) {
      if(end != null) {
        Error error = Error.builder()
            .errorCode(ErrorCode.GENERIC_ERROR)
            .message("'start' is required if 'end' is provided")
            .build();
        result.setError(error);
        return datasetIds;
      } else if (startMin == null && startMax == null && endMin == null && endMax == null) {
        Error error = Error.builder()
            .errorCode(ErrorCode.GENERIC_ERROR)
            .message("Either 'start' or all of 'startMin', 'startMax', 'endMin' and 'endMax' are required")
            .build();
        result.setError(error);
        return datasetIds;
      } else if (startMin == null || startMax == null || endMin == null || endMax == null) {
        Error error = Error.builder()
            .errorCode(ErrorCode.GENERIC_ERROR)
            .message("All of 'startMin', 'startMax', 'endMin' and 'endMax' are required")
            .build();
        result.setError(error);
        return datasetIds;
      }
    } else if (startMin != null || startMax != null || endMin != null || endMax != null) {
      Error error = Error.builder()
          .errorCode(ErrorCode.GENERIC_ERROR)
          .message("'start' cannot be provided at the same time as 'startMin', 'startMax', 'endMin' and 'endMax'")
          .build();
      result.setError(error);
      return datasetIds;
    } else if (end == null && StringUtils.equalsIgnoreCase(referenceBases, "N")) {
      Error error = Error.builder()
          .errorCode(ErrorCode.GENERIC_ERROR)
          .message("'referenceBases' cannot be 'N' if 'start' is provided and 'end' is missing")
          .build();
      result.setError(error);
      return datasetIds;
    }

    if (datasetStableIds != null) {
      // Remove empty/null strings
      datasetStableIds =
          datasetStableIds.stream().filter(s -> (StringUtils.isNotBlank(s)))
              .collect(Collectors.toList());

      for (String datasetStableId : datasetStableIds) {
        // 1) Dataset exists
        BeaconDataset dataset = beaconDatasetRepository.findByStableId(datasetStableId);
        if (dataset == null) {
          Error error = Error.builder()
              .errorCode(ErrorCode.NOT_FOUND)
              .message("Dataset not found")
              .build();
          result.setError(error);
          return datasetIds;
        } else {
          datasetIds.add(dataset.getId());
        }

        DatasetAccessType datasetAccessType = DatasetAccessType.parse(dataset.getAccessType());
        if (datasetAccessType != DatasetAccessType.PUBLIC) {
          Error error = Error.builder()
              .errorCode(ErrorCode.UNAUTHORIZED)
              .message("Unauthenticated users cannot access this dataset")
              .build();
          result.setError(error);
          return datasetIds;
        }

        // Check that the provided reference genome matches the one specified in the DB for this
        // dataset
        if (!StringUtils.equalsIgnoreCase(dataset.getReferenceGenome(), referenceGenome)) {
          Error error = Error.builder()
              .errorCode(ErrorCode.GENERIC_ERROR)
              .message("The assemblyId of this dataset (" + dataset.getReferenceGenome()
                  + ") and the provided value (" + referenceGenome + ") do not match")
              .build();
          result.setError(error);
          return datasetIds;
        }
      }
    }
    // Allele has a valid value
    if (StringUtils.isNotBlank(alternateBases)) {
      boolean matches = Pattern.matches("[ACTG]+|N", alternateBases);
      if (!matches) {
        Error error = Error.builder().errorCode(ErrorCode.GENERIC_ERROR)
            .message("Invalid 'alternateBases' parameter, it must match the pattern [ACTG]+|N")
            .build();
        result.setError(error);
        return datasetIds;
      }
    }
    if (StringUtils.isNotBlank(referenceBases)) {
      boolean matches = Pattern.matches("[ACTG]+|N", referenceBases);
      if (!matches) {
        Error error = Error.builder().errorCode(ErrorCode.GENERIC_ERROR)
            .message("Invalid 'referenceBases' parameter, it must match the pattern [ACTG]+|N").build();
        result.setError(error);
        return datasetIds;
      }
    }
    //    if (type != null && type != VariantType.SNP && type != VariantType.INSERTION
    //        && type != VariantType.DELELETION && type != VariantType.DUPLICATION) {
    //      Error error = Error.builder().errorCode(ErrorCode.GENERIC_ERROR)
    //          .message("Invalid 'variantType' parameter").build();
    //      result.setError(error);
    //      return datasetIds;
    //    }

    //    if (type != VariantType.SNP && type != VariantType.INSERTION && type != VariantType.DELELETION
    //        && type != VariantType.DUPLICATION) {
    //      Error error = Error.builder()
    //          .errorCode(ErrorCode.GENERIC_ERROR)
    //          .message("Invalid alternateBases parameter")
    //          .build();
    //      result.setError(error);
    //      return datasetIds;
    //

    // For now, no tests on the MCPD and BioSample parmeters.
    // Their exact format still needs to be defined and also what values would be considered to throw an error.

    return datasetIds;
  }

  @Override
  public BeaconPlantResponse queryPlantBeacon(BeaconRequest request) {

    return queryPlantBeacon(request.getDatasetIds(), request.getVariantType(),
        request.getAlternateBases(), request.getReferenceBases(), request.getReferenceName(),
        request.getStart(), request.getStartMin(), request.getStartMax(), request.getEnd(),
        request.getEndMin(), request.getEndMax(), request.getAssemblyId(),
        request.getPuid(), request.getAccenumb(), request.getAncest(),
        request.getCropname(), request.getSampletype(), request.getTissue(), request.getAge(),
        request.getIncludeDatasetResponses());
  }

}
