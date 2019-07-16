package org.ega_archive.elixircore.correlation;

/**
 * Utility class which stores ThreadLocal (Request) correlation Id.
 */
public class CorrelationIdHolder {

  public static final String CORRELATION_ID_HEADER = "correlationId";

  private static final ThreadLocal<String> id = new ThreadLocal<String>();

  public static String getId() {
    return id.get();
  }

  public static void setId(String correlationId) {
    id.set(correlationId);
  }

  public static void cleanup() {
    id.remove();
  }
}