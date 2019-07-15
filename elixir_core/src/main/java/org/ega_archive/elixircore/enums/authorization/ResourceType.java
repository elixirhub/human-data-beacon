package org.ega_archive.elixircore.enums.authorization;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

@Log4j
@Getter
@AllArgsConstructor
public enum ResourceType {

  USER(1, "user"),
  DATASET(2, "dataset"),
  SUBMISSION(3, "submission"),
  ANALYSIS(4, "analysis"),
  DAC(5, "dac"),
  EXPERIMENT(6, "experiment"),
  POLICY(7, "policy"),
  RUN(8, "run"),
  SAMPLE(9, "sample"),
  STUDY(10, "study"),
  PACKET(11, "packet"),
  FILE(12, "file");

  private int id;

  private String type;

  public static ResourceType parse(String type) {
    ResourceType resourceType = null;
    if (StringUtils.equalsIgnoreCase(type, USER.type)) {
      resourceType = USER;
    } else if (StringUtils.equalsIgnoreCase(type, DATASET.type)) {
      resourceType = DATASET;
    } else if (StringUtils.equalsIgnoreCase(type, SUBMISSION.type)) {
      resourceType = SUBMISSION;
    } else if (StringUtils.equalsIgnoreCase(type, ANALYSIS.type)) {
      resourceType = ANALYSIS;
    } else if (StringUtils.equalsIgnoreCase(type, DAC.type)) {
      resourceType = DAC;
    } else if (StringUtils.equalsIgnoreCase(type, EXPERIMENT.type)) {
      resourceType = EXPERIMENT;
    } else if (StringUtils.equalsIgnoreCase(type, POLICY.type)) {
      resourceType = POLICY;
    } else if (StringUtils.equalsIgnoreCase(type, RUN.type)) {
      resourceType = RUN;
    } else if (StringUtils.equalsIgnoreCase(type, SAMPLE.type)) {
      resourceType = SAMPLE;
    } else if (StringUtils.equalsIgnoreCase(type, STUDY.type)) {
      resourceType = STUDY;
    } else {
      log.error("Resource type not valid: " + type);
    }
    return resourceType;
  }

}
