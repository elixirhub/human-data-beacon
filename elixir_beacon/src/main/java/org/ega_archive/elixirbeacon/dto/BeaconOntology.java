package org.ega_archive.elixirbeacon.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ega_archive.elixirbeacon.constant.BeaconConstants;
import org.ega_archive.elixircore.constant.CoreConstants;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BeaconOntology {

  private String beaconId = BeaconConstants.BEACON_ID;

  private String version = CoreConstants.API_VERSION;

  private String apiVersion = BeaconConstants.API;

  private List<BeaconOntologyTerm> ontologyTerms;

}
