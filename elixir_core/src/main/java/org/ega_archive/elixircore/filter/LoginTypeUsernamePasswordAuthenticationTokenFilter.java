package org.ega_archive.elixircore.filter;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.ega_archive.elixircore.constant.ParamName;
import org.ega_archive.elixircore.enums.LoginType;
import org.ega_archive.elixircore.helper.CustomRequestMatcher;
import org.ega_archive.elixircore.security.LoginTypeUsernamePasswordAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class LoginTypeUsernamePasswordAuthenticationTokenFilter extends
    UsernamePasswordAuthenticationFilter {

  private final AuthenticationSuccessHandler myAuthenticationSuccessHandler;

  @Autowired
  public LoginTypeUsernamePasswordAuthenticationTokenFilter(
      AuthenticationSuccessHandler myAuthenticationSuccessHandler) {
    this.myAuthenticationSuccessHandler = myAuthenticationSuccessHandler;
    RequestMatcher requestMatcher =
        new CustomRequestMatcher(Arrays.asList(ParamName.LOGIN_FULL_ENDPOINT,
            ParamName.SECONDARY_LOGIN_FULL_ENDPOINT), HttpMethod.POST);
    setRequiresAuthenticationRequestMatcher(requestMatcher);
    setUsernameParameter(ParamName.USERNAME);
    setPasswordParameter(ParamName.PASSWORD);
  }

  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException {
    if (!StringUtils.equals(request.getMethod(), HttpMethod.POST.toString())) {
      throw new AuthenticationServiceException("Authentication method not supported: "
          + request.getMethod());
    }
    String username = obtainUsername(request);
    String password = obtainPassword(request);
    String type = request.getParameter(ParamName.LOGIN_TYPE);
    LoginType loginType = null;

    try {
      loginType = LoginType.parse(type);
    } catch (Exception ex) {
      throw new AuthenticationServiceException("Authentication type not supported");
    }
    // authRequest.isAuthenticated() = false since no
    //authorities are specified
    LoginTypeUsernamePasswordAuthenticationToken authRequest =
        new LoginTypeUsernamePasswordAuthenticationToken(username, password, loginType, null);

    setDetails(request, authRequest);
    setAuthenticationSuccessHandler(myAuthenticationSuccessHandler);
    return this.getAuthenticationManager().authenticate(authRequest);
  }

  @Autowired
  @Override
  public void setAuthenticationManager(AuthenticationManager authenticationManager) {
    super.setAuthenticationManager(authenticationManager);
  }

  @Autowired
  @Override
  public void setAuthenticationSuccessHandler(AuthenticationSuccessHandler successHandler) {
    super.setAuthenticationSuccessHandler(successHandler);
  }

}
