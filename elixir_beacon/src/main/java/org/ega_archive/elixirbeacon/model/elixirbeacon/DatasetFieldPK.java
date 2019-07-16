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
public class DatasetFieldPK implements Serializable {

  private static final long serialVersionUID = 1756502310528714861L;

  @Column(name="dataset_stable_id")
  private String datasetStableId;

  private String field;

  @Column(name="parent_field")
  private String parentField;


}
