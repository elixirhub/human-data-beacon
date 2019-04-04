package org.ega_archive.elixirbeacon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BeaconOntologyTerm {

  private String ontology;

  private String term;

}
