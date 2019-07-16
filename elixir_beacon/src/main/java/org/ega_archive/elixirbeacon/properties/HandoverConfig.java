package org.ega_archive.elixirbeacon.properties;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "handovers")
public class HandoverConfig {

  private List<HandoverProperty> datasetHandover = new ArrayList<>();
//  private List<HandoverProperty> beaconHandover = new ArrayList<>();

  @Getter
  @Setter
  public static class HandoverProperty {

    @NotNull
    private String stableId;
    @NotNull
    private String id;
    @NotNull
    private String label;
    @NotNull
    private String url;
    private String note;
  }

}
