package org.ega_archive.elixirbeacon.model.elixirbeacon;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import lombok.Data;


/**
 * The persistent class for the beacon_dataset_consent_code database table.
 * 
 */
@Data
@Entity
@Table(name="beacon_dataset_consent_code", schema = "public")
@NamedQuery(name="BeaconDatasetConsentCode.findAll", query="SELECT b FROM BeaconDatasetConsentCode b")
public class BeaconDatasetConsentCode implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	private BeaconDatasetConsentCodePK id;

	@Column
    private String category;

//    private String code;
//
//    @Column(name="dataset_id")
//    private Integer datasetId;

	@Column
    private String description;
	
	@Column(name = "additional_constraint")
	private String additionalConstraint;

	@Column(name = "additional_description")
    private String additionalDescription;

	@Column
    private String version;

}
