package org.ega_archive.elixircore.filter;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.log4j.Log4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.MDC;
import org.ega_archive.elixircore.correlation.CorrelationIdHolder;

/**
 * CorrelationHeaderFilter
 */
@Log4j
public class CorrelationHeaderFilter implements Filter {

  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                       FilterChain filterChain)
      throws IOException, ServletException {

    final HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
    try {
      String currentCorrId =
          httpServletRequest.getHeader(CorrelationIdHolder.CORRELATION_ID_HEADER);
      if (!currentRequestIsAsyncDispatcher(httpServletRequest)) {
        if (StringUtils.isBlank(currentCorrId)) {
          currentCorrId = System.getProperty("service.name") + "-" + UUID.randomUUID().toString();
          log.trace("No correlationId found in Header. Generated : " + currentCorrId);
        } else {
          log.trace("Found correlationId in Header : " + currentCorrId);
        }

        CorrelationIdHolder.setId(currentCorrId);
        MDC.put("correlationId", currentCorrId);
      }
    } catch (Throwable t) {
      log.error("Coult not set CorrelationId");
      MDC.put("correlationId", "NA");
    } finally {
      //TODO cleanup. If we use an application server like tomcat this would be needed
      //RequestCorrelation.cleanup();
    }

    filterChain.doFilter(httpServletRequest, servletResponse);
  }

  @Override
  public void destroy() {
  }


  private boolean currentRequestIsAsyncDispatcher(HttpServletRequest httpServletRequest) {
    return httpServletRequest.getDispatcherType().equals(DispatcherType.ASYNC);
  }

}