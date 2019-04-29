package org.ega_archive.elixirbeacon.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javassist.NotFoundException;
import javax.annotation.Resource;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.ega_archive.elixirbeacon.Application;
import org.ega_archive.elixirbeacon.convert.MapConverter;
import org.ega_archive.elixirbeacon.dto.Beacon;
import org.ega_archive.elixirbeacon.dto.BeaconAlleleResponse;
import org.ega_archive.elixirbeacon.dto.BeaconGenomicRegionRequest;
import org.ega_archive.elixirbeacon.dto.BeaconGenomicRegionResponse;
import org.ega_archive.elixirbeacon.dto.BeaconGenomicSnpRequest;
import org.ega_archive.elixirbeacon.dto.BeaconGenomicSnpResponse;
import org.ega_archive.elixirbeacon.dto.DatasetAlleleResponse;
import org.ega_archive.elixirbeacon.dto.Handover;
import org.ega_archive.elixirbeacon.dto.HandoverType;
import org.ega_archive.elixirbeacon.dto.Variant;
import org.ega_archive.elixirbeacon.enums.FilterDatasetResponse;
import org.ega_archive.elixircore.helper.CommonQuery;
import org.ega_archive.elixircore.test.util.TestUtils;
import org.ega_archive.elixircore.util.JsonUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@WebAppConfiguration
@SpringBootTest("server.port:0")
public class ConvertToMapTest {

  @Autowired
  @Qualifier("elixirBeaconServiceDefaultImpl")
  private ElixirBeaconService beaconService;

  @Autowired
  private MapConverter mapConverter;

  @Resource(name = "elixirbeaconDataSource")
  private DataSource dataSource;

  @Autowired
  private ObjectMapper objectMapper;

//  @Autowired
//  private BeaconAccessLevelService accessLevelService;

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
        "/db/dataset_access_level_table.sql",
        // Consent codes
        "/db/consent_code_category_table.sql",
        "/db/consent_code_table.sql",
//        // Beacon->CC
        "/db/beacon_dataset_consent_code_table.sql"
    );
  }

//  @Test
//  public void dataUseConditionsToMap() throws IOException {
//
//    AccessLevelResponse accessLevelResponse = accessLevelService
//        .listAccessLevels(null, null, null, true, true, "access_levels_all_public.yaml");
//
//    DataUseCondition dataUseCondition = initDataUseCondition();
//    System.out.println(JsonUtils
//        .objectToJson(dataUseCondition.toMap(accessLevelResponse.getFields(), false),
//            objectMapper));
//  }
//
//  private DataUseCondition initDataUseCondition() {
//    DataUseCondition dataUseCondition = new DataUseCondition();
//    ConsentCode cc = new ConsentCode();
//    ConsentCodeCondition ccPrim = new ConsentCodeCondition();
//    ccPrim.setCode("ccPrim-1");
//    ccPrim.setDescription("ccPrim-123");
//    ccPrim.setAdditionalConstraint("ccPrim-1234");
//    cc.setPrimaryCategory(ccPrim);
//    List<ConsentCodeCondition> secondaryCategory = new ArrayList<>();
//    ConsentCodeCondition ccSec = new ConsentCodeCondition();
//    ccSec.setCode("ccSec-1");
//    ccSec.setDescription("ccSec-123");
//    ccSec.setAdditionalConstraint("ccSec-1234");
//    secondaryCategory.add(ccSec);
//    cc.setSecondaryCategories(secondaryCategory);
//    List<ConsentCodeCondition> requirements = new ArrayList<>();
//    ConsentCodeCondition ccReq = new ConsentCodeCondition();
//    ccReq.setCode("ccReq-1");
//    ccReq.setDescription("ccReq-123");
//    ccReq.setAdditionalConstraint("ccReq-1234");
//    requirements.add(ccReq);
//    cc.setRequirements(requirements);
//    dataUseCondition.setConsentCodeDataUse(cc);
//    return dataUseCondition;
//  }
//
//  @Test
//  public void datasetToMap() throws IOException {
//    AccessLevelResponse accessLevelResponse = accessLevelService
//        .listAccessLevels(null, null, null, true, true, "access_levels_all_public.yaml");
//
//    Dataset dataset = initDataset();
//
//    System.out.println(JsonUtils
//        .objectToJson(dataset.toMap(accessLevelResponse.getFields(), false), objectMapper));
//  }
//
//  private Dataset initDataset() {
//    Dataset dataset = new Dataset();
//    dataset.setId("id-value");
//    dataset.setName("name-value");
//    dataset.setDescription("description-value");
//    dataset.setAssemblyId("assembly-value");
//    dataset.setCallCount(1L);
//    dataset.setVariantCount(2L);
//    dataset.setSampleCount(3L);
//    Map<String, Object> info = new HashMap<>();
//    info.put("key1", "value1");
//    dataset.setInfo(info);
//    dataset.setDataUseConditions(initDataUseCondition());
//    dataset.setCreateDateTime(DateTime.now());
//    dataset.setUpdateDateTime(DateTime.now());
//    dataset.setExternalUrl("external-url-value");
//    dataset.setVersion("v1");
//    return dataset;
//  }
//
//  @Test
//  public void beaconToMap() throws IOException {
//    AccessLevelResponse accessLevelResponse = accessLevelService
//        .listAccessLevels(null, null, null, true, true, "access_levels_all_public.yaml");
//
//    Beacon beacon = new Beacon();
//    beacon.setId("id-value");
//    beacon.setApiVersion("v1.0.1");
//    beacon.setCreateDateTime(DateTime.now());
//    beacon.setUpdateDateTime(DateTime.now());
//    beacon.setAlternativeUrl("alternative-url-value");
//    beacon.setDescription("description-value");
//    beacon.setName("name-value");
//    beacon.setOrganization(new Organization());
//    beacon.setVersion("v1");
//    beacon.setWelcomeUrl("welcome-url-value");
//    beacon.setSampleAlleleRequests(null);
//    beacon.setDatasets(Arrays.asList(initDataset()));
//
//    System.out.println(JsonUtils
//        .objectToJson(beacon.toMap(accessLevelResponse, false), objectMapper));
//  }

  @Test
  public void beaconToMapWhenAnonymousAndAllPublic() throws NotFoundException, IOException {
    // All fields are public
    String expectedJson = readFile("beacon_response_with_all_fields");
    expectedJson = expectedJson.trim();

    Beacon beaconInfo = (Beacon) beaconService.listDatasets(new CommonQuery(), null);

    Map<String, Object> map = mapConverter.convertToMap(beaconInfo, false, "access_levels_all_public.yaml");
    System.out.println("*************");
    System.out.println(JsonUtils.objectToJson(map, objectMapper));

    assertThat(JsonUtils.objectToJson(map, objectMapper), equalTo(expectedJson));
  }

  @Test
  public void beaconToMapWhenAnonymousAndSomeFieldsRegistered() throws NotFoundException, IOException {
    // contactUrl, variantCount, variantType, filters are marked as REGISTERED
    String expectedJson = readFile("beacon_response_without_registered_fields");
    expectedJson = expectedJson.trim();

    Beacon beaconInfo = (Beacon) beaconService.listDatasets(new CommonQuery(), null);

    Map<String, Object> map = mapConverter.convertToMap(beaconInfo, false, "access_levels_some_registered.yaml");
    System.out.println("*************");
    System.out.println(JsonUtils.objectToJson(map, objectMapper));

    assertThat(JsonUtils.objectToJson(map, objectMapper), equalTo(expectedJson));
  }

  @Test
  public void beaconToMapWhenAnonymousAndSomeFieldsControlled() throws NotFoundException, IOException {
    // contactUrl, sampleCount, variantType, secondaryCategories are controlled
    String expectedJson = readFile("beacon_response_without_controlled_fields");
    expectedJson = expectedJson.trim();

    Beacon beaconInfo = (Beacon) beaconService.listDatasets(new CommonQuery(), null);

    Map<String, Object> map = mapConverter.convertToMap(beaconInfo, false, "access_levels_some_controlled.yaml");
    System.out.println("*************");
    System.out.println(JsonUtils.objectToJson(map, objectMapper));

    assertThat(JsonUtils.objectToJson(map, objectMapper), equalTo(expectedJson));
  }

  @Test
  public void beaconToMapWhenAnonymousAndSomeFieldsNotSupported() throws NotFoundException, IOException {
    // Many fields are not supported: dataUseConditions, sampleAlleleRequests, variantCount, callCount, sampleCount
    String expectedJson = readFile("beacon_response_without_not_supported_fields");
    expectedJson = expectedJson.trim();

    Beacon beaconInfo = (Beacon) beaconService.listDatasets(new CommonQuery(), null);

    Map<String, Object> map = mapConverter.convertToMap(beaconInfo, false, "access_levels_some_not_supported.yaml");
    System.out.println("*************");
    System.out.println(JsonUtils.objectToJson(map, objectMapper));

    assertThat(expectedJson, equalTo(JsonUtils.objectToJson(map, objectMapper)));
  }

  @Test
  public void queryResponseToMapWhenAnonymous() throws IOException {
    String expectedJson = readFile("dataset_response_with_all_fields");
    expectedJson = expectedJson.trim();

    BeaconAlleleResponse response = (BeaconAlleleResponse) beaconService
        .queryBeacon(null, null, "A", "C", "21", 11, null, null, null, null, null, "GRCh37", "all",
            null);

    Map<String, Object> map = mapConverter.convertToMap(response, false, "access_levels_all_public.yaml");
    System.out.println("*************");
    System.out.println(JsonUtils.objectToJson(map, objectMapper));

    assertThat(expectedJson, equalTo(JsonUtils.objectToJson(map, objectMapper)));
  }

  @Test
  public void queryResponseToMapWhenAnonymousAndSomeFieldsNotSupported() throws IOException {
    String expectedJson = readFile("dataset_response_without_not_supported_fields");
    expectedJson = expectedJson.trim();

    BeaconAlleleResponse response = (BeaconAlleleResponse) beaconService
        .queryBeacon(null, null, "A", "C", "21", 11, null, null, null, null, null, "GRCh37", "all",
            null);

    Map<String, Object> map = mapConverter.convertToMap(response, false, "access_levels_some_not_supported.yaml");
    System.out.println("*************");
    System.out.println(JsonUtils.objectToJson(map, objectMapper));

    assertThat(expectedJson, equalTo(JsonUtils.objectToJson(map, objectMapper)));
  }

  @Test
  public void queryResponseToMapWhenAnonymousAndSomeFieldsRegistered() throws IOException {
    // variantType, filters, frequency & variantCount are registered
    String expectedJson = readFile("dataset_response_without_registered_fields");
    expectedJson = expectedJson.trim();

    BeaconAlleleResponse response = (BeaconAlleleResponse) beaconService
        .queryBeacon(null, null, "A", "C", "21", 11, null, null, null, null, null, "GRCh37", "all",
            null);

    Map<String, Object> map = mapConverter.convertToMap(response, false, "access_levels_some_registered.yaml");
    System.out.println("*************");
    System.out.println(JsonUtils.objectToJson(map, objectMapper));

    assertThat(expectedJson, equalTo(JsonUtils.objectToJson(map, objectMapper)));
  }

  @Test
  public void queryResponseToMapWhenAnonymousAndSomeFieldsControlled() throws IOException {
    // variantType, callCount & externalUrl are controlled
    String expectedJson = readFile("dataset_response_without_controlled_fields");
    expectedJson = expectedJson.trim();

    BeaconAlleleResponse response = (BeaconAlleleResponse) beaconService
        .queryBeacon(null, null, "A", "C", "21", 11, null, null, null, null, null, "GRCh37", "all",
            null);

    Map<String, Object> map = mapConverter.convertToMap(response, false, "access_levels_some_controlled.yaml");
    System.out.println("*************");
    System.out.println(JsonUtils.objectToJson(map, objectMapper));

    assertThat(expectedJson, equalTo(JsonUtils.objectToJson(map, objectMapper)));
  }

  private static String readFile(String fileNameWithoutExtension) throws IOException {
    String content = null;
    try (InputStream is =
        ConvertToMapTest.class.getResourceAsStream("/map_files/" + fileNameWithoutExtension + ".json")) {
      content = IOUtils.toString(is);
    }
    return content;
  }

  /************************************************************************
   *                              Genomic SNP                             *
   ************************************************************************/

  @Test
  public void queryGenomicSnpResponseToMapWhenAnonymous() throws IOException {
    String expectedJson = readFile("genomic_snp_response_with_all_fields");
    expectedJson = expectedJson.trim();

    BeaconGenomicSnpResponse response = initGenomicSnpResponse();

    Map<String, Object> map = mapConverter.convertToMap(response, false, "access_levels_all_public.yaml");
    System.out.println("*************");
    System.out.println(JsonUtils.objectToJson(map, objectMapper));

    assertThat(expectedJson, equalTo(JsonUtils.objectToJson(map, objectMapper)));
  }

  @Test
  public void queryGenomicSnpResponseToMapWhenAnonymousAndSomeFieldsRegistered() throws IOException {
    // frequency, variantCount & datasetHandover are registered
    String expectedJson = readFile("genomic_snp_response_without_registered_fields");
    expectedJson = expectedJson.trim();

    BeaconGenomicSnpResponse response = initGenomicSnpResponse();

    Map<String, Object> map = mapConverter.convertToMap(response, false, "access_levels_some_registered.yaml");
    System.out.println("*************");
    System.out.println(JsonUtils.objectToJson(map, objectMapper));

    assertThat(expectedJson, equalTo(JsonUtils.objectToJson(map, objectMapper)));
  }

  @Test
  public void queryGenomicSnpResponseToMapWhenAnonymousAndSomeFieldsControlled() throws IOException {
    // callCount, externalUrl, datasetHandover are controlled
    String expectedJson = readFile("genomic_snp_response_without_controlled_fields");
    expectedJson = expectedJson.trim();

    BeaconGenomicSnpResponse response = initGenomicSnpResponse();

    Map<String, Object> map = mapConverter.convertToMap(response, false, "access_levels_some_controlled.yaml");
    System.out.println("*************");
    System.out.println(JsonUtils.objectToJson(map, objectMapper));

    assertThat(expectedJson, equalTo(JsonUtils.objectToJson(map, objectMapper)));
  }

  @Test
  public void queryGenomicSnpResponseToMapWhenAnonymousAndSomeFieldsNotSupported() throws IOException {
    // frequency, datasetHandover are not supported
    String expectedJson = readFile("genomic_snp_response_without_not_supported_fields");
    expectedJson = expectedJson.trim();

    BeaconGenomicSnpResponse response = initGenomicSnpResponse();

    Map<String, Object> map = mapConverter.convertToMap(response, false, "access_levels_some_not_supported.yaml");
    System.out.println("*************");
    System.out.println(JsonUtils.objectToJson(map, objectMapper));

    assertThat(expectedJson, equalTo(JsonUtils.objectToJson(map, objectMapper)));
  }

  private BeaconGenomicSnpResponse initGenomicSnpResponse() {
    BeaconGenomicSnpResponse response = new BeaconGenomicSnpResponse();
    response.setExists(true);
    response.setApiVersion("v1.0.1");
    response.setBeaconId("myBeacon");
    response.setError(null);

    Handover handover1 = new Handover();
    HandoverType handoverType1 = new HandoverType();
    handoverType1.setId("CUSTOM");
    handoverType1.setLabel("myUrl");
    handover1.setHandoverType(handoverType1);
    handover1.setNote("");
    handover1.setUrl("https://localhost");

    HandoverType handoverType2 = new HandoverType();
    handoverType2.setId("CUSTOM");
    handoverType2.setLabel("myUrl2");
    Handover handover2 = new Handover();
    handover2.setHandoverType(handoverType2);
    handover2.setNote("");
    handover2.setUrl("https://localhost");
    response.setBeaconHandover(Arrays.asList(handover1, handover2));

    HashMap<String, Object> info = new HashMap<>();
    info.put("myKey1", "myValue1");
    info.put("myKey2", "myValue2");
    response.setInfo(info);

    BeaconGenomicSnpRequest request = new BeaconGenomicSnpRequest();
    request.setAssemblyId("GRCh37");
    request.setAlternateBases("A");
    request.setReferenceBases("C");
    request.setDatasetIds(null);
    request.setFilters(Arrays.asList("myFilter1", "myFilter2"));
    request.setIncludeDatasetResponses(FilterDatasetResponse.ALL);
    request.setReferenceName("Y");
    request.setStart(1111);
    response.setRequest(request);

    DatasetAlleleResponse datasetResponse1 = new DatasetAlleleResponse();
    datasetResponse1.setDatasetId("1");
    datasetResponse1.setError(null);
    datasetResponse1.setExists(true);
    datasetResponse1.setCallCount(111L);
    datasetResponse1.setSampleCount(2L);
    datasetResponse1.setVariantCount(11L);
    datasetResponse1.setFrequency(new BigDecimal("0.5"));
    datasetResponse1.setExternalUrl("https://some_url2");
    datasetResponse1.setNote("note 1");

    HandoverType handoverType3 = new HandoverType();
    handoverType3.setId("CUSTOM");
    handoverType3.setLabel("myUrl - dataset");
    Handover handover3 = new Handover();
    handover3.setHandoverType(handoverType2);
    handover3.setNote("dataset handover 1");
    handover3.setUrl("https://localhost/dataset");
    datasetResponse1.setDatasetHandover(Arrays.asList(handover3));

    Map<String, Object> info2 = new LinkedHashMap<>();
    info2.put("myDataset1InfoKey1", "myValue1");
    info2.put("myDataset1InfoKey2", "myValue2");
    datasetResponse1.setInfo(info2);

    DatasetAlleleResponse datasetResponse2 = new DatasetAlleleResponse();
    datasetResponse2.setDatasetId("1");
    datasetResponse2.setError(null);
    datasetResponse2.setExists(true);
    datasetResponse2.setCallCount(222L);
    datasetResponse2.setSampleCount(3L);
    datasetResponse2.setVariantCount(22L);
    datasetResponse2.setFrequency(new BigDecimal("0.1"));
    datasetResponse2.setExternalUrl("https://some_url2");
    datasetResponse2.setNote("note 2");

    HandoverType handoverType4 = new HandoverType();
    handoverType4.setId("CUSTOM");
    handoverType4.setLabel("myUrl - dataset 2");
    Handover handover4 = new Handover();
    handover4.setHandoverType(handoverType2);
    handover4.setNote("dataset handover 2");
    handover4.setUrl("https://localhost/dataset");

    HandoverType handoverType5 = new HandoverType();
    handoverType5.setId("CUSTOM");
    handoverType5.setLabel("myUrl - dataset 3");
    Handover handover5 = new Handover();
    handover5.setHandoverType(handoverType2);
    handover5.setNote("dataset handover 3");
    handover5.setUrl("https://localhost/dataset");
    datasetResponse2.setDatasetHandover(Arrays.asList(handover4, handover5));

    Map<String, Object> info3 = new LinkedHashMap<>();
    info3.put("myDataset2InfoKey1", "myValue1");
    datasetResponse2.setInfo(info3);
    response.setDatasetAlleleResponses(Arrays.asList(datasetResponse1, datasetResponse2));
    return response;
  }

  /************************************************************************
   *                           Genomic Region                             *
   ************************************************************************/

  @Test
  public void queryGenomicRegionResponseToMapWhenAnonymous() throws IOException {
    String expectedJson = readFile("genomic_region_response_with_all_fields");
    expectedJson = expectedJson.trim();

    BeaconGenomicRegionResponse response = initGenomicRegionResponse();

    Map<String, Object> map = mapConverter.convertToMap(response, false, "access_levels_all_public.yaml");
    System.out.println("*************");
    System.out.println(JsonUtils.objectToJson(map, objectMapper));

    assertThat(expectedJson, equalTo(JsonUtils.objectToJson(map, objectMapper)));
  }

  @Test
  public void queryGenomicRegionResponseToMapWhenAnonymousAndSomeFieldsRegistered() throws IOException {
    // frequency, variantCount, datasetHandover, cellBaseInfo are registered
    String expectedJson = readFile("genomic_region_response_without_registered_fields");
    expectedJson = expectedJson.trim();

    BeaconGenomicRegionResponse response = initGenomicRegionResponse();

    Map<String, Object> map = mapConverter.convertToMap(response, false, "access_levels_some_registered.yaml");
    System.out.println("*************");
    System.out.println(JsonUtils.objectToJson(map, objectMapper));

    assertThat(expectedJson, equalTo(JsonUtils.objectToJson(map, objectMapper)));
  }

  @Test
  public void queryGenomicRegionResponseToMapWhenAnonymousAndSomeFieldsControlled() throws IOException {
    // callCount, externalUrl, datasetHandover, cellBase are controlled
    String expectedJson = readFile("genomic_region_response_without_controlled_fields");
    expectedJson = expectedJson.trim();

    BeaconGenomicRegionResponse response = initGenomicRegionResponse();

    Map<String, Object> map = mapConverter.convertToMap(response, false, "access_levels_some_controlled.yaml");
    System.out.println("*************");
    System.out.println(JsonUtils.objectToJson(map, objectMapper));

    assertThat(expectedJson, equalTo(JsonUtils.objectToJson(map, objectMapper)));
  }

  @Test
  public void queryGenomicRegionResponseToMapWhenAnonymousAndSomeFieldsNotSupported() throws IOException {
    // frequency, datasetHandover, datasetAlleleResponses, variantHandover are not supported
    String expectedJson = readFile("genomic_region_response_without_not_supported_fields");
    expectedJson = expectedJson.trim();

    BeaconGenomicRegionResponse response = initGenomicRegionResponse();

    Map<String, Object> map = mapConverter.convertToMap(response, false, "access_levels_some_not_supported.yaml");
    System.out.println("*************");
    System.out.println(JsonUtils.objectToJson(map, objectMapper));

    assertThat(expectedJson, equalTo(JsonUtils.objectToJson(map, objectMapper)));
  }

  private BeaconGenomicRegionResponse initGenomicRegionResponse() {
    BeaconGenomicRegionResponse response = new BeaconGenomicRegionResponse();
    response.setExists(true);
    response.setApiVersion("v1.0.1");
    response.setBeaconId("myBeacon");
    response.setError(null);

    Handover handover1 = new Handover();
    HandoverType handoverType1 = new HandoverType();
    handoverType1.setId("CUSTOM");
    handoverType1.setLabel("myUrl");
    handover1.setHandoverType(handoverType1);
    handover1.setNote("");
    handover1.setUrl("https://localhost");

    HandoverType handoverType2 = new HandoverType();
    handoverType2.setId("CUSTOM");
    handoverType2.setLabel("myUrl2");
    Handover handover2 = new Handover();
    handover2.setHandoverType(handoverType2);
    handover2.setNote("");
    handover2.setUrl("https://localhost");
    response.setBeaconHandover(Arrays.asList(handover1, handover2));

    HashMap<String, Object> info = new HashMap<>();
    info.put("myKey1", "myValue1");
    info.put("myKey2", "myValue2");
    response.setInfo(info);

    BeaconGenomicRegionRequest request = new BeaconGenomicRegionRequest();
    request.setAssemblyId("GRCh37");
//    request.setAlternateBases("A");
    request.setReferenceBases("C");
    request.setDatasetIds(null);
    request.setFilters(Arrays.asList("myFilter1", "myFilter2"));
    request.setIncludeDatasetResponses(FilterDatasetResponse.ALL);
    request.setReferenceName("Y");
    request.setStart(1111);
    request.setEnd(2222);
    response.setRequest(request);

    DatasetAlleleResponse datasetResponse1 = new DatasetAlleleResponse();
    datasetResponse1.setDatasetId("1");
    datasetResponse1.setError(null);
    datasetResponse1.setExists(true);
    datasetResponse1.setCallCount(111L);
    datasetResponse1.setSampleCount(2L);
    datasetResponse1.setVariantCount(11L);
    datasetResponse1.setFrequency(new BigDecimal("0.5"));
    datasetResponse1.setExternalUrl("https://some_url2");
    datasetResponse1.setNote("note 1");

    HandoverType handoverType3 = new HandoverType();
    handoverType3.setId("CUSTOM");
    handoverType3.setLabel("myUrl - dataset");
    Handover handover3 = new Handover();
    handover3.setHandoverType(handoverType2);
    handover3.setNote("dataset handover 1");
    handover3.setUrl("https://localhost/dataset");
    datasetResponse1.setDatasetHandover(Arrays.asList(handover3));

    Map<String, Object> info2 = new LinkedHashMap<>();
    info2.put("myDataset1InfoKey1", "myValue1");
    info2.put("myDataset1InfoKey2", "myValue2");
    datasetResponse1.setInfo(info2);

    DatasetAlleleResponse datasetResponse2 = new DatasetAlleleResponse();
    datasetResponse2.setDatasetId("1");
    datasetResponse2.setError(null);
    datasetResponse2.setExists(true);
    datasetResponse2.setCallCount(222L);
    datasetResponse2.setSampleCount(3L);
    datasetResponse2.setVariantCount(22L);
    datasetResponse2.setFrequency(new BigDecimal("0.1"));
    datasetResponse2.setExternalUrl("https://some_url2");
    datasetResponse2.setNote("note 2");

    HandoverType handoverType4 = new HandoverType();
    handoverType4.setId("CUSTOM");
    handoverType4.setLabel("myUrl - dataset 2");
    Handover handover4 = new Handover();
    handover4.setHandoverType(handoverType2);
    handover4.setNote("dataset handover 2");
    handover4.setUrl("https://localhost/dataset");

    HandoverType handoverType5 = new HandoverType();
    handoverType5.setId("CUSTOM");
    handoverType5.setLabel("myUrl - dataset 3");
    Handover handover5 = new Handover();
    handover5.setHandoverType(handoverType2);
    handover5.setNote("dataset handover 3");
    handover5.setUrl("https://localhost/dataset");
    datasetResponse2.setDatasetHandover(Arrays.asList(handover4, handover5));

    Map<String, Object> info3 = new LinkedHashMap<>();
    info3.put("myDataset2InfoKey1", "myValue1");
    datasetResponse2.setInfo(info3);

    Map<String, Object> cellBaseInfo = new HashMap<>();
    cellBaseInfo.put("cellBase", "some info");

    Variant variant1 = new Variant();
    //variant1.setCellBaseInfo(cellBaseInfo);
    variant1.setDatasetAlleleResponses(Arrays.asList(datasetResponse1));
    HandoverType handoverType6 = new HandoverType();
    handoverType6.setId("CUSTOM");
    handoverType6.setLabel("myUrl - dataset 3");
    Handover handover6 = new Handover();
    handover6.setHandoverType(handoverType2);
    handover6.setNote("dataset handover 3");
    handover6.setUrl("https://localhost/dataset");
    variant1.setVariantHandover(Arrays.asList(handover6));

    Variant variant2 = new Variant();
    //variant2.setCellBaseInfo(cellBaseInfo);
    variant2.setDatasetAlleleResponses(Arrays.asList(datasetResponse2));
    HandoverType handoverType7 = new HandoverType();
    handoverType7.setId("CUSTOM");
    handoverType7.setLabel("myUrl - dataset 3");
    Handover handover7 = new Handover();
    handover7.setHandoverType(handoverType2);
    handover7.setNote("dataset handover 3");
    handover7.setUrl("https://localhost/dataset");
    variant2.setVariantHandover(Arrays.asList(handover7));

    response.setVariantsFound(Arrays.asList(variant1, variant2));
    return response;
  }

}
