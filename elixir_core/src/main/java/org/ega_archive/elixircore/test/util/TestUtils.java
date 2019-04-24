package org.ega_archive.elixircore.test.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.ega_archive.elixircore.dto.Base;
import org.ega_archive.elixircore.dto.auth.DrupalRequesterUser;
import org.ega_archive.elixircore.dto.auth.MongoInternalUser;
import org.ega_archive.elixircore.dto.auth.PostgresSubmitterUser;
import org.ega_archive.elixircore.dto.auth.User;
import org.ega_archive.elixircore.enums.LoginType;
import org.ega_archive.elixircore.enums.UserRole;
import org.ega_archive.elixircore.security.LoginTypeUsernamePasswordAuthenticationToken;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Useful methods to use in tests.
 */
@Slf4j
public class TestUtils {

  public static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json;charset=UTF-8";

  public static void addSubmitterUserToContext(String userId) {
    String role = UserRole.ROLE_SUBMITTER.getValue();

    User user =
        User.builder()
            .username(userId)
            .userId(userId)
            .authorities(new HashSet<GrantedAuthority>(AuthorityUtils.createAuthorityList(role)))
            .postgresSubmitterUser(PostgresSubmitterUser.builder().dirName("ega-box-" + userId).centerName("CRG_test").build())
//            .mongoSubmitterUser(
//                MongoSubmitterUser.builder().egaBox("ega-box-" + userId).alias("CRG_test").build())
            .build();
    addToContext(user, LoginType.SUBMITTER, role);
  }

  private static void addToContext(User user, LoginType loginType, String... role) {
    LoginTypeUsernamePasswordAuthenticationToken testingAuthenticationToken =
        new LoginTypeUsernamePasswordAuthenticationToken(user, "password", loginType,
            AuthorityUtils.createAuthorityList(role), null);
    SecurityContextHolder.getContext().setAuthentication(testingAuthenticationToken);
  }

  public static void addAdminUserToContext(String userId) {
    String role = UserRole.ROLE_ADMIN.getValue();

    User user =
        User.builder().username(userId).userId(userId)
            .authorities(new HashSet<GrantedAuthority>(AuthorityUtils.createAuthorityList(role)))
            .mongoInternalUser(MongoInternalUser.builder().username(userId).build()).build();
    addToContext(user, LoginType.INTERNAL, role);
  }

  public static void addAdminSystemUserToContext(String userId) {
    String adminRole = UserRole.ROLE_ADMIN.getValue();
    String systemRole = "ROLE_SYSTEM";

    User user =
        User.builder().username(userId).userId(userId)
            .authorities(new HashSet<GrantedAuthority>(AuthorityUtils.createAuthorityList(adminRole, systemRole)))
            .mongoInternalUser(MongoInternalUser.builder().username(userId).build()).build();
    addToContext(user, LoginType.INTERNAL, adminRole, systemRole);
  }

  public static void addRequesterUserToContext(String userId) {
    String role = UserRole.ROLE_REQUESTER.getValue();

    User user =
        User.builder()
            .username(userId)
            .userId(userId)
            .authorities(new HashSet<GrantedAuthority>(AuthorityUtils.createAuthorityList(role)))
            .drupalRequesterUser(DrupalRequesterUser.builder().name(userId).build())
            .build();
    addToContext(user, LoginType.REQUESTER, role);
  }

  public static <T> Base<T> jsonToObject(MvcResult mvcResult, Class<T> typeclass,
                                         ObjectMapper objectMapper) throws JsonParseException, JsonMappingException, IOException {

    String content = mvcResult.getResponse().getContentAsString();

    JavaType type = objectMapper.getTypeFactory().constructParametricType(Base.class, typeclass);
    Base<T> basicDTO = objectMapper.readValue(content, type);
    return basicDTO;
  }

  public static <T> List<T> jsonToObjectList(MvcResult mvcResult, Class<T> typeclass,
                                             ObjectMapper objectMapper) throws JsonParseException, JsonMappingException, IOException {

    return jsonToObject(mvcResult, typeclass, objectMapper).getResponse().getResult();
  }

  /**
   * Method to be used when injecting @Mock fields into an @Autowired field.<br> I. e.<br> <code>
   * when(serviceUtils.getUserCenterName()).thenReturn("CRG_test"); ReflectionTestUtils.setField(TestUtils.unwrapService(dacXML),
   * "serviceUtils", serviceUtils); </code><br> this code injects serviceUtils (a mock) into
   * dacXML.
   */
  private static Object unwrapService(Object service) throws Exception {
    if (AopUtils.isAopProxy(service) && service instanceof Advised) {
      Object target = ((Advised) service).getTargetSource().getTarget();
      return target;
    }
    return service;
  }

  /**
   * Method that injects <code>mock</code> into <code>service</code> with name <code>mockName</code>
   * .
   *
   * @param mock     : mock object
   * @param mockName : java name of the autowired field that is going to be replaced with the mock.
   * @param service  : service where the autowired field is.
   */
  public static void injectMock(Object mock, String mockName, Object service) throws Exception {
    ReflectionTestUtils.setField(TestUtils.unwrapService(service), mockName, mock);
  }

  public static void removeUserFromContext() {
    SecurityContextHolder.clearContext();//getContext().setAuthentication(null);
  }

  /**
   * Insert data into database.
   * 
   * @param dataSource
   * @param sqlScripts : specify the path relative to src/test/resources/
   * @throws SQLException
   */
  public static void populateDatabase(DataSource dataSource, String... sqlScripts)
      throws SQLException {

    List<Resource> resources = new ArrayList<Resource>();
    for (String script : sqlScripts) {
      resources.add(new ClassPathResource(script));
    }

    ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
    populator.addScripts(resources.toArray(new Resource[0]));

    Connection connection = null;
    try {
      connection = DataSourceUtils.getConnection(dataSource);
      populator.populate(connection);
    } finally {
      if (connection != null) {
        DataSourceUtils.releaseConnection(connection, dataSource);
      }
    }
  }

}
