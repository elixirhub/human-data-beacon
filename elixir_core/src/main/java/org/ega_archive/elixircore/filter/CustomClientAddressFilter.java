package org.ega_archive.elixircore.filter;

import org.ega_archive.elixircore.util.NetUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.log4j.Log4j;

@Log4j
public class CustomClientAddressFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

    if (request instanceof HttpServletRequest) {
      String clientIpAddress = NetUtils.getClientIpAddress(request);
      HttpHeaderRequestWrapper req = new HttpHeaderRequestWrapper((HttpServletRequest) request);
      req.setHeader(NetUtils.X_CLIENT_IP_ADDRESS, clientIpAddress);
      log.debug("Header " + NetUtils.X_CLIENT_IP_ADDRESS + " added with value: " + clientIpAddress);
      filterChain.doFilter(req, response);
    } else {
      filterChain.doFilter(request, response);
    }
  }

  private static class HttpHeaderRequestWrapper extends HttpServletRequestWrapper {

    private Map<String, List<String>> customHeaders = new HashMap<String, List<String>>();

    public HttpHeaderRequestWrapper(HttpServletRequest request) {
      super(request);
    }

    @Override
    public String getHeader(String name) {
      if (customHeaders.containsKey(name)) {
        return customHeaders.get(name).get(0);
      }
      return super.getHeader(name);
    }

    public void setHeader(String header, String value) {
      List<String> list = new ArrayList<String>();
      list.add(value);
      customHeaders.put(header, list);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
      List<String> list = new ArrayList<String>();

      HttpServletRequest request = (HttpServletRequest) getRequest();
      Enumeration<String> e = request.getHeaderNames();
      while (e.hasMoreElements()) {
        String headerName = e.nextElement();
        list.add(headerName);
      }
      list.addAll(customHeaders.keySet());

      return Collections.enumeration(list);
    }
  }

}
