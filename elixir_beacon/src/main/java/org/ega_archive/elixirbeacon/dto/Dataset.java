package org.ega_archive.elixirbeacon.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.ega_archive.elixirbeacon.constant.BeaconConstants;
import org.ega_archive.elixirbeacon.dto.datause.DataUseCondition;
import org.ega_archive.elixirbeacon.enums.AccessLevel;
import org.joda.time.DateTime;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dataset {

  @JsonIgnore
  private String[] fields = {"id", "name", "description", "assemblyId", "createDateTime",
      "updateDateTime", "dataUseConditions", "version", "variantCount", "callCount", "sampleCount",
      "externalUrl", "info"};

  private String id;

  private String name;

  private String description;

  private String assemblyId;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = BeaconConstants.ISO8601_DATE_TIME_PATTERN)
  private DateTime createDateTime;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = BeaconConstants.ISO8601_DATE_TIME_PATTERN)
  private DateTime updateDateTime;

  private DataUseCondition dataUseConditions;
  
  private String version;

  private Long variantCount;

  private Long callCount;

  private Long sampleCount;

  private String externalUrl;

  private Map<String, Object> info;
  
//  public void addDataUseCondition(DataUseCondition condition) {
//    if(dataUseConditions == null) {
//      dataUseConditions = new ArrayList<DataUseCondition>();
//    }
//    dataUseConditions.add(condition);
//  }

  public Map<String, Object> toMap(Map<String, Object> accessLevelFields, boolean isAuthenticated) {

    Map<String, Object> defaultAccessLevelDatasets = (Map<String, Object>) accessLevelFields
        .get("beaconDataset");

    Map<String, Object> map = new LinkedHashMap<>();
    if (defaultAccessLevelDatasets == null) {
      return map;
    }

    final BeanWrapper src = new BeanWrapperImpl(this);
    for (String fieldName : fields) {
      Object fieldValue = src.getPropertyValue(fieldName);
      String value = (String) defaultAccessLevelDatasets.get(fieldName);

      if (StringUtils.isNotBlank(value)
          && (!StringUtils.equalsIgnoreCase(value, AccessLevel.NOT_SUPPORTED.getLevel())
          && (StringUtils.equalsIgnoreCase(value, AccessLevel.PUBLIC.getLevel()) || isAuthenticated))) {

        if (value.equalsIgnoreCase(AccessLevel.CONTROLLED.getLevel())) {
          boolean userIsAuthorized = Boolean.parseBoolean((String) this.info.get("authorized"));
          if (!userIsAuthorized) {
            // User is not authorized to access this dataset -> skip field
            continue;
          }
        }
        if(fieldName.equalsIgnoreCase("dataUseConditions")) {
          map.put(fieldName, dataUseConditions.toMap(accessLevelFields, isAuthenticated));
        } else {
          map.put(fieldName, fieldValue);
        }
      }
    }
    return map;
  }

}
