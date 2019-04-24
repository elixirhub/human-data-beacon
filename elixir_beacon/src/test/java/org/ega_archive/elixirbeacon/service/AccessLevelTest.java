package org.ega_archive.elixirbeacon.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.integration.test.matcher.MapContentMatchers.hasAllEntries;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.ega_archive.elixirbeacon.Application;
import org.ega_archive.elixirbeacon.dto.AccessLevelResponse;
import org.ega_archive.elixirbeacon.enums.ErrorCode;
import org.ega_archive.elixircore.test.util.TestUtils;
import org.ega_archive.elixircore.util.JsonUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@WebAppConfiguration
@SpringBootTest("server.port:0")
public class AccessLevelTest {

  @Autowired
  private BeaconAccessLevelService beaconAccessLevelService;

  @Resource(name = "elixirbeaconDataSource")
  private DataSource dataSource;

  @Autowired
  private ObjectMapper objectMapper;

  @Before
  public void setUp() throws SQLException {
    TestUtils.removeUserFromContext();

    // Truncate + Insert
    TestUtils.populateDatabase(dataSource,
        "/db/truncate_tables.sql",
        "/db/alter_schema.sql",
        // Beacon
        "/db/beacon_dataset_table.sql",
        // Access level
        "/db/dataset_access_level_table.sql"
    );
  }

  @Test
  public void getAllAccessLevels() {
    Map<String, Object> expectedMap = fillExpectedMapWithAllFields();
    Map<String, Object> expectedDatasets = fillExpectedDatasetsMapWithAllFields();

    AccessLevelResponse accessLevels = beaconAccessLevelService.listAccessLevels(null, null, null,
        true, true);

    assertThat(accessLevels.getFields().size(), equalTo(8));
    assertThat(accessLevels.getFields(), hasAllEntries(expectedMap));
    assertThat(expectedMap, hasAllEntries(accessLevels.getFields()));

    assertThat(accessLevels.getDatasets().size(), equalTo(2));
    assertThat(accessLevels.getDatasets(), hasAllEntries(expectedDatasets));
    assertThat(expectedDatasets, hasAllEntries(accessLevels.getDatasets()));
  }

  private Map<String, Object> fillExpectedMapWithAllFields() {
    Map<String, Object> expectedMap = new HashMap<>();
    HashMap<String, String> innerMap = new HashMap<>();
    innerMap.put("accessLevelSummary", "PUBLIC");
    innerMap.put("id", "PUBLIC");
    innerMap.put("name", "PUBLIC");
    innerMap.put("apiVersion", "PUBLIC");
    innerMap.put("organization", "PUBLIC");
    innerMap.put("description", "PUBLIC");
    innerMap.put("version", "PUBLIC");
    innerMap.put("welcomeUrl", "PUBLIC");
    innerMap.put("alternativeUrl", "PUBLIC");
    innerMap.put("createDateTime", "PUBLIC");
    innerMap.put("updateDateTime", "PUBLIC");
    innerMap.put("description", "PUBLIC");
    innerMap.put("datasets", "PUBLIC");
    innerMap.put("sampleAlleleRequests", "PUBLIC");
    innerMap.put("info", "PUBLIC");
    expectedMap.put("beacon", innerMap);
    innerMap = new HashMap<>();
    innerMap.put("accessLevelSummary", "PUBLIC");
    innerMap.put("id", "PUBLIC");
    innerMap.put("name", "PUBLIC");
    innerMap.put("description", "PUBLIC");
    innerMap.put("address", "PUBLIC");
    innerMap.put("welcomeUrl", "PUBLIC");
    innerMap.put("contactUrl", "PUBLIC");
    innerMap.put("logoUrl", "PUBLIC");
    innerMap.put("info", "PUBLIC");
    expectedMap.put("beaconOrganization", innerMap);
    innerMap = new HashMap<>();
    innerMap.put("accessLevelSummary", "REGISTERED");
    innerMap.put("id", "PUBLIC");
    innerMap.put("name", "PUBLIC");
    innerMap.put("description", "PUBLIC");
    innerMap.put("assemblyId", "PUBLIC");
    innerMap.put("createDateTime", "PUBLIC");
    innerMap.put("updateDateTime", "PUBLIC");
    innerMap.put("version", "PUBLIC");
    innerMap.put("variantCount", "PUBLIC");
    innerMap.put("callCount", "PUBLIC");
    innerMap.put("sampleCount", "PUBLIC");
    innerMap.put("externalUrl", "PUBLIC");
    innerMap.put("info", "PUBLIC");
    innerMap.put("dataUseConditions", "PUBLIC");
    innerMap.put("new_sensitive_field", "REGISTERED");
    innerMap.put("new_field", "NOT_SUPPORTED");
    expectedMap.put("beaconDataset", innerMap);
    innerMap = new HashMap<>();
    innerMap.put("accessLevelSummary", "PUBLIC");
    innerMap.put("referenceName", "PUBLIC");
    innerMap.put("start", "PUBLIC");
    innerMap.put("end", "PUBLIC");
    innerMap.put("startMin", "PUBLIC");
    innerMap.put("startMax", "PUBLIC");
    innerMap.put("endMin", "PUBLIC");
    innerMap.put("endMax", "PUBLIC");
    innerMap.put("referenceBases", "PUBLIC");
    innerMap.put("alternateBases", "PUBLIC");
    innerMap.put("variantType", "PUBLIC");
    innerMap.put("assemblyId", "PUBLIC");
    innerMap.put("datasetIds", "PUBLIC");
    innerMap.put("includeDatasetResponses", "PUBLIC");
    expectedMap.put("beaconAlleleRequest", innerMap);
    innerMap = new HashMap<>();
    innerMap.put("accessLevelSummary", "PUBLIC");
    innerMap.put("beaconId", "PUBLIC");
    innerMap.put("apiVersion", "PUBLIC");
    innerMap.put("exists", "PUBLIC");
    innerMap.put("error", "PUBLIC");
    innerMap.put("info", "PUBLIC");
    innerMap.put("datasetAlleleResponses", "PUBLIC");
    innerMap.put("alleleRequest", "PUBLIC");
    expectedMap.put("beaconAlleleResponse", innerMap);
    innerMap = new HashMap<>();
    innerMap.put("accessLevelSummary", "REGISTERED");// or public?
    innerMap.put("datasetId", "PUBLIC");
    innerMap.put("exists", "PUBLIC");
    innerMap.put("error", "PUBLIC");
    innerMap.put("frequency", "PUBLIC");
    innerMap.put("variantCount", "PUBLIC");
    innerMap.put("callCount", "PUBLIC");
    innerMap.put("sampleCount", "PUBLIC");
    innerMap.put("note", "PUBLIC");
    innerMap.put("externalUrl", "PUBLIC");
    innerMap.put("info", "PUBLIC");
    innerMap.put("new_sensitive_field", "REGISTERED");
    innerMap.put("new_non_sensitive_data", "PUBLIC");
    expectedMap.put("datasetAlleleResponses", innerMap);
    innerMap = new HashMap<>();
    innerMap.put("accessLevelSummary", "PUBLIC");
    innerMap.put("errorCode", "PUBLIC");
    innerMap.put("errorMessage", "PUBLIC");
    expectedMap.put("beaconError", innerMap);
    innerMap = new HashMap<>();
    innerMap.put("accessLevelSummary", "PUBLIC");
    innerMap.put("consentCodeDataUse", "PUBLIC");
    innerMap.put("adamDataUse", "PUBLIC");
    expectedMap.put("dataUseConditions", innerMap);

    return expectedMap;
  }

  private Map<String, Object> fillExpectedDatasetsMapWithAllFields() {
    // Datasets
    HashMap<String, Object> innerMap2 = new HashMap<>();
    HashMap<String, Object> innerMap3 = new HashMap<>();
    HashMap<String, String> innerMap = new HashMap<>();
    innerMap.put("new_sensitive_field", "PUBLIC");
    innerMap3.put("datasetAlleleResponses", innerMap);
    innerMap = new HashMap<>();
    innerMap.put("new_sensitive_field", "REGISTERED");
    innerMap.put("new_field", "NOT_SUPPORTED");
    innerMap3.put("beaconDataset", innerMap);
    innerMap2.put("EGAD00000000001", innerMap3);

    innerMap3 = new HashMap<>();
    innerMap = new HashMap<>();
    innerMap.put("new_sensitive_field", "CONTROLLED");
    innerMap3.put("datasetAlleleResponses", innerMap);
    innerMap = new HashMap<>();
    innerMap.put("new_sensitive_field", "REGISTERED");
    innerMap.put("new_field", "NOT_SUPPORTED");
    innerMap3.put("beaconDataset", innerMap);
    innerMap2.put("EGAD00000000002", innerMap3);

    return innerMap2;
  }

  /**
   * No info about datasets is included because we don't want detailed info by dataset and we are filtering fields:
   * fields="error", "datasetAlleleResponses", "id"
   * includeFieldDetails=true
   * includeDatasetDetails=false
   */
  @Test
  public void getSomeAccessLevelsWithoutInfoAboutDatasets() throws JsonProcessingException {
    Map<String, Object> expectedMap = fillExpectedMapWithSomeFields();

    List<String> fields = Arrays.asList("error", "datasetAlleleResponses", "id");
    AccessLevelResponse accessLevels = beaconAccessLevelService
        .listAccessLevels(fields, null, null, true, false);

    System.out.println("*************");
    System.out.println(JsonUtils.objectToJson(accessLevels, objectMapper));
    System.out.println("*************");
    System.out.println("*************");
    System.out.println(JsonUtils.objectToJson(expectedMap, objectMapper));

    assertThat(accessLevels.getError(), nullValue());
    assertThat(accessLevels.getFields().size(), equalTo(5));
    assertThat(accessLevels.getFields(), hasAllEntries(expectedMap));
    assertThat(expectedMap, hasAllEntries(accessLevels.getFields()));
    // No datasets
    assertThat(accessLevels.getDatasets().size(), equalTo(0));
  }

  private Map<String, Object> fillExpectedMapWithSomeFields() {
    Map<String, Object> expectedMap = new HashMap<>();
    HashMap<String, String> innerMap = new HashMap<>();
    innerMap.put("id", "PUBLIC");
    expectedMap.put("beacon", innerMap);
    innerMap = new HashMap<>();
    innerMap.put("id", "PUBLIC");
    expectedMap.put("beaconOrganization", innerMap);
    innerMap = new HashMap<>();
    innerMap.put("error", "PUBLIC");
    innerMap.put("datasetAlleleResponses", "PUBLIC");
    expectedMap.put("beaconAlleleResponse", innerMap);
    innerMap = new HashMap<>();
    innerMap.put("id", "PUBLIC");
    expectedMap.put("beaconDataset", innerMap);
    innerMap = new HashMap<>();
    innerMap.put("accessLevelSummary", "REGISTERED");
    innerMap.put("datasetId", "PUBLIC");
    innerMap.put("exists", "PUBLIC");
    innerMap.put("error", "PUBLIC");
    innerMap.put("frequency", "PUBLIC");
    innerMap.put("variantCount", "PUBLIC");
    innerMap.put("callCount", "PUBLIC");
    innerMap.put("sampleCount", "PUBLIC");
    innerMap.put("note", "PUBLIC");
    innerMap.put("externalUrl", "PUBLIC");
    innerMap.put("info", "PUBLIC");
    innerMap.put("new_sensitive_field", "REGISTERED");
    innerMap.put("new_non_sensitive_data", "PUBLIC");
    expectedMap.put("datasetAlleleResponses", innerMap);

    return expectedMap;
  }

  @Test
  public void getAccessLevelsReturnError() {
    AccessLevelResponse accessLevels = beaconAccessLevelService
        .listAccessLevels(Arrays.asList("this field does not exist"), null, null, false,
            false);

    assertThat(accessLevels.getError(), notNullValue());
    assertThat(accessLevels.getError().getErrorCode(), equalTo(ErrorCode.NOT_FOUND));
    assertThat(accessLevels.getFields().size(), equalTo(0));
  }

  /**
   * Get fields and datasets top access level.
   */
  @Test
  public void getDatasetsTopAccessLevels() {
    Map<String, String> expectedMap = fillExpectedMapForDatasets();
    Map<String, String> expectedDatasetsMap = fillExpectedDatasetsMapWithTopFields();

    AccessLevelResponse accessLevels = beaconAccessLevelService
        .listAccessLevels(null, Arrays.asList("EGAD00000000001","EGAD00000000002"), null,
        false, false);

    assertThat(accessLevels.getError(), nullValue());
    assertThat(accessLevels.getFields().size(), equalTo(8));
    assertThat(accessLevels.getFields(), hasAllEntries(expectedMap));

    assertThat(accessLevels.getDatasets().size(), equalTo(2));
    assertThat(accessLevels.getDatasets(), hasAllEntries(expectedDatasetsMap));
  }

  private Map<String, String> fillExpectedMapForDatasets() {
    Map<String, String> expectedMap = new HashMap<>();
    expectedMap.put("beacon", "PUBLIC");
    expectedMap.put("beaconOrganization", "PUBLIC");
    expectedMap.put("beaconDataset", "REGISTERED");
    expectedMap.put("beaconAlleleRequest", "PUBLIC");
    expectedMap.put("datasetAlleleResponses", "REGISTERED");
    expectedMap.put("beaconError", "PUBLIC");
    expectedMap.put("dataUseConditions", "PUBLIC");
    return expectedMap;
  }

  private Map<String, String> fillExpectedDatasetsMapWithTopFields() {
    // Datasets
    HashMap<String, String> innerMap = new HashMap<>();
    innerMap.put("EGAD00000000001", "PUBLIC");
    innerMap.put("EGAD00000000002", "CONTROLLED");

    return innerMap;
  }

  /**
   * Filter global and dataset fields.
   */
  @Test
  public void getSomeDatasetsAccessLevels() {
    Map<String, Object> expectedMap = fillExpectedMapForDatasetsWithSomeFields();

    Map<String, Object> expectedDatasets = new HashMap<>();
    HashMap<String, Object> innerMap3 = new HashMap<>();
    HashMap<String, String> innerMap = new HashMap<>();
    innerMap.put("new_sensitive_field", "CONTROLLED");
    innerMap3.put("datasetAlleleResponses", innerMap);
    innerMap = new HashMap<>();
    innerMap.put("new_sensitive_field", "REGISTERED");
//    innerMap.put("new_field", "NOT_SUPPORTED");
    innerMap3.put("beaconDataset", innerMap);
    expectedDatasets.put("EGAD00000000002", innerMap3);

    List<String> fields = Arrays
        .asList("new_sensitive_field", "id", "info", "datasetAlleleResponses");
    List<String> datasetIds = Arrays.asList("EGAD00000000002");
    AccessLevelResponse accessLevels = beaconAccessLevelService
        .listAccessLevels(fields, datasetIds, null, true, true);

    assertThat(accessLevels.getError(), nullValue());
    assertThat(accessLevels.getFields().size(), equalTo(5));
    assertThat(accessLevels.getFields(), hasAllEntries(expectedMap));
    assertThat(expectedMap, hasAllEntries(accessLevels.getFields()));

    assertThat(accessLevels.getDatasets(), hasAllEntries(expectedDatasets));
    assertThat(expectedDatasets, hasAllEntries(accessLevels.getDatasets()));
  }

  private Map<String, Object> fillExpectedMapForDatasetsWithSomeFields() {
    Map<String, Object> expectedMap = new HashMap<>();
    HashMap<String, String> innerMap = new HashMap<>();
    innerMap.put("id", "PUBLIC");
    innerMap.put("info", "PUBLIC");
    expectedMap.put("beacon", innerMap);
    innerMap = new HashMap<>();
    innerMap.put("id", "PUBLIC");
    innerMap.put("info", "PUBLIC");
    expectedMap.put("beaconOrganization", innerMap);
    innerMap = new HashMap<>();
    innerMap.put("id", "PUBLIC");
    innerMap.put("info", "PUBLIC");
    innerMap.put("new_sensitive_field", "REGISTERED");
    expectedMap.put("beaconDataset", innerMap);
    innerMap = new HashMap<>();
    innerMap.put("info", "PUBLIC");
    innerMap.put("datasetAlleleResponses", "PUBLIC");
    expectedMap.put("beaconAlleleResponse", innerMap);
    innerMap = new HashMap<>();
    innerMap.put("accessLevelSummary", "REGISTERED");
    innerMap.put("datasetId", "PUBLIC");
    innerMap.put("exists", "PUBLIC");
    innerMap.put("error", "PUBLIC");
    innerMap.put("frequency", "PUBLIC");
    innerMap.put("variantCount", "PUBLIC");
    innerMap.put("callCount", "PUBLIC");
    innerMap.put("sampleCount", "PUBLIC");
    innerMap.put("note", "PUBLIC");
    innerMap.put("externalUrl", "PUBLIC");
    innerMap.put("info", "PUBLIC");
    innerMap.put("new_sensitive_field", "REGISTERED");
    innerMap.put("new_non_sensitive_data", "PUBLIC");
    expectedMap.put("datasetAlleleResponses", innerMap);
    return expectedMap;
  }

}
