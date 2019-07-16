package org.ega_archive.elixirbeacon.model.elixirbeacon;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Embeddable
public class BeaconDatasetConsentCodePK implements Serializable {

  private static final long serialVersionUID = 1756502310528714861L;

  private String code;

  @Column(name="dataset_id")
  private Integer datasetId;

}
