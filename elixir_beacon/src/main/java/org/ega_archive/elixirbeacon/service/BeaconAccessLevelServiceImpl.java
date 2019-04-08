package org.ega_archive.elixirbeacon.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.ega_archive.elixirbeacon.constant.BeaconConstants;
import org.ega_archive.elixirbeacon.dto.AccessLevelResponse;
import org.ega_archive.elixirbeacon.dto.Error;
import org.ega_archive.elixirbeacon.enums.ErrorCode;
import org.ega_archive.elixirbeacon.model.elixirbeacon.DatasetAccessLevel;
import org.ega_archive.elixirbeacon.model.elixirbeacon.QDatasetAccessLevel;
import org.ega_archive.elixirbeacon.repository.elixirbeacon.DatasetAccessLevelRepository;
import org.ega_archive.elixircore.exception.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BeaconAccessLevelServiceImpl implements BeaconAccessLevelService {

  public static final String ACCESS_LEVEL_SUMMARY = "accessLevelSummary";

  @Autowired
  private DatasetAccessLevelRepository datasetAccessLevelRepository;

  @Override
  public AccessLevelResponse listAccessLevels(List<String> fields,
      List<String> datasetStableIds, String level, boolean includeFieldDetails,
      boolean includeDatasetDetails) {

    // TODO implement search by "level"
    if (StringUtils.isNotBlank(level)) {
      throw new NotImplementedException("Searching by 'level' is not implemented yet!");
    }

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
//      TestUtils.printMapInConsole(map);
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

  private Map<String, Object> searchFieldsInMap(Map<String, Object> map, List<String> fields,
      boolean includeFieldDetails) {

    Map<String, Object> newMap;

    if (fields != null && !fields.isEmpty()) {
      newMap = new HashMap<>();
      fields.replaceAll(String::toLowerCase);

      map.entrySet()
          .stream()
          .filter(entry -> fields.contains(entry.getKey().toLowerCase())
              || (includeFieldDetails && CollectionUtils
              .containsAny(convertToLowerCase(entry), fields))
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
