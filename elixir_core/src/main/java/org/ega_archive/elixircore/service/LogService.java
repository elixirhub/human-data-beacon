package org.ega_archive.elixircore.service;

import java.util.List;

import ch.qos.logback.classic.Level;

public interface LogService {

  public Level getLoglevel();

  public void setLoglevel(String level);

  public void setLoglevel(String classname, String level);

  public List<String> getLoglevels();

}
