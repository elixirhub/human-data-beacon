package org.ega_archive.elixircore.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.ega_archive.elixircore.dto.auth.User;
import org.ega_archive.elixircore.util.UserUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogRequestFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String userId = null;
    User user = UserUtils.getUserFromContext();
    if (user != null) {
      userId = user.getUserId();
    }

    HttpRequestWrapper wrappedRequest = new HttpRequestWrapper((HttpServletRequest) request);

    String userInfo = "User[" + userId + "] ";
    String urlInfo = "URL[" + wrappedRequest.getRequestURI() + "] ";
    String queryInfo = "Query[" + wrappedRequest.getQueryString() + "] ";
    String headersInfo = "Headers[" + printHeaders(wrappedRequest) + "] ";
    String parameterInfo = "Parameters[";
    String bodyInfo = "";
    if (user == null) {
      // It is a login request: only log username
      parameterInfo += wrappedRequest.getParameter("username") + "] ";
    } else {
      parameterInfo += printParameters(wrappedRequest) + "] ";
      //bodyInfo = "Body [" + IOUtils.toString(wrappedRequest.getInputStream()) + "] ";
    }

    log.info(userInfo + urlInfo + queryInfo + headersInfo + parameterInfo + bodyInfo);

    wrappedRequest.resetInputStream();
    filterChain.doFilter(wrappedRequest, response);
  }

  private String printHeaders(HttpServletRequest request) {
    String values = "{";

    Enumeration<String> headerNames = request.getHeaderNames();
    boolean first = true;
    while (headerNames.hasMoreElements()) {
      String name = headerNames.nextElement();
      String value = request.getHeader(name);
      if (!first) {
        values += ", ";
      }
      values += name + "=[" + value + "]";
      first = false;
    }
    values += "}";
    return values;
  }

  private String printParameters(HttpServletRequest request) {
    String values = "{";

    Enumeration<String> parameterNames = request.getParameterNames();
    boolean first = true;
    while (parameterNames.hasMoreElements()) {
      String name = parameterNames.nextElement();
      String value = request.getParameter(name);
      if (!first) {
        values += ", ";
      }
      values += name + "=[" + value + "]";
      first = false;
    }
    values += "}";
    return values;
  }

  private static class HttpRequestWrapper extends HttpServletRequestWrapper {

    private byte[] rawData;
    private HttpServletRequest request;
    private ResettableServletInputStream servletStream;

    public HttpRequestWrapper(HttpServletRequest request) {
      super(request);
      this.request = request;
      this.servletStream = new ResettableServletInputStream();
    }


    public void resetInputStream() {
      if (rawData != null) {
        servletStream.stream = new ByteArrayInputStream(rawData);
      }

    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
      if (rawData == null) {
        rawData = IOUtils.toByteArray(this.request.getInputStream());
        servletStream.stream = new ByteArrayInputStream(rawData);
      }
      return servletStream;
    }

    @Override
    public BufferedReader getReader() throws IOException {
      if (rawData == null) {
        rawData = IOUtils.toByteArray(this.request.getReader());
        servletStream.stream = new ByteArrayInputStream(rawData);
      }
      return new BufferedReader(
          new InputStreamReader(servletStream, request.getCharacterEncoding()));
    }


    private class ResettableServletInputStream extends ServletInputStream {

      private InputStream stream;

      @Override
      public int read() throws IOException {
        return stream.read();
      }

        @Override
        public boolean isFinished() {
            // TODO Auto-generated method stub
            return false;
        }
    
        @Override
        public boolean isReady() {
            // TODO Auto-generated method stub
            return false;
        }
    
        @Override
        public void setReadListener(ReadListener arg0) {
            // TODO Auto-generated method stub
            
        }
    }

  }

}
