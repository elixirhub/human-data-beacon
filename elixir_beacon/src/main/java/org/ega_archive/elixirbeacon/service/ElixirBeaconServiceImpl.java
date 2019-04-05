package org.ega_archive.elixirbeacon.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.ega_archive.elixirbeacon.constant.BeaconConstants;
import org.ega_archive.elixirbeacon.convert.Operations;
import org.ega_archive.elixirbeacon.dto.AccessLevelResponse;
import org.ega_archive.elixirbeacon.dto.Beacon;
import org.ega_archive.elixirbeacon.dto.BeaconAlleleRequest;
import org.ega_archive.elixirbeacon.dto.BeaconAlleleResponse;
import org.ega_archive.elixirbeacon.dto.BeaconOntology;
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
import org.ega_archive.elixirbeacon.model.elixirbeacon.OntologyTerm;
import org.ega_archive.elixirbeacon.model.elixirbeacon.OntologyTermColumnCorrespondance;
import org.ega_archive.elixirbeacon.model.elixirbeacon.DatasetAccessLevel;
import org.ega_archive.elixirbeacon.model.elixirbeacon.QDatasetAccessLevel;
import org.ega_archive.elixirbeacon.properties.SampleRequests;
import org.ega_archive.elixirbeacon.repository.elixirbeacon.BeaconDatasetConsentCodeRepository;
import org.ega_archive.elixirbeacon.repository.elixirbeacon.BeaconDatasetRepository;
import org.ega_archive.elixirbeacon.repository.elixirbeacon.BeaconSummaryDataRepository;
import org.ega_archive.elixirbeacon.repository.elixirbeacon.OntologyTermColumnCorrespondanceRepository;
import org.ega_archive.elixirbeacon.repository.elixirbeacon.OntologyTermRepository;
import org.ega_archive.elixirbeacon.repository.elixirbeacon.DatasetAccessLevelRepository;
import org.ega_archive.elixircore.enums.DatasetAccessType;
import org.ega_archive.elixircore.helper.CommonQuery;
import org.ega_archive.elixircore.util.StoredProcedureUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ElixirBeaconServiceImpl implements ElixirBeaconService {

  public static final String ACCESS_LEVEL_SUMMARY = "accessLevelSummary";
  @Autowired
  private SampleRequests sampleRequests;

  @Autowired
  private BeaconDatasetRepository beaconDatasetRepository;
  
  @Autowired
  private BeaconSummaryDataRepository beaconDataRepository;
  
  @Autowired
  private BeaconDatasetConsentCodeRepository beaconDatasetConsentCodeRepository;

  @Autowired
  private OntologyTermColumnCorrespondanceRepository ontologyTermColumnCorrespondanceRepository;

  Autowired
  private DatasetAccessLevelRepository datasetAccessLevelRepository;

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

    Integer size = 0;
    for (BeaconDataset dataset : allDatasets) {
      DatasetAccessType accessType = DatasetAccessType.parse(dataset.getAccessType());
      boolean authorized = false;
      if (accessType == DatasetAccessType.PUBLIC) {
        authorized = true;
      }
      List<BeaconDatasetConsentCode> ccDataUseConditions =
          beaconDatasetConsentCodeRepository.findByDatasetId(dataset.getId());

      convertedDatasets.add(Operations.convert(dataset, authorized, ccDataUseConditions));

      size += dataset.getVariantCnt();
    }

    List<KeyValuePair> info = new ArrayList<>();
    info.add(new KeyValuePair(BeaconConstants.SIZE, size.toString()));

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
      String referenceGenome, String includeDatasetResponses, List<String> filters) {

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
        .filters(filters)
        .build();
    result.setAlleleRequest(request);
    
    VariantType type = VariantType.parse(variantType);

    List<String> translatedFilters = new ArrayList<>();
    List<Integer> datasetIds =
        checkParams(result, datasetStableIds, type, alternateBases, referenceBases, chromosome,
            start, startMin, startMax, end, endMin, endMax, referenceGenome, filters,
            translatedFilters);

    boolean globalExists = false;
    if (result.getError() == null) {
      globalExists = queryDatabase(datasetIds, type, referenceBases, alternateBases, chromosome,
          start, startMin, startMax, end, endMin, endMax, referenceGenome, translatedFilters, result);
    }
    result.setExists(globalExists);
    return result;
  }

  @Override
  public List<Integer> checkParams(BeaconAlleleResponse result, List<String> datasetStableIds,
      VariantType type, String alternateBases, String referenceBases, String chromosome,
      Integer start, Integer startMin, Integer startMax, Integer end, Integer endMin,
      Integer endMax, String referenceGenome, List<String> filters, List<String> translatedFilters) {

    List<Integer> datasetIds = new ArrayList<>();

    if (StringUtils.isBlank(chromosome) || StringUtils.isBlank(referenceGenome) || StringUtils.isBlank(referenceBases)) {
      Error error = Error.builder()
          .errorCode(ErrorCode.GENERIC_ERROR)
          .message("All 'referenceName', 'referenceBases' and/or 'assemblyId' are required")
          .build();
      result.setError(error);
      return datasetIds;
    }
    if (StringUtils.isNotBlank(referenceGenome)){
      boolean matches = Pattern.matches("^grch[1-9]{2}$", StringUtils.lowerCase(referenceGenome));
      if (!matches) {
        Error error = Error.builder().errorCode(ErrorCode.GENERIC_ERROR)
            .message("Invalid 'assemblyId' parameter, GRC notation required (e.g. GRCh37)")
            .build();
        result.setError(error);
        return datasetIds;
      }
    }
    if (StringUtils.isNotBlank(chromosome)){
      boolean matches = Pattern.matches("^([1-9][0-9]|[1-9]|X|Y|MT)$", chromosome);
      if (!matches) {
        Error error = Error.builder().errorCode(ErrorCode.GENERIC_ERROR)
            .message("Invalid 'referenceName' parameter, accepted values are 1-22, X, Y, MT")
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

    if (filters != null) {
      if (translateFilters(result, filters, translatedFilters)) {
        return datasetIds;
      }
      log.debug("Filters: {}", translatedFilters);
    }

    return datasetIds;
  }

  private boolean translateFilters(BeaconAlleleResponse result, List<String> filters,
      List<String> translatedFilters) {

    // PATO:0000383,HP:0011007=>49,EFO:0009656

    for (String filter : filters) {
      // Remove spaces before, between or after words
      filter = filter.replaceAll("\\s+", "");
      String[] tokens = filter.split(":");
      String ontology = tokens[0];
      String term = tokens[1];
      String value = null;

      String filterOperators = "\\d(<=|>=|=|<|>)\\d";
      Pattern p = Pattern.compile(filterOperators);   // the pattern to search for
      Matcher m = p.matcher(term);
      String operator = "="; // Default operator
      if (m.find()) {
        operator = m.group(1);
        String[] operationTokens = term.split("(<=|>=|=|<|>)");
        term = operationTokens[0];
        value = operationTokens[1];
      }
      // Search this ontology and term in the DB
      OntologyTermColumnCorrespondance ontologyTerm = ontologyTermColumnCorrespondanceRepository
          .findByOntologyAndTerm(ontology, term);
      if (ontologyTerm == null) {
        Error error = Error.builder()
            .errorCode(ErrorCode.GENERIC_ERROR)
            .message("Ontology (" + ontology + ") and/or term (" + term
                + ") not known in this Beacon. Remember that only the following operators are accepted in some terms (e.g. age_of_onset): "
                + "<=, >=, =, <, >")
            .build();
        result.setError(error);
        return true;
      }
      if (StringUtils.isNotEmpty(ontologyTerm.getSampleTableColumnValue())) {
        value = ontologyTerm.getSampleTableColumnValue();
      }
      try {
        Integer.parseInt(value);
      } catch(NumberFormatException e) {
        // It's not an Integer -> surround with '
        value = "'" + value + "'";
      }
      value = operator + value;
//      log.debug("Value: {}", value);
      translatedFilters.add(ontologyTerm.getSampleTableColumnName() + value);
    }
    return false;
  }

  private boolean queryDatabase(List<Integer> datasetIds, VariantType type, String referenceBases,
      String alternateBases, String chromosome, Integer start, Integer startMin, Integer startMax,
      Integer end, Integer endMin, Integer endMax, String referenceGenome, List<String> translatedFilters,
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
            + "endMin={}, endMax={}, chrom={}, reference={}, alternate={}, assemlbyId={}, "
            + "datasetIds={}, filters={}", variantType, start, startMin, startMax, end, endMin, endMax,
        chromosome, referenceBases, alternateBases, referenceGenome, datasetIds, translatedFilters);

    String filters = null;
    if (translatedFilters != null && !translatedFilters.isEmpty()) {
      filters = StoredProcedureUtils.joinArrayOfString(translatedFilters, " AND ");
    }
    List<BeaconDataSummary> dataList = beaconDataRepository
        .searchForVariantsQuery(variantType, start,
            startMin, startMax, end, endMin, endMax, chromosome, referenceBases, alternateBases,
            referenceGenome, StoredProcedureUtils.joinArrayOfInteger(datasetIds), filters);
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
        datasetResponse.setVariantCount(new Long(data.getVariantCnt()));
        datasetResponse.setCallCount(new Long(data.getCallCnt()));
        datasetResponse.setSampleCount(new Long(data.getSampleCnt()));
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
        request.getIncludeDatasetResponses(), request.getFilters());
  }

  @Override
  public AccessLevelResponse listAccessLevels(List<String> fields,
      List<String> datasetStableIds, String level, boolean includeFieldDetails,
      boolean includeDatasetDetails) {

    // TODO implement search by "level"

    AccessLevelResponse response = new AccessLevelResponse();
    response.setBeaconId(BeaconConstants.BEACON_ID);

    ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
    try {
      InputStream input = this.getClass().getClassLoader().getResourceAsStream("access_levels.yaml");
      @SuppressWarnings("unchecked")
      Map<String, Object> map = objectMapper.readValue(input, Map.class);
      map = searchFieldsInMap(map, fields, includeFieldDetails);
      if (map == null || map.isEmpty()) {
        Error error = new Error(ErrorCode.NOT_FOUND, "Field not found");
        response.setError(error);
      }
      response.setFields(map);
      printMapInConsole(map);
    } catch (IOException e) {
      log.error("Exception parsing yaml", e);
    }

    Iterable<DatasetAccessLevel> datasetAccessLevels = findDatasets(fields, datasetStableIds,
        includeFieldDetails, includeDatasetDetails, response);

    if (datasetStableIds != null && !datasetStableIds.isEmpty()
        && (datasetAccessLevels == null || !datasetAccessLevels.iterator().hasNext())) {
      Error error = new Error(ErrorCode.NOT_FOUND, "Dataset(s) not found");
      response.setError(error);
    }
    if (response.getError() == null) {
      Map<String, Object> datasetsMap = fillDatasetsMap(datasetAccessLevels, includeFieldDetails,
          includeDatasetDetails);
      response.setDatasets(datasetsMap);
    }

    return response;
  }

  private Iterable<DatasetAccessLevel> findDatasets(List<String> fields, List<String> datasetStableIds,
      boolean includeFieldDetails, boolean includeDatasetDetails, AccessLevelResponse response) {
    // Get datasets
    BooleanExpression condition = null;
    QDatasetAccessLevel qDatasetAccessLevel = QDatasetAccessLevel.datasetAccessLevel;
    if (response.getError() == null && datasetStableIds != null && !datasetStableIds.isEmpty()) {
      condition = qDatasetAccessLevel.id.datasetStableId.in(datasetStableIds);
    }
    if (fields != null && !fields.isEmpty()) {
      BooleanExpression fieldsIn = null;
      if (includeFieldDetails) {
        // Look for a match among the parent fields or children
        fieldsIn = qDatasetAccessLevel.id.parentField.toLowerCase().in(fields)
            .or(qDatasetAccessLevel.id.field.toLowerCase().in(fields));
      } else {
        // Look for a match only among the parent fields
        fieldsIn = qDatasetAccessLevel.id.parentField.toLowerCase().in(fields);
      }
      if (condition != null) {
        condition = condition.and(fieldsIn);
      } else {
        condition = fieldsIn;
      }
    } else if(!includeDatasetDetails) {
      // Only show the summary
      BooleanExpression findSummary = qDatasetAccessLevel.id.parentField.toLowerCase().eq(ACCESS_LEVEL_SUMMARY.toLowerCase());
      if (condition != null) {
        condition = condition.and(findSummary);
      } else {
        condition = findSummary;
      }
    }
    Iterable<DatasetAccessLevel> datasetAccessLevels = null;
    if (condition != null) {
      datasetAccessLevels = datasetAccessLevelRepository.findAll(condition);
    } else {
      datasetAccessLevels = datasetAccessLevelRepository.findAll();
    }
    return datasetAccessLevels;
  }

  private Map<String, Object> fillDatasetsMap(Iterable<DatasetAccessLevel> datasetFields,
      boolean includeFieldDetails, boolean includeDatasetDetails) {

    Map<String, Object> datasetsMap = new HashMap<>();
    datasetFields.forEach(d -> {
      if (!includeDatasetDetails) {
        if (ACCESS_LEVEL_SUMMARY.equalsIgnoreCase(d.getId().getParentField())) {
          datasetsMap.put(d.getId().getDatasetStableId(), d.getAccessLevel());
        }
      } else { // includeDatasetDetails = true
        if (ACCESS_LEVEL_SUMMARY.equalsIgnoreCase(d.getId().getParentField())) {
          // Skip field
          return;
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> parentFieldsMap = (Map<String, Object>) datasetsMap.get(d.getId().getDatasetStableId());
        if (parentFieldsMap == null) {
          parentFieldsMap = new HashMap<>();
        }

        if (includeFieldDetails) {
          @SuppressWarnings("unchecked")
          Map<String, String> fieldsMap = (Map<String, String>) parentFieldsMap.get(d.getId().getParentField());
          if (fieldsMap == null) {
            fieldsMap = new HashMap<>();
          }
          fieldsMap.put(d.getId().getField(), d.getAccessLevel());
          parentFieldsMap.put(d.getId().getParentField(), fieldsMap);
        } else {
          parentFieldsMap.put(d.getId().getParentField(), d.getAccessLevel());
        }
        datasetsMap.put(d.getId().getDatasetStableId(), parentFieldsMap);
      }
    });
    return datasetsMap;
  }

  private void printMapInConsole(Map<String, Object> map) {
    map.entrySet()
//        .stream()
        .forEach(e -> {
          log.debug("{} :", e.getKey());
          if (e.getValue() instanceof Map) {
            ((Map<String, Object>) e.getValue())
                .forEach((key, value) -> {
                  if (value instanceof String) {
                    log.debug(" - {} : {}", key, value);
                  } else if (value instanceof Map) {
                    log.debug(" - {} :", key);
                    ((Map<String, Object>) value)
                        .entrySet()
                        .forEach( entry -> {
                          if(entry.getValue() instanceof String) {
                            log.debug("  - {} : {}", entry.getKey(), entry.getValue());
                          } else if (entry.getValue() instanceof Map) {
                            log.debug("   - {} :", entry.getKey());
                            ((Map<String, String>) entry.getValue())
                                .forEach( (key2, value2) -> {
                                  log.debug("     - {} :", key2, value2);
                                });
                          }
                        });
                  }
                });
          } else if (e.getValue() instanceof String) {
            log.debug(" - {}", e.getValue());
          }

        });
//    map.entrySet()
////        .stream()
//        .forEach(e -> {
//          log.debug("{} :", e.getKey());
//          ((Map<String, String>) e.getValue())
//              .forEach((key, value) -> {
//                log.debug(" - {} : {}", key, value);
//              });
//        });
  }

  private Map<String, Object> searchFieldsInMap(Map<String, Object> map, List<String> fields,
      boolean includeFieldDetails) {

    Map<String, Object> newMap;

    if (fields != null && !fields.isEmpty()) {
      newMap = new HashMap<>();
      fields.replaceAll(String::toLowerCase);

      map.entrySet()
          .stream()
          .filter(entry -> fields.contains(entry.getKey().toLowerCase())
              || (includeFieldDetails && CollectionUtils.containsAny(convertToLowerCase(entry), fields))
          )
          .forEach(entry -> {
            String key = entry.getKey();
            if (!fields.contains(entry.getKey().toLowerCase()) &&
                (includeFieldDetails && CollectionUtils.containsAny(convertToLowerCase(entry), fields))) {
              // Match found among the inner keys
              copyInnerMap(fields, newMap, entry);
            } else if(!includeFieldDetails) {
              // Match found among the outer keys -> Show summary value
              @SuppressWarnings("unchecked")
              Map<String, String> value = (Map<String, String>) entry.getValue();
              newMap.put(key, value.get(ACCESS_LEVEL_SUMMARY));
            } else {
              // Match found among the outer keys
              newMap.put(key, entry.getValue());
            }
          });
    } else if(!includeFieldDetails) {
      newMap = showSummary(map);
    } else {
      newMap = map;
    }
    return newMap;
  }

  @SuppressWarnings("unchecked")
  private void copyInnerMap(List<String> fields, Map<String, Object> newMap,
      Entry<String, Object> entry) {

    ((Map<String, String>) entry.getValue())
        .entrySet()
        .stream()
        .filter(innerEntry -> fields.contains(innerEntry.getKey().toLowerCase()))
        .forEach(innerEntry -> {
          String value = innerEntry.getValue();

          Map<String, String> newInnerMap = (Map<String, String>) newMap.get(entry.getKey());
          if (newInnerMap == null) {
            // The key does not exist -> initialize
            newInnerMap = new HashMap<>();
            newInnerMap.put(innerEntry.getKey(), value);
          }
          // Add the value to this key
          newInnerMap.put(innerEntry.getKey(), value);
          // Add the inner map to the outer one
          newMap.put(entry.getKey(), newInnerMap);
        });
  }

  private Map<String, Object> showSummary(Map<String, Object> map) {
    Map<String, Object> newMap = new HashMap<>();
    map.entrySet()
        .forEach(entry -> {
          @SuppressWarnings("unchecked")
          Map<String, Object> value = (Map<String, Object>) entry.getValue();
          String summaryValue = (String) value.get(ACCESS_LEVEL_SUMMARY);
          newMap.put(entry.getKey(), summaryValue);
        });
    return newMap;
  }

  @SuppressWarnings("unchecked")
  private Set<String> convertToLowerCase(Entry<String, Object> entry) {
    return ((Map<String, String>) entry.getValue())
        .keySet()
        .stream()
        .map(key -> key.toLowerCase())
        .collect(Collectors.toSet());
  }

}
