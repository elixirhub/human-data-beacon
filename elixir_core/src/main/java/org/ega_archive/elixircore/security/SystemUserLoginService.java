package org.ega_archive.elixircore.security;


public interface SystemUserLoginService {

  public void addSystemUserToContext();

  public void addSystemUserToContext(boolean updateSystemUserSession);

}
