package org.ega_archive.elixircore.service;

import org.ega_archive.elixircore.ApplicationContextProvider;
import org.ega_archive.elixircore.enums.Status;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j;

@Log4j
@Service
public class AppServiceImpl implements AppService {

  private Status status = Status.STARTED;

  public void reload() {
    ApplicationContext ctx = ApplicationContextProvider.getApplicationContext();
    ((ConfigurableApplicationContext) ctx).refresh();
  }

  public void stop() {
    ApplicationContext ctx = ApplicationContextProvider.getApplicationContext();
    if (ctx != null) {
      ((ConfigurableApplicationContext) ctx).close();
    } else {
      log.debug("No context to close");
    }
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    if (this.status != Status.STOPPING) {
      this.status = status;
    }
  }

  @Override
  public ApplicationContext getContext() {
    return ApplicationContextProvider.getApplicationContext();
  }
}
