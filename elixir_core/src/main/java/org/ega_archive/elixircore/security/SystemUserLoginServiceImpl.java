package org.ega_archive.elixircore.security;

import org.apache.commons.lang3.StringUtils;
import org.ega_archive.elixircore.dto.auth.User;
import org.ega_archive.elixircore.enums.LoginType;
import org.ega_archive.elixircore.exception.RestRuntimeException;
import org.ega_archive.elixircore.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class SystemUserLoginServiceImpl implements SystemUserLoginService {

  @Value("${system.username}")
  protected String username;

  @Value("${system.password}")
  protected String password;

  @Autowired
  private MySessionToken mySessionToken;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Override
  public void addSystemUserToContext() {
    User user = UserUtils.getUserFromContext();
    if (user != null && user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SYSTEM"))) {
      // System user is already in security context
      // Check if session is still valid and login if it is not
      addSystemUser(false);
      return;
    }

    addSystemUser(true);
  }

  @Override
  public void addSystemUserToContext(boolean updateSystemUserSession) {
    User user = UserUtils.getUserFromContext();
    if (user != null && user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SYSTEM"))) {
      // System user is already in security context
      // Check if session is still valid and login if it is not
      addSystemUser(updateSystemUserSession);
      return;
    }

    addSystemUser(updateSystemUserSession);
  }

  private void addSystemUser(boolean addToContext) {
    if (StringUtils.isBlank(mySessionToken.getSessionToken())) {
      authenticateWithUsernameAndPassword();
    } else {
      authenticateWithSessionToken(addToContext);
    }
  }

  protected void authenticateWithUsernameAndPassword() {
    LoginTypeUsernamePasswordAuthenticationToken loginTypeUsernamePasswordAuthenticationToken =
        new LoginTypeUsernamePasswordAuthenticationToken(username, password, LoginType.INTERNAL,
            null);
    //Clear security context before performing calls to avoid sessionTimeOut if the authenticate
    //process calls other microservices (for example: the configmicroservice)
    SecurityContextHolder.clearContext();
    //Try to authenticate
    Authentication authentication =
        authenticationManager.authenticate(loginTypeUsernamePasswordAuthenticationToken);
    SecurityContextHolder.getContext().setAuthentication(authentication);
    loginTypeUsernamePasswordAuthenticationToken =
        (LoginTypeUsernamePasswordAuthenticationToken) authentication;
    SessionTokenAuthenticationDetails details =
        (SessionTokenAuthenticationDetails) loginTypeUsernamePasswordAuthenticationToken
            .getDetails();
    mySessionToken.setSessionToken(details.getToken());
  }

  private void authenticateWithSessionToken(boolean addUserToContext) {
    PreAuthenticatedAuthenticationToken token =
        new PreAuthenticatedAuthenticationToken(mySessionToken.getSessionToken(), "");
    token.setDetails(new SessionTokenAuthenticationDetails(mySessionToken.getSessionToken()));
    try {
      Authentication authentication = authenticationManager.authenticate(token);
      if (addUserToContext) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    } catch (RestRuntimeException | UsernameNotFoundException e) {
      authenticateWithUsernameAndPassword();
    }
  }

}
