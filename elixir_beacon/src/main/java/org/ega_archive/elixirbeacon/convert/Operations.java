package org.ega_archive.elixirbeacon.convert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javassist.NotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.ega_archive.elixirbeacon.constant.BeaconConstants;
import org.ega_archive.elixirbeacon.dto.BeaconAlleleRequest;
import org.ega_archive.elixirbeacon.dto.BeaconOntology;
import org.ega_archive.elixirbeacon.dto.BeaconOntologyTerm;
import org.ega_archive.elixirbeacon.dto.Dataset;
import org.ega_archive.elixirbeacon.dto.DatasetAlleleResponse;
import org.ega_archive.elixirbeacon.dto.Error;
import org.ega_archive.elixirbeacon.dto.Handover;
import org.ega_archive.elixirbeacon.dto.HandoverType;
import org.ega_archive.elixirbeacon.dto.Organization;
import org.ega_archive.elixirbeacon.dto.Variant;
import org.ega_archive.elixirbeacon.dto.datause.DataUseCondition;
import org.ega_archive.elixirbeacon.dto.datause.consent_code.ConsentCode;
import org.ega_archive.elixirbeacon.dto.datause.consent_code.ConsentCodeCondition;
import org.ega_archive.elixirbeacon.enums.AccessLevel;
import org.ega_archive.elixirbeacon.enums.consent_code.ConsentCodeCategory;
import org.ega_archive.elixirbeacon.model.elixirbeacon.BeaconDataset;
import org.ega_archive.elixirbeacon.model.elixirbeacon.BeaconDatasetConsentCode;
import org.ega_archive.elixirbeacon.model.elixirbeacon.OntologyTerm;
import org.ega_archive.elixircore.enums.DatasetAccessType;
import org.ega_archive.elixircore.exception.PreConditionFailed;
import org.joda.time.DateTime;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class Operations {

  public static Dataset convert(BeaconDataset dataset, boolean authorized,
      List<BeaconDatasetConsentCode> ccDataUseConditions) throws NotFoundException {

    Dataset beaconDataset = new Dataset();
    beaconDataset.setId(dataset.getStableId());// Use the Stable Id as the ID to be used by the user
    beaconDataset.setDescription(dataset.getDescription());
    beaconDataset.setVariantCount(new Long(dataset.getVariantCnt()));
    beaconDataset.setCallCount(new Long(dataset.getCallCnt()));
    beaconDataset.setSampleCount(new Long(dataset.getSampleCnt()));
    beaconDataset.setAssemblyId(dataset.getReferenceGenome());

    Map<String, Object> info = new HashMap<>();
    info.put(BeaconConstants.ACCESS_TYPE, DatasetAccessType.parse(dataset.getAccessType()).getType());
    info.put(BeaconConstants.AUTHORIZED, Boolean.toString(authorized));
    beaconDataset.setInfo(info);

    DataUseCondition dataUseCondition = new DataUseCondition();
    dataUseCondition.setConsentCodeDataUse(convertConsentCodes(ccDataUseConditions));
    beaconDataset.setDataUseConditions(dataUseCondition);

    return beaconDataset;
  }

  private static ConsentCode convertConsentCodes(List<BeaconDatasetConsentCode> ccDataUseConditions)
      throws NotFoundException {

    ConsentCode consentCode = new ConsentCode();
    String abreviation = null;
    ConsentCodeCondition condition = null;

    List<String> versions = new ArrayList<>();
    for (BeaconDatasetConsentCode beaconDatasetConsentCode : ccDataUseConditions) {
      ConsentCodeCategory category =
          ConsentCodeCategory.parse(beaconDatasetConsentCode.getCategory());
      switch (category) {
        case PRIMARY:
          condition = new ConsentCodeCondition();
          abreviation = beaconDatasetConsentCode.getId().getCode();
          condition.setCode(abreviation);
          condition.setDescription(beaconDatasetConsentCode.getDescription());
          condition.setAdditionalConstraint(beaconDatasetConsentCode.getAdditionalConstraint());
          consentCode.setPrimaryCategory(condition);
          break;
        case REQUIREMENTS:
          condition = new ConsentCodeCondition();
          abreviation = beaconDatasetConsentCode.getId().getCode();
          condition.setCode(abreviation);
          condition.setDescription(beaconDatasetConsentCode.getDescription());
          condition.setAdditionalConstraint(beaconDatasetConsentCode.getAdditionalConstraint());
          consentCode.addRequirement(condition);
          break;
        case SECONDARY:
          condition = new ConsentCodeCondition();
          abreviation = beaconDatasetConsentCode.getId().getCode();
          condition.setCode(abreviation);
          condition.setDescription(beaconDatasetConsentCode.getDescription());
          condition.setAdditionalConstraint(beaconDatasetConsentCode.getAdditionalConstraint());
          consentCode.addSecondaryCategory(condition);
          break;
        default:
          break;
      }
      versions.add(beaconDatasetConsentCode.getVersion());
    }
    // Remove duplicates
    versions = versions.stream().distinct().collect(Collectors.toList());
    if (!ccDataUseConditions.isEmpty() && versions.isEmpty()) { // Should not happen
      throw new NotFoundException("No Consent Code version found");
    }
    if (versions.size() > 1) {
      throw new PreConditionFailed("Found different versions of Consent Codes: " + versions);
    }
    if (!versions.isEmpty()) {
      consentCode.setVersion(versions.get(0));
    }
    return consentCode;
  }

  public static BeaconOntology convertToBeaconOntologyTerm(List<OntologyTerm> all) {
    BeaconOntology result = new BeaconOntology();
    List<BeaconOntologyTerm> list = new ArrayList<>();

    for (OntologyTerm value : all) {
      list.add(BeaconOntologyTerm.builder()
          .ontology(value.getOntology())
          .term(value.getTerm())
          .label(value.getLabel())
          .build());
    }
    result.setOntologyTerms(list);

    return result;
  }

  @SuppressWarnings("unchecked")
  public static Map<String, Object> convertToMap(Object obj, String[] fields,
      String key, Map<String, Object> accessLevelFields,
      boolean isAuthenticated) {

    Map<String, Object> map = new LinkedHashMap<>();

    if (accessLevelFields == null) {
      return map;
    }
    Map<String, String> accessLevelCurrent = (Map<String, String>) accessLevelFields.get(key);
    if (accessLevelCurrent == null) {
      return map;
    }

    final BeanWrapper src = new BeanWrapperImpl(obj);

    Arrays.asList(fields)
        .stream()
        .filter(fieldName -> accessLevelCurrent.containsKey(fieldName))
        .forEach(fieldName -> {
          String value = accessLevelCurrent.get(fieldName);
          if (StringUtils.isNotBlank(value)
              && (!StringUtils.equalsIgnoreCase(value, AccessLevel.NOT_SUPPORTED.getLevel())
              && (StringUtils.equalsIgnoreCase(value, AccessLevel.PUBLIC.getLevel())
              || isAuthenticated))) {
            // Field is public or is registered/controlled and user is authenticated

            Object fieldValue = src.getPropertyValue(fieldName);

            if (StringUtils.equalsIgnoreCase(fieldName, "createDateTime")) {
              fieldValue = ((DateTime) fieldValue)
                  .toString(BeaconConstants.ISO8601_DATE_TIME_PATTERN);
            } else if (StringUtils.equalsIgnoreCase(fieldName, "updateDateTime")) {
              fieldValue = ((DateTime) fieldValue)
                  .toString(BeaconConstants.ISO8601_DATE_TIME_PATTERN);
            }
            convertGenericTypeToMap(map, fieldValue, fieldName, accessLevelFields, isAuthenticated);
          }
        });

    return map;
  }

  @SuppressWarnings("unchecked")
  private static void convertGenericTypeToMap(Map<String, Object> map, Object fieldValue, String fieldName,
      Map<String, Object> accessLevelFields, boolean isAuthenticated) {

    if (fieldValue == null) {
      map.put(fieldName, null);
    } else if (fieldValue instanceof List) {
      List<Map<String, Object>> consentsMap = (List<Map<String, Object>>) convertListToMap(
          accessLevelFields, fieldName, isAuthenticated, (List<?>) fieldValue);
      map.put(fieldName, consentsMap);
    } else if (fieldValue instanceof Organization) {
      map.put(fieldName, ((Organization) fieldValue).toMap(accessLevelFields, isAuthenticated));
    } else if(fieldValue instanceof  BeaconAlleleRequest) {
      map.put(fieldName, ((BeaconAlleleRequest) fieldValue).toMap(accessLevelFields, fieldName,
          isAuthenticated));
    } else if(fieldValue instanceof ConsentCode) {
      map.put(fieldName, ((ConsentCode) fieldValue).toMap(accessLevelFields, isAuthenticated));
    } else if(fieldValue instanceof ConsentCodeCondition) {
      map.put(fieldName, ((ConsentCodeCondition) fieldValue).toMap(accessLevelFields, isAuthenticated));
    } else if(fieldValue instanceof Error) {
      map.put(fieldName, ((Error) fieldValue).toMap(accessLevelFields, isAuthenticated));
    } else if(fieldValue instanceof HandoverType) {
      map.put(fieldName, ((HandoverType) fieldValue).toMap(accessLevelFields, isAuthenticated));
    } else {
      map.put(fieldName, fieldValue);
    }
  }

  private static <T> List<?> convertListToMap(Map<String, Object> accessLevelFields, String key,
      boolean isAuthenticated, List<T> fieldValue) {

    boolean isSimpleList = false;
    List<Map<String, Object>> map = new ArrayList<>();
    List<Object> listObj = new ArrayList<>();
    List<T> list = fieldValue;
    if (list != null) {
      for (T object : list) {
        if (object instanceof DatasetAlleleResponse) {
          map.add(((DatasetAlleleResponse) object).toMap(accessLevelFields, isAuthenticated));
        } else if (object instanceof Dataset) {
          map.add(((Dataset) object).toMap(accessLevelFields, isAuthenticated));
        } else if (object instanceof BeaconAlleleRequest) {
          map.add(((BeaconAlleleRequest) object).toMap(accessLevelFields, key, isAuthenticated));
        } else if (object instanceof ConsentCodeCondition) {
          map.add(((ConsentCodeCondition) object).toMap(accessLevelFields, isAuthenticated));
        } else if (object instanceof Handover) {
          map.add(((Handover) object).toMap(accessLevelFields, key, isAuthenticated));
        } else if (object instanceof Variant) {
          map.add(((Variant) object).toMap(accessLevelFields, isAuthenticated));
        } else {
          isSimpleList = true;
          listObj.add(object);
        }
      }
    }
    return isSimpleList ? listObj : map;
  }

}
