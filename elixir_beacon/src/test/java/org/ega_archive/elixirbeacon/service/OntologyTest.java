package org.ega_archive.elixirbeacon.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.sql.SQLException;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.ega_archive.elixirbeacon.Application;
import org.ega_archive.elixirbeacon.constant.BeaconConstants;
import org.ega_archive.elixirbeacon.dto.BeaconOntology;
import org.ega_archive.elixircore.constant.CoreConstants;
import org.ega_archive.elixircore.test.util.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@WebAppConfiguration
@SpringBootTest("server.port:0")
public class OntologyTest {

  @Autowired
  private BeaconOntologyService beaconOntologyService;

  @Resource(name = "elixirbeaconDataSource")
  private DataSource dataSource;

  @Before
  public void setUp() throws SQLException {
    TestUtils.removeUserFromContext();

    // Truncate + Insert
    TestUtils.populateDatabase(dataSource,
        "/db/truncate_tables.sql",
        "/db/ontology_term_table.sql");
  }

  @Test
  public void list() {
    BeaconOntology beaconOntology = beaconOntologyService.listOntologyTerms();

    assertThat(beaconOntology.getBeaconId(), equalTo(BeaconConstants.BEACON_ID));
    assertThat(beaconOntology.getVersion(), equalTo(CoreConstants.API_VERSION));
    assertThat(beaconOntology.getApiVersion(), equalTo(BeaconConstants.API));
    assertThat(beaconOntology.getOntologyTerms().size(), equalTo(5));
  }

}
