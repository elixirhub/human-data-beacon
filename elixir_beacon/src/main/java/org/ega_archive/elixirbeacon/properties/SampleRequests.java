package org.ega_archive.elixirbeacon.properties;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix="querySamples")
public class SampleRequests {

  @NotNull
  private String assemblyId1;
  
  private Integer start1;
  
  private Integer startMin1;
  
  private Integer startMax1;

  private Integer end1;
  
  private Integer endMin1;
  
  private Integer endMax1;
  
  @NotNull
  private String referenceName1;
  
  @NotNull
  private String referenceBases1;
  
  private List<String> datasetIds1;
  
  private String alternateBases1;
  
  @NotNull
  private String assemblyId2;
  
  private Integer start2;
  
  private Integer startMin2;
  
  private Integer startMax2;
  
  private Integer end2;
  
  private Integer endMin2;
  
  private Integer endMax2;
  
  @NotNull
  private String referenceName2;
  
  @NotNull
  private String referenceBases2;
  
  private String variantType2;
  
  private List<String> datasetIds2;
  
  private String alternateBases2;
  
  @NotNull
  private String assemblyId3;
  
  private Integer start3;
  
  private Integer startMin3;
  
  private Integer startMax3;
  
  private Integer end3;
  
  private Integer endMin3;
  
  private Integer endMax3;
  
  @NotNull
  private String referenceName3;
  
  @NotNull
  private String referenceBases3;
  
  private String variantType3;
  
  private List<String> datasetIds3;
  
  private String alternateBases3;
   
}
