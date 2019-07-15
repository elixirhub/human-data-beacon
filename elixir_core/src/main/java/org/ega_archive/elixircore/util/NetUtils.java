package org.ega_archive.elixircore.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

public class NetUtils {

  public static final String X_CLIENT_IP_ADDRESS = "X-CLIENT-IP-ADDRESS";
  private static final String X_FORWARDED_FOR = "X-FORWARDED-FOR";

  public static String getClientIpAddress(HttpServletRequest request) {
    String remoteAddr = request.getHeader(X_CLIENT_IP_ADDRESS);
    if (StringUtils.isBlank(remoteAddr)) {
      remoteAddr = request.getRemoteAddr();
      String x;
      // Look if the request has been forwarded through a proxy/load balancer
      if ((x = request.getHeader(X_FORWARDED_FOR)) != null) {
        remoteAddr = x;
        int idx = remoteAddr.indexOf(',');
        if (idx > -1) {
          // If there are several forwarded addresses, get the first one
          remoteAddr = remoteAddr.substring(0, idx);
        }
      }
    }
    return remoteAddr;
  }

}