package org.ega_archive.elixircore.filter;

import com.google.common.collect.ImmutableMap;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.log4j.Log4j;

import static com.google.common.collect.Iterators.asEnumeration;
import static com.google.common.collect.ObjectArrays.concat;

@Log4j
public class LowercaseRequestParamsFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

    filterChain.doFilter(new LowerCaseRequestWrapper(request), response);
  }

  private static class LowerCaseRequestWrapper extends HttpServletRequestWrapper {

    private Map<String, String[]> lowerCaseParams = new HashMap<>();

    public LowerCaseRequestWrapper(final HttpServletRequest request) {
      super(request);
      Map<String, String[]> originalParams = request.getParameterMap();
      for (String name: originalParams.keySet()) {
        String lower = name.toLowerCase();
        if (!lowerCaseParams.containsKey(lower)) {
          lowerCaseParams.put(lower, new String[0]);
        }
        lowerCaseParams.put(lower,
                            concat(lowerCaseParams.get(lower),
                                   originalParams.get(name),
                                   String.class));
      }
      log.debug(lowerCaseParams);
    }

    @Override
    public Map getParameterMap() {
      return new ImmutableMap.Builder<String, String[]>()
          .putAll(lowerCaseParams).build();
    }

    @Override
    public String getParameter(final String name) {
      String[] values = getParameterValues(name);
      if (values != null) {
        return values[0];
      } else {
        return null;
      }
    }

    @Override
    public Enumeration getParameterNames() {
      return asEnumeration(lowerCaseParams.keySet().iterator());
    }

    @Override
    public String[] getParameterValues(final String name) {
      return lowerCaseParams.get(name);
    }

  }

}
