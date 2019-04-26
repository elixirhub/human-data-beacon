package org.ega_archive.elixirbeacon.model.elixirbeacon;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;


/**
 * The persistent class for the beacon_dataset_access_level database view.
 * 
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class DatasetAccessLevel implements Serializable {
	private static final long serialVersionUID = 1L;

//	@EmbeddedId
	private DatasetFieldPK id;

//	@Column(name="access_level")
	private String accessLevel;

}
