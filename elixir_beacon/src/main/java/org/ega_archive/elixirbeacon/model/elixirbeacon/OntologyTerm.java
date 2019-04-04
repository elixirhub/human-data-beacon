package org.ega_archive.elixirbeacon.model.elixirbeacon;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "ontology_term", schema = "public", catalog = "ega_beacon_dev")
public class OntologyTerm {

  @Id
  private Integer id;

  private String ontology;

  private String term;

}
