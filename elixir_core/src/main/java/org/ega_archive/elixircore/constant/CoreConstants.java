package org.ega_archive.elixircore.constant;

import org.ega_archive.elixircore.ApplicationContextProvider;

public class CoreConstants {

  public static final String TOKEN_HEADER = "X-Token";
  public static final String OK = "OK";
  public static final String API_VERSION = (ApplicationContextProvider.getApplicationContext()!= null) ? ApplicationContextProvider.getApplicationContext()
      .getEnvironment().getProperty("server.servlet-path").substring(1) : "v1";
  public static final String MICROSERVICE = (ApplicationContextProvider.getApplicationContext()!= null) ? ApplicationContextProvider.getApplicationContext()
      .getEnvironment().getProperty("server.context-path").substring(1) : "microservice";
  public static final String NOT_FOUND = "The requested resource was not found";
  public static final String NOT_FOUND_OR_ACCESS_DENIED = NOT_FOUND + " or you don't have permissions to access";
  public static final String ACCESS_DENIED = "You don't have permissions to access the resource specified";
}
