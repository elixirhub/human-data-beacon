package org.ega_archive.elixirbeacon.model.elixirbeacon;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;


/**
 * The persistent class for the beacon_dataset_consent_code database table.
 * 
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class BeaconDatasetConsentCode implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private BeaconDatasetConsentCodePK id;

    private String category;

    private String description;

	private String additionalConstraint;

    private String additionalDescription;

    private String version;

}
