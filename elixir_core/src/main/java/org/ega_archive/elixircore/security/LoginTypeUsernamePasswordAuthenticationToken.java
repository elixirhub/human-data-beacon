package org.ega_archive.elixircore.security;

import org.ega_archive.elixircore.enums.LoginType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public final class LoginTypeUsernamePasswordAuthenticationToken extends
    UsernamePasswordAuthenticationToken {

  private static final long serialVersionUID = -5545583948895923457L;

  private final LoginType loginType;

  // used for attempting authentication
  public LoginTypeUsernamePasswordAuthenticationToken(String principal, String credentials,
      LoginType loginType, Object details) {
    super(principal, credentials);
    this.loginType = loginType;
    setDetails(details);
  }

  // used for returning to Spring Security after being
  // authenticated
  public LoginTypeUsernamePasswordAuthenticationToken(Object principal, String credentials,
      LoginType loginType, Collection<? extends GrantedAuthority> authorities, Object details) {
    super(principal, credentials, authorities);
    this.loginType = loginType;
    setDetails(details);
  }

  public LoginType getLoginType() {
    return loginType;
  }
}
