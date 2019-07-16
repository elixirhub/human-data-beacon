package org.ega_archive.elixircore.filter;

import org.ega_archive.elixircore.constant.CoreConstants;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Filter for adding mandatory headers that are needed to avoid cross-origin (CORS) problems when
 * doing Ajax calls to this portal:<br>
 *
 * Example of error: <b>No 'Access-Control-Allow-Origin' header is present on the requested
 * resource. Origin 'http://egasubmitter' is therefore not allowed access.</b>
 */
@Component
public class CORSFilter extends OncePerRequestFilter {

  private static final String WILDCARD = "*";

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    Properties prop = new Properties();
    try (InputStream in = getClass().getResourceAsStream("/META-INF/corsFilter.properties")) {
      prop.load(in);
    }

    Set<String> allowedOrigins =
        new HashSet<String>(Arrays.asList(prop.getProperty("allowed.origins").split(",")));

    String originHeader = request.getHeader("Origin");

    if (allowedOrigins.contains(WILDCARD) || allowedOrigins.contains(originHeader)) {
      response.setHeader("Access-Control-Allow-Origin", originHeader);
    }

    response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
    response.addHeader("Access-Control-Allow-Headers",
        "origin, x-requested-with, Content-Type, Authorization, " + CoreConstants.TOKEN_HEADER);
    response.addHeader("Access-Control-Allow-Credentials", "true");

    filterChain.doFilter(request, response);
  }

}
