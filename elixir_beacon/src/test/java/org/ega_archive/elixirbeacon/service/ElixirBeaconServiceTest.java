package org.ega_archive.elixirbeacon.service;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.integration.test.matcher.MapContentMatchers.hasAllEntries;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.ega_archive.elixirbeacon.Application;
import org.ega_archive.elixirbeacon.constant.BeaconConstants;
import org.ega_archive.elixirbeacon.dto.AccessLevelResponse;
import org.ega_archive.elixirbeacon.dto.Beacon;
import org.ega_archive.elixirbeacon.dto.BeaconAlleleResponse;
import org.ega_archive.elixirbeacon.enums.ErrorCode;
import org.ega_archive.elixirbeacon.enums.FilterDatasetResponse;
import org.ega_archive.elixirbeacon.enums.VariantType;
import org.ega_archive.elixircore.helper.CommonQuery;
import org.ega_archive.elixircore.test.util.TestUtils;
import org.junit.After;
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
public class ElixirBeaconServiceTest {

  private static final CommonQuery COMMON_QUERY = CommonQuery.builder().skip(0).limit(0).build();

  @Autowired
  private ElixirBeaconService elixirBeaconService;

  @Resource(name = "elixirbeaconDataSource")
  private DataSource dataSource;

  @Before
  public void setUp() throws SQLException {
    TestUtils.removeUserFromContext();

    // Truncate + Insert
    TestUtils.populateDatabase(dataSource,
        "/db/truncate_tables.sql", 
        // Beacon
        "/db/beacon_dataset_table.sql",
        "/db/beacon_sample_table.sql",
        "/db/beacon_dataset_sample_table.sql",
        "/db/beacon_data_table.sql",
        "/db/beacon_data_sample_table.sql",
        // CC
        "/db/consent_code_category_table.sql",
        "/db/consent_code_table.sql",
        // Beacon->CC
        "/db/beacon_dataset_consent_code_table.sql",
        // Access level
        "/db/dataset_access_level_table.sql"
    )
    ;
  }

  @After
  public void tearDown() throws SQLException {
//    TestUtils.populateDatabase(dataSource, "/db/truncate_tables.sql");
  }

  @Test
  public void listDatasets() throws Exception {
    // Mock 3 calls to that method
    Beacon allowedDatasets = elixirBeaconService.listDatasets(COMMON_QUERY, null);

    assertThat(allowedDatasets.getDatasets().size(), equalTo(5));
    int i = 0;
    assertThat(allowedDatasets.getDatasets().get(i).getId(), equalTo("EGAD00000000001"));

    assertThat(allowedDatasets.getDatasets().get(i).getInfo().get(1).getKey(), equalTo(
        BeaconConstants.AUTHORIZED));
    assertThat(allowedDatasets.getDatasets().get(i).getInfo().get(1).getValue(), equalTo(Boolean.toString(true)));//public
    assertThat(allowedDatasets.getDatasets().get(i).getDataUseConditions().getConsentCodedataUse(), notNullValue());
    assertThat(allowedDatasets.getDatasets().get(i).getDataUseConditions().getConsentCodedataUse().getPrimaryCategory(), notNullValue());
    assertThat(allowedDatasets.getDatasets().get(i).getDataUseConditions().getConsentCodedataUse().getSecondaryCategories().size(), equalTo(1));
    assertThat(allowedDatasets.getDatasets().get(i).getDataUseConditions().getConsentCodedataUse().getRequirements().size(), equalTo(0));
    i++;
    assertThat(allowedDatasets.getDatasets().get(i).getId(), equalTo("EGAD00000000002"));
    assertThat(allowedDatasets.getDatasets().get(i).getInfo().get(1).getKey(), equalTo(
        BeaconConstants.AUTHORIZED));
    assertThat(allowedDatasets.getDatasets().get(i).getInfo().get(1).getValue(), equalTo(Boolean.toString(false)));// registered
    assertThat(allowedDatasets.getDatasets().get(i).getDataUseConditions().getConsentCodedataUse(), notNullValue());
    assertThat(allowedDatasets.getDatasets().get(i).getDataUseConditions().getConsentCodedataUse().getPrimaryCategory(), notNullValue());
    assertThat(allowedDatasets.getDatasets().get(i).getDataUseConditions().getConsentCodedataUse().getSecondaryCategories().size(), equalTo(2));
    assertThat(allowedDatasets.getDatasets().get(i).getDataUseConditions().getConsentCodedataUse().getRequirements().size(), equalTo(0));
    i++;
    assertThat(allowedDatasets.getDatasets().get(i).getId(), equalTo("EGAD00000000003"));
    assertThat(allowedDatasets.getDatasets().get(i).getInfo().get(1).getKey(), equalTo(
        BeaconConstants.AUTHORIZED));
    assertThat(allowedDatasets.getDatasets().get(i).getInfo().get(1).getValue(), equalTo(Boolean.toString(false)));// protected
    assertThat(allowedDatasets.getDatasets().get(i).getDataUseConditions().getConsentCodedataUse(), notNullValue());
    assertThat(allowedDatasets.getDatasets().get(i).getDataUseConditions().getConsentCodedataUse().getPrimaryCategory(), notNullValue());
    assertThat(allowedDatasets.getDatasets().get(i).getDataUseConditions().getConsentCodedataUse().getSecondaryCategories().size(), equalTo(0));
    assertThat(allowedDatasets.getDatasets().get(i).getDataUseConditions().getConsentCodedataUse().getRequirements().size(), equalTo(0));
    i++;
    assertThat(allowedDatasets.getDatasets().get(i).getId(), equalTo("EGAD00000000004"));
    assertThat(allowedDatasets.getDatasets().get(i).getInfo().get(1).getKey(), equalTo(
        BeaconConstants.AUTHORIZED));
    assertThat(allowedDatasets.getDatasets().get(i).getInfo().get(1).getValue(), equalTo(Boolean.toString(true)));// public
    assertThat(allowedDatasets.getDatasets().get(i).getDataUseConditions().getConsentCodedataUse(), notNullValue());
    assertThat(allowedDatasets.getDatasets().get(i).getDataUseConditions().getConsentCodedataUse().getPrimaryCategory(), notNullValue());
    assertThat(allowedDatasets.getDatasets().get(i).getDataUseConditions().getConsentCodedataUse().getSecondaryCategories().size(), equalTo(1));
    assertThat(allowedDatasets.getDatasets().get(i).getDataUseConditions().getConsentCodedataUse().getRequirements().size(), equalTo(1));
    i++;
    assertThat(allowedDatasets.getDatasets().get(i).getId(), equalTo("EGAD00000000005"));
    assertThat(allowedDatasets.getDatasets().get(i).getInfo().get(1).getKey(), equalTo(
        BeaconConstants.AUTHORIZED));
    assertThat(allowedDatasets.getDatasets().get(i).getInfo().get(1).getValue(), equalTo(Boolean.toString(true)));// public
    assertThat(allowedDatasets.getDatasets().get(i).getDataUseConditions().getConsentCodedataUse(), notNullValue());
    assertThat(allowedDatasets.getDatasets().get(i).getDataUseConditions().getConsentCodedataUse().getPrimaryCategory(), notNullValue());
    assertThat(allowedDatasets.getDatasets().get(i).getDataUseConditions().getConsentCodedataUse().getSecondaryCategories().size(), equalTo(1));
    assertThat(allowedDatasets.getDatasets().get(i).getDataUseConditions().getConsentCodedataUse().getRequirements().size(), equalTo(2));
  }

  @Test
  public void listDatasetsByReferenceGenome() throws Exception {
    Beacon allowedDatasets = elixirBeaconService.listDatasets(COMMON_QUERY, "grch37");

    assertThat(allowedDatasets.getDatasets().size(), equalTo(4));
    int i = 0;
    assertThat(allowedDatasets.getDatasets().get(i).getId(), equalTo("EGAD00000000001"));
    assertThat(allowedDatasets.getDatasets().get(i).getInfo().get(1).getKey(), equalTo(
        BeaconConstants.AUTHORIZED));
    assertThat(allowedDatasets.getDatasets().get(i).getInfo().get(1).getValue(), equalTo(Boolean.toString(true)));// public
    i++;
    assertThat(allowedDatasets.getDatasets().get(i).getId(), equalTo("EGAD00000000002"));
    assertThat(allowedDatasets.getDatasets().get(i).getInfo().get(1).getKey(), equalTo(
        BeaconConstants.AUTHORIZED));
    assertThat(allowedDatasets.getDatasets().get(i).getInfo().get(1).getValue(), equalTo(Boolean.toString(false)));// registered
    i++;
    assertThat(allowedDatasets.getDatasets().get(i).getId(), equalTo("EGAD00000000004"));
    assertThat(allowedDatasets.getDatasets().get(i).getInfo().get(1).getKey(), equalTo(
        BeaconConstants.AUTHORIZED));
    assertThat(allowedDatasets.getDatasets().get(i).getInfo().get(1).getValue(), equalTo(Boolean.toString(true)));// public
    i++;
    assertThat(allowedDatasets.getDatasets().get(i).getId(), equalTo("EGAD00000000005"));
    assertThat(allowedDatasets.getDatasets().get(i).getInfo().get(1).getKey(), equalTo(
        BeaconConstants.AUTHORIZED));
    assertThat(allowedDatasets.getDatasets().get(i).getInfo().get(1).getValue(), equalTo(Boolean.toString(true)));// public
  }
  
  @Test
  public void checkParamChromosomeMissing() {
    BeaconAlleleResponse result = new BeaconAlleleResponse();
    VariantType type = null;
    Integer start = null;
    Integer startMin = null;
    Integer startMax = null;
    Integer end = null;
    Integer endMin = null;
    Integer endMax = null;
    List<String> datasetStableIds = null;
    String referenceGenome = "grch37";
    String chromosome = null;
    String referenceBases = "C";
    String alternateBases = null;

    elixirBeaconService.checkParams(result, datasetStableIds, type, alternateBases, referenceBases,
        chromosome, start, startMin, startMax, end, endMin, endMax, referenceGenome);

    assertThat(result.getError(), notNullValue());
    assertThat(result.getError().getErrorCode(), equalTo(ErrorCode.GENERIC_ERROR));
    assertThat(result.getError().getMessage(),
        equalTo("All 'referenceName', 'referenceBases' and/or 'assemblyId' are required"));
  }

  @Test
  public void checkParamReferenceGenomeMissing() {
    BeaconAlleleResponse result = new BeaconAlleleResponse();
    VariantType type = null;
    Integer start = null;
    Integer startMin = null;
    Integer startMax = null;
    Integer end = 1111;
    Integer endMin = null;
    Integer endMax = null;
    List<String> datasetStableIds = null;
    String referenceGenome = null;
    String chromosome = "12";
    String referenceBases = "C";
    String alternateBases = null;

    elixirBeaconService.checkParams(result, datasetStableIds, type, alternateBases, referenceBases,
        chromosome, start, startMin, startMax, end, endMin, endMax, referenceGenome);

    assertThat(result.getError(), notNullValue());
    assertThat(result.getError().getErrorCode(), equalTo(ErrorCode.GENERIC_ERROR));
    assertThat(result.getError().getMessage(),
        equalTo("All 'referenceName', 'referenceBases' and/or 'assemblyId' are required"));
  }
  
  @Test
  public void checkParamReferenceBasesMissing() {
    BeaconAlleleResponse result = new BeaconAlleleResponse();
    VariantType type = null;
    Integer start = null;
    Integer startMin = null;
    Integer startMax = null;
    Integer end = 1111;
    Integer endMin = null;
    Integer endMax = null;
    List<String> datasetStableIds = null;
    String referenceGenome = "grch37";
    String chromosome = "12";
    String referenceBases = null;
    String alternateBases = null;

    elixirBeaconService.checkParams(result, datasetStableIds, type, alternateBases, referenceBases,
        chromosome, start, startMin, startMax, end, endMin, endMax, referenceGenome);

    assertThat(result.getError(), notNullValue());
    assertThat(result.getError().getErrorCode(), equalTo(ErrorCode.GENERIC_ERROR));
    assertThat(result.getError().getMessage(),
        equalTo("All 'referenceName', 'referenceBases' and/or 'assemblyId' are required"));
  }
  
  @Test
  public void checkParamAlternateBasesAndVariantTypeMissing() {
    BeaconAlleleResponse result = new BeaconAlleleResponse();
    VariantType type = null;
    Integer start = 1111;
    Integer startMin = null;
    Integer startMax = null;
    Integer end = 1111;
    Integer endMin = null;
    Integer endMax = null;
    List<String> datasetStableIds = null;
    String referenceGenome = "grch37";
    String chromosome = "12";
    String referenceBases = "C";
    String alternateBases = null;

    elixirBeaconService.checkParams(result, datasetStableIds, type, alternateBases, referenceBases,
        chromosome, start, startMin, startMax, end, endMin, endMax, referenceGenome);

    assertThat(result.getError(), notNullValue());
    assertThat(result.getError().getErrorCode(), equalTo(ErrorCode.GENERIC_ERROR));
    assertThat(result.getError().getMessage(),
        equalTo("Either 'alternateBases' or 'variantType' is required"));
  }
  
  @Test
  public void checkParamAlternateBasesAndVariantTypeProvided() {
    BeaconAlleleResponse result = new BeaconAlleleResponse();
    VariantType type = VariantType.DEL;
    Integer start = 1111;
    Integer startMin = null;
    Integer startMax = null;
    Integer end = 1111;
    Integer endMin = null;
    Integer endMax = null;
    List<String> datasetStableIds = null;
    String referenceGenome = "grch37";
    String chromosome = "12";
    String referenceBases = "C";
    String alternateBases = "A";

    elixirBeaconService.checkParams(result, datasetStableIds, type, alternateBases, referenceBases,
        chromosome, start, startMin, startMax, end, endMin, endMax, referenceGenome);

    assertThat(result.getError(), notNullValue());
    assertThat(result.getError().getErrorCode(), equalTo(ErrorCode.GENERIC_ERROR));
    assertThat(result.getError().getMessage(),
        equalTo("If 'variantType' is provided then 'alternateBases' must be empty or equal to 'N'"));
    
    result = new BeaconAlleleResponse();
    alternateBases = "N";

    elixirBeaconService.checkParams(result, datasetStableIds, type, alternateBases, referenceBases,
        chromosome, start, startMin, startMax, end, endMin, endMax, referenceGenome);

    assertThat(result.getError(), nullValue());
    
    result = new BeaconAlleleResponse();
    alternateBases = null;

    elixirBeaconService.checkParams(result, datasetStableIds, type, alternateBases, referenceBases,
        chromosome, start, startMin, startMax, end, endMin, endMax, referenceGenome);

    assertThat(result.getError(), nullValue());
  }

  @Test
  public void checkParamStartMissingWhenEndProvided() {
    BeaconAlleleResponse result = new BeaconAlleleResponse();
    VariantType type = null;
    Integer start = null;
    Integer startMin = null;
    Integer startMax = null;
    Integer end = 1111;
    Integer endMin = null;
    Integer endMax = null;
    List<String> datasetStableIds = null;
    String referenceGenome = "grch37";
    String chromosome = "12";
    String referenceBases = "N";
    String alternateBases = null;

    elixirBeaconService.checkParams(result, datasetStableIds, type, alternateBases, referenceBases,
        chromosome, start, startMin, startMax, end, endMin, endMax, referenceGenome);

    assertThat(result.getError(), notNullValue());
    assertThat(result.getError().getErrorCode(), equalTo(ErrorCode.GENERIC_ERROR));
    assertThat(result.getError().getMessage(), equalTo("'start' is required if 'end' is provided"));
  }
  
  @Test
  public void checkParamStartMissing() {
    BeaconAlleleResponse result = new BeaconAlleleResponse();
    VariantType type = null;
    Integer start = null;
    Integer startMin = null;
    Integer startMax = null;
    Integer end = null;
    Integer endMin = null;
    Integer endMax = null;
    List<String> datasetStableIds = null;
    String referenceGenome = "grch37";
    String chromosome = "12";
    String referenceBases = "C";
    String alternateBases = null;

    elixirBeaconService.checkParams(result, datasetStableIds, type, alternateBases, referenceBases,
        chromosome, start, startMin, startMax, end, endMin, endMax, referenceGenome);

    assertThat(result.getError(), notNullValue());
    assertThat(result.getError().getErrorCode(), equalTo(ErrorCode.GENERIC_ERROR));
    assertThat(result.getError().getMessage(), equalTo(
        "Either 'start' or all of 'startMin', 'startMax', 'endMin' and 'endMax' are required"));
  }

  @Test
  public void checkParamSomeStartXXOrEndXXMissing() {
    BeaconAlleleResponse result = new BeaconAlleleResponse();
    VariantType type = null;
    Integer start = null;
    Integer startMin = 1111;
    Integer startMax = 1112;
    Integer end = null;
    Integer endMin = 2222;
    Integer endMax = null;
    List<String> datasetStableIds = null;
    String referenceGenome = "grch37";
    String chromosome = "12";
    String referenceBases = "C";
    String alternateBases = null;

    elixirBeaconService.checkParams(result, datasetStableIds, type, alternateBases, referenceBases,
        chromosome, start, startMin, startMax, end, endMin, endMax, referenceGenome);

    assertThat(result.getError(), notNullValue());
    assertThat(result.getError().getErrorCode(), equalTo(ErrorCode.GENERIC_ERROR));
    assertThat(result.getError().getMessage(),
        equalTo("All of 'startMin', 'startMax', 'endMin' and 'endMax' are required"));
  }

  @Test
  public void checkParamStartProvidedAndEndMissing() {
    BeaconAlleleResponse result = new BeaconAlleleResponse();
    VariantType type = null;
    Integer start = 1111;
    Integer startMin = null;
    Integer startMax = null;
    Integer end = null;
    Integer endMin = null;
    Integer endMax = null;
    List<String> datasetStableIds = null;
    String referenceGenome = "grch37";
    String chromosome = "12";
    String referenceBases = "N";
    String alternateBases = null;
    elixirBeaconService.checkParams(result, datasetStableIds, type, alternateBases, referenceBases,
        chromosome, start, startMin, startMax, end, endMin, endMax, referenceGenome);

    assertThat(result.getError(), notNullValue());
    assertThat(result.getError().getErrorCode(), equalTo(ErrorCode.GENERIC_ERROR));
    assertThat(result.getError().getMessage(),
        equalTo("'referenceBases' cannot be 'N' if 'start' is provided and 'end' is missing"));
  }
  
  @Test
  public void checkParamStartStartMinStartMaxEndMinEndMaxProvided() {
    BeaconAlleleResponse result = new BeaconAlleleResponse();
    VariantType type = null;
    Integer start = 1111;
    Integer startMin = 1111;
    Integer startMax = null;
    Integer end = null;
    Integer endMin = null;
    Integer endMax = null;
    List<String> datasetStableIds = null;
    String referenceGenome = "grch37";
    String chromosome = "12";
    String referenceBases = "C";
    String alternateBases = null;
    elixirBeaconService.checkParams(result, datasetStableIds, type, alternateBases, referenceBases,
        chromosome, start, startMin, startMax, end, endMin, endMax, referenceGenome);

    assertThat(result.getError(), notNullValue());
    assertThat(result.getError().getErrorCode(), equalTo(ErrorCode.GENERIC_ERROR));
    assertThat(result.getError().getMessage(),
        equalTo("'start' cannot be provided at the same time as 'startMin', 'startMax', 'endMin' and 'endMax'"));
  }

  @Test
  public void checkParamDatasetFound() {
    BeaconAlleleResponse result = new BeaconAlleleResponse();
    VariantType type = null;
    Integer start = 1111;
    Integer startMin = null;
    Integer startMax = null;
    Integer end = null;
    Integer endMin = null;
    Integer endMax = null;
    List<String> datasetStableIds = Arrays.asList("this ID does not exist");
    String referenceGenome = "grch37";
    String chromosome = "12";
    String referenceBases = "A";
    String alternateBases = "AC";
    elixirBeaconService.checkParams(result, datasetStableIds, type, alternateBases, referenceBases,
        chromosome, start, startMin, startMax, end, endMin, endMax, referenceGenome);

    assertThat(result.getError(), notNullValue());
    assertThat(result.getError().getErrorCode(), equalTo(ErrorCode.NOT_FOUND));
    assertThat(result.getError().getMessage(), equalTo("Dataset not found"));
  }

  @Test
  public void checkParamDatasetUnauthenticatedUser() {
    BeaconAlleleResponse result = new BeaconAlleleResponse();
    VariantType type = null;
    Integer start = 1111;
    Integer startMin = null;
    Integer startMax = null;
    Integer end = null;
    Integer endMin = null;
    Integer endMax = null;
    List<String> datasetStableIds = Arrays.asList("EGAD00000000003");
    String referenceGenome = "grch37";
    String chromosome = "12";
    String referenceBases = "A";
    String alternateBases = "AC";

    elixirBeaconService.checkParams(result, datasetStableIds, type, alternateBases, referenceBases,
        chromosome, start, startMin, startMax, end, endMin, endMax, referenceGenome);

    assertThat(result.getError(), notNullValue());
    assertThat(result.getError().getErrorCode(), equalTo(ErrorCode.UNAUTHORIZED));
    assertThat(result.getError().getMessage(),
        equalTo("Unauthenticated users cannot access this dataset"));
  }

  @Test
  public void checkParamReferenceGenomeDoesNotMatchDataset() {
    BeaconAlleleResponse result = new BeaconAlleleResponse();
    VariantType type = null;
    Integer start = 1111;
    Integer startMin = null;
    Integer startMax = null;
    Integer end = null;
    Integer endMin = null;
    Integer endMax = null;
    List<String> datasetStableIds = Arrays.asList("EGAD00000000001");
    String referenceGenome = "grch38";
    String chromosome = "12";
    String referenceBases = "A";
    String alternateBases = "AC";

    elixirBeaconService.checkParams(result, datasetStableIds, type, alternateBases, referenceBases,
        chromosome, start, startMin, startMax, end, endMin, endMax, referenceGenome);

    assertThat(result.getError(), notNullValue());
    assertThat(result.getError().getErrorCode(), equalTo(ErrorCode.GENERIC_ERROR));
    assertThat(result.getError().getMessage(), startsWith("The assemblyId of this dataset ("));
  }

  @Test
  public void checkParamAlternateBasesNotValid() {
    BeaconAlleleResponse result = new BeaconAlleleResponse();
    VariantType type = null;
    Integer start = 1111;
    Integer startMin = null;
    Integer startMax = null;
    Integer end = null;
    Integer endMin = null;
    Integer endMax = null;
    List<String> datasetStableIds = Arrays.asList("EGAD00000000001");
    String referenceGenome = "grch37";
    String chromosome = "12";
    String referenceBases = "A";
    String alternateBases = "AR";

    elixirBeaconService.checkParams(result, datasetStableIds, type, alternateBases, referenceBases,
        chromosome, start, startMin, startMax, end, endMin, endMax, referenceGenome);

    assertThat(result.getError(), notNullValue());
    assertThat(result.getError().getErrorCode(), equalTo(ErrorCode.GENERIC_ERROR));
    assertThat(result.getError().getMessage(),
        startsWith("Invalid 'alternateBases' parameter, it must match the pattern [ACTG]+|N"));

    result = new BeaconAlleleResponse();
    alternateBases = "A";

    elixirBeaconService.checkParams(result, datasetStableIds, type, alternateBases, referenceBases,
        chromosome, start, startMin, startMax, end, endMin, endMax, referenceGenome);

    assertThat(result.getError(), nullValue());

    result = new BeaconAlleleResponse();
    alternateBases = ".";

    elixirBeaconService.checkParams(result, datasetStableIds, type, alternateBases, referenceBases,
        chromosome, start, startMin, startMax, end, endMin, endMax, referenceGenome);

    assertThat(result.getError(), notNullValue());
    assertThat(result.getError().getErrorCode(), equalTo(ErrorCode.GENERIC_ERROR));
    assertThat(result.getError().getMessage(),
        startsWith("Invalid 'alternateBases' parameter, it must match the pattern [ACTG]+|N"));
    
    result = new BeaconAlleleResponse();
    alternateBases = "N";

    elixirBeaconService.checkParams(result, datasetStableIds, type, alternateBases, referenceBases,
        chromosome, start, startMin, startMax, end, endMin, endMax, referenceGenome);

    assertThat(result.getError(), nullValue());
  }

  @Test
  public void checkReferenceBasesParamNotValid() {
    BeaconAlleleResponse result = new BeaconAlleleResponse();
    VariantType type = null;
    Integer start = 1111;
    Integer startMin = null;
    Integer startMax = null;
    Integer end = null;
    Integer endMin = null;
    Integer endMax = null;
    List<String> datasetStableIds = Arrays.asList("EGAD00000000001");
    String referenceGenome = "grch37";
    String chromosome = "12";
    String referenceBases = "R";
    String alternateBases = "C";

    elixirBeaconService.checkParams(result, datasetStableIds, type, alternateBases, referenceBases,
        chromosome, start, startMin, startMax, end, endMin, endMax, referenceGenome);

    assertThat(result.getError(), notNullValue());
    assertThat(result.getError().getErrorCode(), equalTo(ErrorCode.GENERIC_ERROR));
    assertThat(result.getError().getMessage(),
        startsWith("Invalid 'referenceBases' parameter, it must match the pattern [ACTG]+|N"));

    result = new BeaconAlleleResponse();
    referenceBases = "AT";

    elixirBeaconService.checkParams(result, datasetStableIds, type, alternateBases, referenceBases,
        chromosome, start, startMin, startMax, end, endMin, endMax, referenceGenome);

    assertThat(result.getError(), nullValue());

    result = new BeaconAlleleResponse();
    referenceBases = ".";

    elixirBeaconService.checkParams(result, datasetStableIds, type, alternateBases, referenceBases,
        chromosome, start, startMin, startMax, end, endMin, endMax, referenceGenome);

    assertThat(result.getError(), notNullValue());
    assertThat(result.getError().getErrorCode(), equalTo(ErrorCode.GENERIC_ERROR));
    assertThat(result.getError().getMessage(),
        startsWith("Invalid 'referenceBases' parameter, it must match the pattern [ACTG]+|N"));
    
    result = new BeaconAlleleResponse();
    referenceBases = "N";
    end = 123;

    elixirBeaconService.checkParams(result, datasetStableIds, type, alternateBases, referenceBases,
        chromosome, start, startMin, startMax, end, endMin, endMax, referenceGenome);

    assertThat(result.getError(), nullValue());
  }

  @Test
  public void queryForSNPs() {
    String datasetStableId = "EGAD00000000001";
    String variantType = null;
    Integer start = 4;
    Integer startMin = null;
    Integer startMax = null;
    Integer end = null;
    Integer endMin = null;
    Integer endMax = null;
    List<String> datasetStableIds = Arrays.asList(datasetStableId);
    String referenceGenome = "grch37";
    String chromosome = "4";
    String referenceBases = "C";
    String alternateBases = "G";
    String includeDatasetResponses = FilterDatasetResponse.NONE.toString();

    // Query with positive answer WITHOUT detailed response by dataset
    BeaconAlleleResponse response = elixirBeaconService.queryBeacon(datasetStableIds, variantType,
        alternateBases, referenceBases, chromosome, start, startMin, startMax, end, endMin, endMax,
        referenceGenome, includeDatasetResponses);

    assertThat(response.isExists(), equalTo(true));
    assertThat(response.getDatasetAlleleResponses(), nullValue());
    assertThat(response.getAlleleRequest(), notNullValue());

    includeDatasetResponses = FilterDatasetResponse.ALL.toString();
    // Query with positive answer AND detailed response by dataset
    response = elixirBeaconService.queryBeacon(datasetStableIds, variantType, alternateBases,
        referenceBases, chromosome, start, startMin, startMax, end, endMin, endMax, referenceGenome,
        includeDatasetResponses);

    assertThat(response.isExists(), equalTo(true));
    assertThat(response.getDatasetAlleleResponses().get(0).getDatasetId(),
        equalTo(datasetStableId));
    assertThat(response.getDatasetAlleleResponses().get(0).isExists(), equalTo(true));
    assertThat(response.getDatasetAlleleResponses().get(0).getCallCount(), notNullValue());
    assertThat(response.getDatasetAlleleResponses().get(0).getVariantCount(), notNullValue());
    assertThat(response.getDatasetAlleleResponses().get(0).getSampleCount(), notNullValue());
    assertThat(response.getDatasetAlleleResponses().get(0).getFrequency(), notNullValue());

    includeDatasetResponses = FilterDatasetResponse.HIT.toString();
    // Query with positive answer AND detailed response by dataset
    response = elixirBeaconService.queryBeacon(datasetStableIds, variantType, alternateBases,
        referenceBases, chromosome, start, startMin, startMax, end, endMin, endMax, referenceGenome,
        includeDatasetResponses);

    assertThat(response.isExists(), equalTo(true));
    assertThat(response.getDatasetAlleleResponses().get(0).getDatasetId(),
        equalTo(datasetStableId));
    assertThat(response.getDatasetAlleleResponses().get(0).isExists(), equalTo(true));

    includeDatasetResponses = FilterDatasetResponse.MISS.toString();
    // Query with positive answer AND detailed response by dataset
    response = elixirBeaconService.queryBeacon(datasetStableIds, variantType, alternateBases,
        referenceBases, chromosome, start, startMin, startMax, end, endMin, endMax, referenceGenome,
        includeDatasetResponses);

    assertThat(response.isExists(), equalTo(true));
    assertThat(response.getDatasetAlleleResponses(), nullValue());

    alternateBases = "T";
    // Query with negative answer AND detailed response by dataset
    response = elixirBeaconService.queryBeacon(datasetStableIds, variantType, alternateBases,
        referenceBases, chromosome, start, startMin, startMax, end, endMin, endMax, referenceGenome,
        includeDatasetResponses);

    assertThat(response.isExists(), equalTo(false));
    assertThat(response.getDatasetAlleleResponses().get(0).getDatasetId(),
        equalTo(datasetStableId));
    assertThat(response.getDatasetAlleleResponses().get(0).isExists(), equalTo(false));


    includeDatasetResponses = FilterDatasetResponse.NONE.toString();
    // Query with negative answer WITHOUT detailed response by dataset
    response = elixirBeaconService.queryBeacon(datasetStableIds, variantType, alternateBases,
        referenceBases, chromosome, start, startMin, startMax, end, endMin, endMax, referenceGenome,
        includeDatasetResponses);

    assertThat(response.isExists(), equalTo(false));
    assertThat(response.getDatasetAlleleResponses(), nullValue());

    includeDatasetResponses = FilterDatasetResponse.HIT.toString();
    // Query with negative answer AND detailed response by dataset if HIT
    response = elixirBeaconService.queryBeacon(datasetStableIds, variantType, alternateBases,
        referenceBases, chromosome, start, startMin, startMax, end, endMin, endMax, referenceGenome,
        includeDatasetResponses);

    assertThat(response.isExists(), equalTo(false));
    assertThat(response.getDatasetAlleleResponses(), nullValue());

    includeDatasetResponses = FilterDatasetResponse.MISS.toString();
    // Query with negative answer AND detailed response by dataset if MISS
    response = elixirBeaconService.queryBeacon(datasetStableIds, variantType, alternateBases,
        referenceBases, chromosome, start, startMin, startMax, end, endMin, endMax, referenceGenome,
        includeDatasetResponses);

    assertThat(response.isExists(), equalTo(false));
    assertThat(response.getDatasetAlleleResponses().get(0).getDatasetId(),
        equalTo(datasetStableId));
    assertThat(response.getDatasetAlleleResponses().get(0).isExists(), equalTo(false));
  }
  
  @Test
  public void queryForSVExactMatch() throws Exception {
    String datasetStableId = "EGAD00000000001";
    String variantType = VariantType.DUP.getType();
    Integer start = 12665100;
    Integer startMin = null;
    Integer startMax = null;
    Integer end = 12686201;
    Integer endMin = null;
    Integer endMax = null;
    List<String> datasetStableIds = Arrays.asList(datasetStableId);
    String referenceGenome = "grch37";
    String chromosome = "1";
    String referenceBases = "A";
    String alternateBases = null;
    String includeDatasetResponses = FilterDatasetResponse.NONE.toString();

    // Query with positive answer WITHOUT detailed response by dataset
    BeaconAlleleResponse response = elixirBeaconService.queryBeacon(datasetStableIds, variantType,
        alternateBases, referenceBases, chromosome, start, startMin, startMax, end, endMin, endMax,
        referenceGenome, includeDatasetResponses);

    assertThat(response.isExists(), equalTo(true));
    assertThat(response.getDatasetAlleleResponses(), nullValue());

    variantType = VariantType.INS.getType();
    start = 4;
    startMin = null;
    startMax = null;
    end = null;
    endMin = null;
    endMax = null;
    referenceBases = "GCG";
    chromosome = "3";
    response = elixirBeaconService.queryBeacon(datasetStableIds, variantType,
        alternateBases, referenceBases, chromosome, start, startMin, startMax, end, endMin, endMax,
        referenceGenome, includeDatasetResponses);

    assertThat(response.isExists(), equalTo(true));
    assertThat(response.getDatasetAlleleResponses(), nullValue());
  }

  @Test
  public void queryForSVRangeQuery() throws Exception {
    String datasetStableId = "EGAD00000000001";
    String variantType = VariantType.DUP.getType();
    Integer start = null;
    Integer startMin = 12665100;
    Integer startMax = 12665101;
    Integer end = null;
    Integer endMin = 12686200;
    Integer endMax = 12686201;
    List<String> datasetStableIds = Arrays.asList(datasetStableId);
    String referenceGenome = "grch37";
    String chromosome = "1";
    String referenceBases = "A";
    String alternateBases = null;
    String includeDatasetResponses = FilterDatasetResponse.NONE.toString();

    // Query with positive answer WITHOUT detailed response by dataset
    BeaconAlleleResponse response = elixirBeaconService.queryBeacon(datasetStableIds, variantType,
        alternateBases, referenceBases, chromosome, start, startMin, startMax, end, endMin, endMax,
        referenceGenome, includeDatasetResponses);

    assertThat(response.isExists(), equalTo(true));
    assertThat(response.getDatasetAlleleResponses(), nullValue());
  }

  @Test
  public void queryMultipleDatasets() throws Exception {
    String variantType = null;
    Integer start = 4;
    Integer startMin = null;
    Integer startMax = null;
    Integer end = null;
    Integer endMin = null;
    Integer endMax = null;
    List<String> datasetStableIds = Arrays.asList("EGAD00000000001", "EGAD00000000004", "EGAD00000000005");
    String referenceGenome = "grch37";
    String chromosome = "4";
    String referenceBases = "C";
    String alternateBases = "N";
    String includeDatasetResponses = FilterDatasetResponse.ALL.toString();

    BeaconAlleleResponse response = elixirBeaconService.queryBeacon(datasetStableIds, variantType,
        alternateBases, referenceBases, chromosome, start, startMin, startMax, end, endMin, endMax,
        referenceGenome, includeDatasetResponses);

    assertThat(response.isExists(), equalTo(true));
    assertThat(response.getDatasetAlleleResponses().size(), equalTo(3));
    assertThat(response.getDatasetAlleleResponses().get(0).getDatasetId(), equalTo("EGAD00000000001"));
    assertThat(response.getDatasetAlleleResponses().get(0).isExists(), equalTo(true));
    assertThat(response.getDatasetAlleleResponses().get(1).getDatasetId(), equalTo("EGAD00000000005"));
    assertThat(response.getDatasetAlleleResponses().get(1).isExists(), equalTo(true));
    assertThat(response.getDatasetAlleleResponses().get(2).getDatasetId(), equalTo("EGAD00000000004"));
    assertThat(response.getDatasetAlleleResponses().get(2).isExists(), equalTo(false));

    // Don't include detailed response per dataset
    includeDatasetResponses = FilterDatasetResponse.NONE.toString();
    response = elixirBeaconService.queryBeacon(datasetStableIds, variantType, alternateBases,
        referenceBases, chromosome, start, startMin, startMax, end, endMin, endMax, referenceGenome,
        includeDatasetResponses);
    assertThat(response.isExists(), equalTo(true));
    assertThat(response.getDatasetAlleleResponses(), nullValue());

    // Just return "true" datasets
    includeDatasetResponses = FilterDatasetResponse.HIT.toString();
    response = elixirBeaconService.queryBeacon(datasetStableIds, variantType, alternateBases,
        referenceBases, chromosome, start, startMin, startMax, end, endMin, endMax, referenceGenome,
        includeDatasetResponses);
    assertThat(response.isExists(), equalTo(true));
    assertThat(response.getDatasetAlleleResponses().size(), equalTo(2));
    assertThat(response.getDatasetAlleleResponses().get(0).getDatasetId(),
        equalTo("EGAD00000000001"));
    assertThat(response.getDatasetAlleleResponses().get(0).isExists(), equalTo(true));
    assertThat(response.getDatasetAlleleResponses().get(1).getDatasetId(),
        equalTo("EGAD00000000005"));
    assertThat(response.getDatasetAlleleResponses().get(1).isExists(), equalTo(true));

    // Just return "false" datasets
    includeDatasetResponses = FilterDatasetResponse.MISS.toString();
    response = elixirBeaconService.queryBeacon(datasetStableIds, variantType, alternateBases,
        referenceBases, chromosome, start, startMin, startMax, end, endMin, endMax, referenceGenome,
        includeDatasetResponses);
    assertThat(response.isExists(), equalTo(true));
    assertThat(response.getDatasetAlleleResponses().size(), equalTo(1));
    assertThat(response.getDatasetAlleleResponses().get(0).getDatasetId(),
        equalTo("EGAD00000000004"));
    assertThat(response.getDatasetAlleleResponses().get(0).isExists(), equalTo(false));
  }

  @Test
  public void queryAllDatasetPassingEmptyString() throws Exception {
    String variantType = null;
    Integer start = 4;
    Integer startMin = null;
    Integer startMax = null;
    Integer end = null;
    Integer endMin = null;
    Integer endMax = null;
    List<String> datasetStableIds = null;
    String referenceGenome = "grch37";
    String chromosome = "4";
    String referenceBases = "c";
    String alternateBases = "G";
    String includeDatasetResponses = FilterDatasetResponse.NONE.toString();

    BeaconAlleleResponse response = elixirBeaconService.queryBeacon(datasetStableIds, variantType,
        alternateBases, referenceBases, chromosome, start, startMin, startMax, end, endMin, endMax,
        referenceGenome, includeDatasetResponses);

    assertThat(response.isExists(), equalTo(true));
    assertThat(response.getDatasetAlleleResponses(), nullValue());
    
    // Get only HITS
    includeDatasetResponses = FilterDatasetResponse.HIT.toString();
    response = elixirBeaconService.queryBeacon(datasetStableIds, variantType, alternateBases,
        referenceBases, chromosome, start, startMin, startMax, end, endMin, endMax, referenceGenome,
        includeDatasetResponses);

    assertThat(response.isExists(), equalTo(true));
    assertThat(response.getDatasetAlleleResponses().size(), equalTo(1));
    assertThat(response.getDatasetAlleleResponses(), notNullValue());
    assertThat(response.getDatasetAlleleResponses().get(0).getDatasetId(),
        equalTo("EGAD00000000001"));
    assertThat(response.getDatasetAlleleResponses().get(0).isExists(), equalTo(true));

    // Get only MISSES
    includeDatasetResponses = FilterDatasetResponse.MISS.toString();
    response = elixirBeaconService.queryBeacon(datasetStableIds, variantType, alternateBases,
        referenceBases, chromosome, start, startMin, startMax, end, endMin, endMax, referenceGenome,
        includeDatasetResponses);

    assertThat(response.isExists(), equalTo(true)); // Global is true
    assertThat(response.getDatasetAlleleResponses(), notNullValue());
    assertThat(response.getDatasetAlleleResponses().size(), equalTo(2));
    assertThat(response.getDatasetAlleleResponses().get(0).getDatasetId(), equalTo("EGAD00000000004"));
    assertThat(response.getDatasetAlleleResponses().get(0).isExists(), equalTo(false));
    assertThat(response.getDatasetAlleleResponses().get(1).getDatasetId(), equalTo("EGAD00000000005"));
    assertThat(response.getDatasetAlleleResponses().get(1).isExists(), equalTo(false));
  }

  @Test
  public void queryControlledDatasetByUnauthenticatedUser() throws Exception {
    String datasetStableId = "EGAD00000000002";
    String variantType = null;
    Integer start = 14929;
    Integer startMin = null;
    Integer startMax = null;
    Integer end = null;
    Integer endMin = null;
    Integer endMax = null;
    List<String> datasetStableIds = Arrays.asList(datasetStableId);
    String referenceGenome = "grch37";
    String chromosome = "1";
    String referenceBases = "A";
    String alternateBases = "G";
    String includeDatasetResponses = FilterDatasetResponse.NONE.toString();

    // Query with positive answer WITHOUT detailed response by dataset
    BeaconAlleleResponse response = elixirBeaconService.queryBeacon(datasetStableIds, variantType,
        alternateBases, referenceBases, chromosome, start, startMin, startMax, end, endMin, endMax,
        referenceGenome, includeDatasetResponses);
    
    assertThat(response.getError(), notNullValue());
    assertThat(response.getError().getErrorCode(), equalTo(ErrorCode.UNAUTHORIZED));
  }

  @Test
  public void getAllAccessLevels() {
    Map<String, Object> expectedMap = fillExpectedMapWithAllFields();

    AccessLevelResponse accessLevels = elixirBeaconService.listAccessLevels(null, null, null,
        false, false);

    printMapInConsole(expectedMap);
    System.out.println("******************************************");
    System.out.println("******************************************");
    printMapInConsole(accessLevels.getFields());

    assertThat(accessLevels.getFields().size(), equalTo(9));
    assertThat(accessLevels.getFields(), hasAllEntries(expectedMap));
    assertThat(expectedMap, hasAllEntries(accessLevels.getFields()));
  }

  private Map<String, Object> fillExpectedMapWithAllFields() {
    Map<String, Object> expectedMap = new HashMap<>();
    HashMap<String, String> innerMap = new HashMap<>();
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
    expectedMap.put("beaconDataset", innerMap);
    innerMap = new HashMap<>();
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
    innerMap.put("beaconId", "PUBLIC");
    innerMap.put("apiVersion", "PUBLIC");
    innerMap.put("exists", "PUBLIC");
    innerMap.put("error", "PUBLIC");
    innerMap.put("info", "PUBLIC");
    innerMap.put("datasetAlleleResponses", "PUBLIC");
    innerMap.put("alleleRequest", "PUBLIC");
    expectedMap.put("beaconAlleleResponse", innerMap);
    innerMap = new HashMap<>();
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
    innerMap.put("errorCode", "PUBLIC");
    innerMap.put("errorMessage", "PUBLIC");
    expectedMap.put("beaconError", innerMap);
    innerMap = new HashMap<>();
    innerMap.put("consentCodeDataUse", "PUBLIC");
    innerMap.put("adamDataUse", "PUBLIC");
    expectedMap.put("dataUseConditions", innerMap);

    // Datasets
    HashMap<String, Object> innerMap2 = new HashMap<>();
    HashMap<String, Object> innerMap3 = new HashMap<>();
    innerMap = new HashMap<>();
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
    expectedMap.put("datasets", innerMap2);

    return expectedMap;
  }

  @Test
  public void getSomeAccessLevels() {
    Map<String, Object> expectedMap = fillExpectedMapWithSomeFields();

    AccessLevelResponse accessLevels = elixirBeaconService
        .listAccessLevels(Arrays.asList("error", "datasetAlleleResponses", "id"), null, null,
            false, false);

    assertThat(accessLevels.getError(), nullValue());
    assertThat(accessLevels.getFields().size(), equalTo(6));
    assertThat(accessLevels.getFields(), hasAllEntries(expectedMap));
    Map<String, Object> expectedInnerMap = (Map<String, Object>) expectedMap.get("datasets");
    Map<String, Object> datasetsMap = ( Map<String, Object>) accessLevels.getFields().get("datasets");
    assertThat(datasetsMap, hasAllEntries(expectedInnerMap));
    assertThat(expectedMap, hasAllEntries(accessLevels.getFields()));
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
    AccessLevelResponse accessLevels = elixirBeaconService
        .listAccessLevels(Arrays.asList("this field does not exist"), null, null, false,
            false);

    assertThat(accessLevels.getError(), notNullValue());
    assertThat(accessLevels.getError().getErrorCode(), equalTo(ErrorCode.NOT_FOUND));
    assertThat(accessLevels.getFields().size(), equalTo(0));
  }

  @SuppressWarnings("unchecked")
  private void printMapInConsole(Map<String, Object> map) {
    map.entrySet()
//        .stream()
        .forEach(e -> {
          System.out.println(e.getKey() + " :");
          ((Map<String, Object>) e.getValue())
              .forEach((key, value) -> {
                if (value instanceof String) {
                  System.out.println(" - " + key + " : " + value);
                } else if (value instanceof Map) {
                  System.out.println(" - " + key + " :");
                  ((Map<String, Object>) value)
                      .entrySet()
                      .forEach( entry -> {
                        if(entry.getValue() instanceof String) {
                          System.out.println("  - " + entry.getKey() + " : " + entry.getValue());
                        } else if (entry.getValue() instanceof Map) {
                          System.out.println("   - " + entry.getKey() + " :");
                          ((Map<String, String>) entry.getValue())
                              .forEach( (key2, value2) -> {
                            System.out.println("     - " + key2 + " : " + value2);
                          });
                        }
                      });
                }
              });
        });
  }

  @Test
  public void getDatasetsAccessLevels() {
    Map<String, Object> expectedMap = fillExpectedMapForDatasets();

    AccessLevelResponse accessLevels = elixirBeaconService.listAccessLevels(null, Arrays.asList("EGAD00000000001","EGAD00000000002"), null,
        false, false);

    System.out.println("************************");
    printMapInConsole(accessLevels.getFields());

    assertThat(accessLevels.getError(), nullValue());
    assertThat(accessLevels.getFields().size(), equalTo(9));
    assertThat(accessLevels.getFields(), hasAllEntries(expectedMap));
    assertThat(expectedMap, hasAllEntries(accessLevels.getFields()));
  }

  private Map<String, Object> fillExpectedMapForDatasets() {
    Map<String, Object> expectedMap = new HashMap<>();
    HashMap<String, String> innerMap = new HashMap<>();
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
    expectedMap.put("beaconDataset", innerMap);
    innerMap = new HashMap<>();
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
    innerMap.put("beaconId", "PUBLIC");
    innerMap.put("apiVersion", "PUBLIC");
    innerMap.put("exists", "PUBLIC");
    innerMap.put("error", "PUBLIC");
    innerMap.put("info", "PUBLIC");
    innerMap.put("datasetAlleleResponses", "PUBLIC");
    innerMap.put("alleleRequest", "PUBLIC");
    expectedMap.put("beaconAlleleResponse", innerMap);
    innerMap = new HashMap<>();
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
    innerMap.put("errorCode", "PUBLIC");
    innerMap.put("errorMessage", "PUBLIC");
    expectedMap.put("beaconError", innerMap);
    innerMap = new HashMap<>();
    innerMap.put("consentCodeDataUse", "PUBLIC");
    innerMap.put("adamDataUse", "PUBLIC");
    expectedMap.put("dataUseConditions", innerMap);

    // Datasets
    HashMap<String, Object> innerMap2 = new HashMap<>();
    HashMap<String, Object> innerMap3 = new HashMap<>();
    innerMap = new HashMap<>();
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
    expectedMap.put("datasets", innerMap2);

    printMapInConsole(expectedMap);

    return expectedMap;
  }

  @Test
  public void getSomeDatasetsAccessLevels() {
    Map<String, Object> expectedMap = fillExpectedMapForDatasetsWithSomeFields();

    AccessLevelResponse accessLevels = elixirBeaconService.listAccessLevels(Arrays.asList("new_sensitive_field", "id", "info", "datasetAlleleResponses"), Arrays.asList("EGAD00000000002"), null,
        false, false);

    printMapInConsole(accessLevels.getFields());
    System.out.println("************************");

    assertThat(accessLevels.getError(), nullValue());
    assertThat(accessLevels.getFields().size(), equalTo(6));
    assertThat(accessLevels.getFields(), hasAllEntries(expectedMap));
    assertThat(expectedMap, hasAllEntries(accessLevels.getFields()));
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

    // Datasets
    HashMap<String, Object> innerMap2 = new HashMap<>();
    HashMap<String, Object> innerMap3 = new HashMap<>();
    innerMap = new HashMap<>();
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
    expectedMap.put("datasets", innerMap2);

    printMapInConsole(expectedMap);

    return expectedMap;
  }

  @Test
  public void getNoDatasetsAccessLevels() {
    Map<String, Object> expectedMap = fillExpectedMapWithEmptyDatasets();

    AccessLevelResponse accessLevels = elixirBeaconService.listAccessLevels(Arrays.asList("id", "info", "datasetAlleleResponses"), Arrays.asList("EGAD00000000001"), null,
        false, false);

    System.out.println("************************");
    printMapInConsole(accessLevels.getFields());

    assertThat(accessLevels.getError(), nullValue());
    assertThat(accessLevels.getFields().size(), equalTo(6));
    assertThat(accessLevels.getFields(), hasAllEntries(expectedMap));
    assertThat(expectedMap, hasAllEntries(accessLevels.getFields()));
  }

  private Map<String, Object> fillExpectedMapWithEmptyDatasets() {
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
    expectedMap.put("beaconDataset", innerMap);
    innerMap = new HashMap<>();
    innerMap.put("info", "PUBLIC");
    innerMap.put("datasetAlleleResponses", "PUBLIC");
    expectedMap.put("beaconAlleleResponse", innerMap);
    innerMap = new HashMap<>();
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

    // Datasets
    HashMap<String, Object> innerMap2 = new HashMap<>();
    expectedMap.put("datasets", innerMap2);

    printMapInConsole(expectedMap);

    return expectedMap;
  }

}
