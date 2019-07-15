package org.ega_archive.elixirbeacon.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.SQLException;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.ega_archive.elixirbeacon.Application;
import org.ega_archive.elixirbeacon.dto.BeaconAlleleRequest;
import org.ega_archive.elixirbeacon.dto.BeaconAlleleResponse;
import org.ega_archive.elixirbeacon.enums.VariantType;
import org.ega_archive.elixircore.constant.ParamName;
import org.ega_archive.elixircore.test.util.TestUtils;
import org.ega_archive.elixircore.util.JsonUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@WebAppConfiguration
@SpringBootTest("server.port:0")
public class ElixirBeaconControllerTest {

  @Resource(name = "elixirbeaconDataSource")
  private DataSource dataSource;

  @Autowired
  @InjectMocks
  private ElixirBeaconController controller;
  
  private MockMvc mockMvc;
  
  @Autowired
  private ObjectMapper objectMapper;
  
  @Autowired
  private WebApplicationContext wac;
  
  @Before
  public void setUp() throws SQLException {
    TestUtils.removeUserFromContext();

    // Truncate + Insert
    TestUtils.populateDatabase(dataSource,
        "/db/truncate_tables.sql", 
        // Beacon
        "/db/beacon_dataset_table.sql", 
        "/db/beacon_data_table.sql", 
        // CC
        "/db/consent_code_category_table.sql",
        "/db/consent_code_table.sql",
        // Beacon->CC
        "/db/beacon_dataset_consent_code_table.sql");
    
    mockMvc = MockMvcBuilders.webAppContextSetup(wac)
        .alwaysExpect(status().isOk())
        .alwaysExpect(content().contentType(TestUtils.APPLICATION_JSON_CHARSET_UTF_8))
        .build();
  }
  
  @After
  public void tearDown() throws SQLException {
    // TestUtils.populateDatabase(dataSource, "/db/truncate_tables.sql");
  }
  
  @Test
  public void callRoot() throws Exception {
    
    MvcResult mvcResult = mockMvc.perform(get("/beacon/")
        .accept(MediaType.APPLICATION_JSON))
        .andReturn();
    
    String response = mvcResult.getResponse().getContentAsString();
    
    assertThat(response, notNullValue());
  }
  
  @Test
  public void callQueryGetMethod() throws Exception {
    
    MvcResult mvcResult = mockMvc.perform(get("/beacon/query")
        .param(ParamName.BEACON_CHROMOSOME, "19")
        .param(ParamName.BEACON_DATASET_IDS, "EGAD00000000001")
        .param(ParamName.BEACON_START, "1234")
        .param(ParamName.BEACON_REFERENCE_BASES, "C")
        .param(ParamName.BEACON_ALTERNATE_BASES, "A")
        .param(ParamName.BEACON_REFERENCE_GENOME, "grch37")
        .accept(MediaType.APPLICATION_JSON))
        .andReturn();
    
    BeaconAlleleResponse response = JsonUtils.jsonToObject(
        mvcResult.getResponse().getContentAsString(), BeaconAlleleResponse.class, objectMapper);

    assertThat(response, notNullValue());
    assertThat(response.getError(), nullValue());
  }
  
  @Test
  public void callQueryPostMethod() throws Exception {
    
    BeaconAlleleRequest request = BeaconAlleleRequest.builder()
        .variantType(VariantType.DUP.getType())
        .referenceName("19")
        .start(1234)
        .referenceBases("C")
        .assemblyId("grch37")
        .build();
    
    MvcResult mvcResult = mockMvc.perform(post("/beacon/query")
        .content(JsonUtils.objectToJson(request, objectMapper))
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .accept(MediaType.APPLICATION_JSON))
        .andReturn();
    
    BeaconAlleleResponse response = JsonUtils.jsonToObject(
        mvcResult.getResponse().getContentAsString(), BeaconAlleleResponse.class, objectMapper);

    assertThat(response, notNullValue());
    assertThat(response.getError(), nullValue());
  }
  
}
