package org.ega_archive.elixirbeacon.constant;

import org.apache.commons.lang3.StringUtils;
import org.ega_archive.elixircore.ApplicationContextProvider;
import org.joda.time.DateTime;

public class BeaconConstants {

  public static final String ORGANIZATION_ID =
      (ApplicationContextProvider.getApplicationContext() != null) ? ApplicationContextProvider
          .getApplicationContext().getEnvironment().getProperty("elixirbeacon.organization.id")
          : "EGA";

  public static final String ORGANIZATION_NAME = (ApplicationContextProvider
      .getApplicationContext() != null) ? ApplicationContextProvider.getApplicationContext()
      .getEnvironment().getProperty("elixirbeacon.organization.name")
      : "European Genome-Phenome Archive (EGA)";

  public static final String ORGANIZATION_HOMEPAGE = (ApplicationContextProvider
      .getApplicationContext() != null) ? ApplicationContextProvider.getApplicationContext()
      .getEnvironment().getProperty("elixirbeacon.organization.welcome.url")
      : "https://ega-archive.org/";

  public static final String ORGANIZATION_LOGO = (ApplicationContextProvider
      .getApplicationContext() != null) ? ApplicationContextProvider.getApplicationContext()
      .getEnvironment().getProperty("elixirbeacon.organization.logo")
      : "https://ega-archive.org/images/logo.png";

  public static final String ORGANIZATION_DESCRIPTION = (ApplicationContextProvider
      .getApplicationContext() != null) ? ApplicationContextProvider.getApplicationContext()
      .getEnvironment().getProperty("elixirbeacon.organization.description") : "";

  public static final String ORGANIZATION_ADDRESS = (ApplicationContextProvider
      .getApplicationContext() != null) ? ApplicationContextProvider.getApplicationContext()
      .getEnvironment().getProperty("elixirbeacon.organization.address") : "";

  public static final String ORGANIZATION_CONTACT = (ApplicationContextProvider
      .getApplicationContext() != null) ? ApplicationContextProvider.getApplicationContext()
      .getEnvironment().getProperty("elixirbeacon.organization.contact")
      : "mailto:sabela.delatorre@crg.eu";

  public static final String API =
      (ApplicationContextProvider.getApplicationContext() != null) ? ApplicationContextProvider
          .getApplicationContext().getEnvironment().getProperty("elixirbeacon.beacon.api") : "1.0";

  public static final String BEACON_ID =
      (ApplicationContextProvider.getApplicationContext() != null) ? ApplicationContextProvider
          .getApplicationContext().getEnvironment().getProperty("elixirbeacon.beacon.id")
          : "elixir-beacon";

  public static final String BEACON_NAME =
      (ApplicationContextProvider.getApplicationContext() != null) ? ApplicationContextProvider
          .getApplicationContext().getEnvironment().getProperty("elixirbeacon.beacon.name")
          : "Elixir Beacon";

  public static final String BEACON_DESCRIPTION =
      (ApplicationContextProvider.getApplicationContext() != null) ? ApplicationContextProvider
          .getApplicationContext().getEnvironment().getProperty("elixirbeacon.beacon.description")
          : "This <a href=\"http://ga4gh.org/#/beacon\">Beacon</a> is based on the GA4GH Beacon <a href=\"https://github.com/ga4gh/schemas/blob/beacon/src/main/resources/avro/beacon.avdl\"></a>";

  public static final String BEACON_HOMEPAGE =
      (ApplicationContextProvider.getApplicationContext() != null) ? ApplicationContextProvider
          .getApplicationContext().getEnvironment().getProperty("elixirbeacon.beacon.homepage")
          : "https://ega-archive.org/elixir_beacon/";

  public static final String BEACON_ALTERNATIVE_URL = (ApplicationContextProvider
      .getApplicationContext() != null) ? ApplicationContextProvider.getApplicationContext()
      .getEnvironment().getProperty("elixirbeacon.beacon.alternative.url")
      : "https://ega-archive.org/elixir_beacon_web/";

  public static final String ISO8601_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:ss.SSS'Z'";

  // Date of first release
  public static final DateTime BEACON_CREATED =
      (ApplicationContextProvider.getApplicationContext() != null) ? DateTime
          .parse(ApplicationContextProvider.getApplicationContext().getEnvironment()
              .getProperty("elixirbeacon.beacon.creation.date")) : null;

  // Date of last update
  public static final DateTime BEACON_EDITED =
      ApplicationContextProvider.getApplicationContext() != null ? (StringUtils
          .isNotBlank(ApplicationContextProvider.getApplicationContext().getEnvironment()
              .getProperty("elixirbeacon.beacon.last.edition.date")) ? DateTime
          .parse(ApplicationContextProvider.getApplicationContext().getEnvironment()
              .getProperty("elixirbeacon.beacon.last.edition.date")) : null) : null;

  public static final String ACCESS_TYPE = "accessType";

  public static final String AUTHORIZED = "authorized";

  public static final String SIZE = "size";

}
