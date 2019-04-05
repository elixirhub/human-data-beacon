package org.ega_archive.elixirbeacon.model.elixirbeacon;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import lombok.Data;


/**
 * The persistent class for the beacon_dataset_access_level database view.
 * 
 */
@Data
@Entity
@Table(name="beacon_dataset_access_level")
@NamedQuery(name="DatasetAccessLevel.findAll", query="SELECT b FROM DatasetAccessLevel b")
public class DatasetAccessLevel implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private DatasetFieldPK id;

	@Column(name="access_level")
	private String accessLevel;

}
