package org.ega_archive.elixirbeacon.model.elixirbeacon;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class BeaconDatasetConsentCodePK implements Serializable {

  private static final long serialVersionUID = 1756502310528714861L;

  private String code;

  private Integer datasetId;

}
