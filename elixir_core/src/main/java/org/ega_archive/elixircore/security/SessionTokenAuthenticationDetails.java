package org.ega_archive.elixircore.security;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SessionTokenAuthenticationDetails implements Serializable {

  private static final long serialVersionUID = 47186242862035737L;

  private final String token;
  
}
