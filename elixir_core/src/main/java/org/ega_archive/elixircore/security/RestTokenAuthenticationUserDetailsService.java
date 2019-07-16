package org.ega_archive.elixircore.security;

import org.apache.commons.lang3.StringUtils;
import org.ega_archive.elixircore.constant.CoreConstants;
import org.ega_archive.elixircore.dto.Base;
import org.ega_archive.elixircore.dto.auth.SessionUser;
import org.ega_archive.elixircore.enums.ServiceType;
import org.ega_archive.elixircore.event.sender.RestEventSender;
import org.ega_archive.elixircore.exception.RestRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

public class RestTokenAuthenticationUserDetailsService
    implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

  @Autowired
  private RestEventSender restEventSender;

  @Override
  public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token)
      throws UsernameNotFoundException {

    HttpHeaders httpHeaders = new HttpHeaders();
    String sessionToken = token.getPrincipal().toString();
    if (StringUtils.isNotBlank(sessionToken)) {
      httpHeaders.add(CoreConstants.TOKEN_HEADER, sessionToken);
    } else {
      System.out.println("SessionToken is empty");
    }

    try {
      final Base<SessionUser>
          returnedSessionUser =
          restEventSender.sendMicroservice(ServiceType.SESSION,
                                           "/sessions/" + sessionToken,
                                           HttpMethod.GET, null, null, httpHeaders,
                                           SessionUser.class,
                                           false);
      SessionUser sessionUser = returnedSessionUser.getResponse().getResult().get(0);
      if (returnedSessionUser == null || returnedSessionUser.getHeader() == null
          || Integer.parseInt(returnedSessionUser.getHeader().getCode()) != HttpStatus.OK.value()
          || returnedSessionUser.getResponse() == null
          || returnedSessionUser.getResponse().getResult() == null
          || returnedSessionUser.getResponse().getResult().size() != 1
          || sessionUser.getUser() == null) {
        throw new UsernameNotFoundException("Username not found");
      }
      return sessionUser.getUser();
    } catch (Exception e) {
      if (e instanceof RestRuntimeException) {
        throw new UsernameNotFoundException(e.getMessage());
      }
    }
    throw new UsernameNotFoundException("User not found");
  }

}
