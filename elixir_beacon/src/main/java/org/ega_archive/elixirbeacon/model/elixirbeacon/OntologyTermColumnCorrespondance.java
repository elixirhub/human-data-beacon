package org.ega_archive.elixirbeacon.model.elixirbeacon;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class OntologyTermColumnCorrespondance {

  private Integer id;

  private String ontology;

  private String term;

  private String sampleTableColumnName;

  private String sampleTableColumnValue;

  private String additionalComments;

}
