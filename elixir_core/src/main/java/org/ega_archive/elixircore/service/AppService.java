package org.ega_archive.elixircore.service;

import org.ega_archive.elixircore.enums.Status;
import org.springframework.context.ApplicationContext;

public interface AppService {

  public void reload();

  public void stop();

  public Status getStatus();

  public void setStatus(Status status);

  public ApplicationContext getContext();

}
