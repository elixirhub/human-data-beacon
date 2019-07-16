/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ega_archive.elixircore.filter;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;


public class CustomHiddenHttpMethodFilter extends OncePerRequestFilter {

  /**
   * Default method parameter: {@code _method}
   */
  public static final String DEFAULT_METHOD_PARAM = "_method";

  private String methodParam = DEFAULT_METHOD_PARAM;


  /**
   * Set the parameter name to look for HTTP methods.
   *
   * @see #DEFAULT_METHOD_PARAM
   */
  public void setMethodParam(String methodParam) {
    Assert.hasText(methodParam, "'methodParam' must not be empty");
    this.methodParam = methodParam;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

    String paramValue = request.getParameter(this.methodParam);
    if (("POST".equals(request.getMethod()) || "GET".equals(request.getMethod()))
        && StringUtils.hasLength(paramValue)) {
      String method = paramValue.toUpperCase(Locale.ENGLISH);
      HttpServletRequest wrapper = new HttpMethodRequestWrapper(request, method);
      filterChain.doFilter(wrapper, response);
    } else {
      filterChain.doFilter(request, response);
    }
  }


  /**
   * Simple {@link HttpServletRequest} wrapper that returns the supplied method for {@link
   * HttpServletRequest#getMethod()}.
   */
  private static class HttpMethodRequestWrapper extends HttpServletRequestWrapper {

    private final String method;

    public HttpMethodRequestWrapper(HttpServletRequest request, String method) {
      super(request);
      this.method = method;
    }

    @Override
    public String getMethod() {
      return this.method;
    }
  }

}
