package org.ega_archive.elixircore.constant;

/*
 * Please remember that theses constant paramnames can be used in a pathvariable and in a
 * requestparam at the same time so all strings must be lowercase
 */
public class ParamName {

  public static final String LOGIN_TYPE = "loginType";

  public static final String PASSWORD = "password";

  public static final String USERNAME = "username";

  public static final String LOGIN_ENDPOINT = "/login";

  //TODO, at this point the servlet name (v1) cannot be retrieved, spring still not loaded
  //TODO remove the final and test like corecontants?
  //TODO If a microservice goes to v2 but login is still v1 this will break things
  //TODO or if a portal talks to another microservice
  //WARN Do not user this for specifying the login point in a call!!
  public static final String LOGIN_FULL_ENDPOINT = "/v1" + LOGIN_ENDPOINT;
  
  public static final String SECONDARY_LOGIN_ENDPOINT = "/auth";
  public static final String SECONDARY_LOGIN_FULL_ENDPOINT = "/v1" + SECONDARY_LOGIN_ENDPOINT;

  public static final String LOGOUT_ENDPOINT = "/logout";

  //TODO, at this point the servlet name (v1) cannot be retrieved, spring still not loaded
  public static final String LOGOUT_FULL_ENDPOINT = "/v1" + LOGOUT_ENDPOINT;

  public static final String LIMIT = "limit";

  public static final String SKIP = "skip";

  public static final String LEVEL = "level";

  public static final String PARAMS = "params";
  
  public static final String SORT = "sort";

  public static final String VALUE = "value";

  public static final String CACHE_TYPE = "cachetype";
  
  // Beacon
  public static final String BEACON_DATASET_IDS = "datasetids";
  public static final String BEACON_ALTERNATE_BASES = "alternatebases";
  public static final String BEACON_REFERENCE_BASES = "referencebases";
  public static final String BEACON_CHROMOSOME = "referencename";
  public static final String BEACON_START = "start";
  public static final String BEACON_START_MIN = "startmin";
  public static final String BEACON_START_MAX = "startmax";
  public static final String BEACON_END = "end";
  public static final String BEACON_END_MIN = "endmin";
  public static final String BEACON_END_MAX = "endmax";
  public static final String VARIANT_TYPE = "varianttype";
  public static final String BEACON_REFERENCE_GENOME = "assemblyid";
  public static final String BEACON_INCLUDE_DATASET_RESPONSES = "includedatasetresponses";
  public static final String BEACON_FILTERS = "filters";
  public static final String BEACON_FIELDS = "fields";
  public static final String BEACON_LEVEL = "level";
  public static final String BEACON_INCLUDE_FIELD_DETAILS = "includefielddetails";
  public static final String BEACON_INCLUDE_DATASET_DETAILS = "includedatasetdetails";
  public static final String BEACON_MATE_NAME = "matename";

}
