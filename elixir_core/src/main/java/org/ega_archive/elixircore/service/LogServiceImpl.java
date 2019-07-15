package org.ega_archive.elixircore.service;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.ega_archive.elixircore.exception.PreConditionFailed;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LogServiceImpl implements LogService {

  LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
  private static String mainLogger = "eu.crg.ega";

  private static String[]
      levels =
      {"ALL", "TRACE", "DEBUG", "INFO", "WARN", "ERROR", "FATAL", "OFF"};

  @Override
  public Level getLoglevel() {
    return loggerContext.getLogger(mainLogger).getLevel();
  }

  @Override
  public void setLoglevel(String level) {
    log.debug("Entering setLoglevel(" + level + ")");
    if (!Arrays.asList(levels).contains(level.toUpperCase())) {
      throw new PreConditionFailed("Level not correct");
    }
    loggerContext.getLogger(mainLogger).setLevel(Level.valueOf(level));

    log.debug("Exiting setLoglevel");
  }

  @Override
  public void setLoglevel(String classname, String level) {
    if (StringUtils.isBlank(level) || StringUtils.isBlank(classname)) {
      throw new PreConditionFailed("");
    }
    if (!Arrays.asList(levels).contains(level.toUpperCase())) {
      throw new PreConditionFailed("Level not correct");
    }
    loggerContext.getLogger(classname.toLowerCase()).setLevel(Level.valueOf(level));
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<String> getLoglevels() {
    log.debug("Entering getLogLevels");
    return (Arrays.asList(levels));

  }

}
