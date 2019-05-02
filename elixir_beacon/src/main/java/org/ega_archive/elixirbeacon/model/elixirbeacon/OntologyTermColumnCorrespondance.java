package org.ega_archive.elixirbeacon.model.elixirbeacon;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "ontology_term_column_correspondance", schema = "public")
public class OntologyTermColumnCorrespondance {

  @Id
  private Integer id;

  private String ontology;

  private String term;

  @Column(name = "sample_table_column_name")
  private String sampleTableColumnName;

  @Column(name = "sample_table_column_value")
  private String sampleTableColumnValue;

  @Column(name = "additional_comments")
  private String additionalComments;

}
