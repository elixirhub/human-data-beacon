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
public class DatasetFieldPK implements Serializable {

  private static final long serialVersionUID = 1756502310528714861L;

  private String datasetStableId;

  private String field;

  private String parentField;


}
