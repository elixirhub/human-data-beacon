package org.ega_archive.elixirbeacon.model.elixirbeacon;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;


/**
 * The persistent class for the beacon_dataset database table.
 * 
 */

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class BeaconDataset implements Serializable {
	private static final long serialVersionUID = 1L;

	private String accessType;
	private Integer callCnt;
	private String description;
	private Integer id;
	private String referenceGenome;
	private Integer sampleCnt;
	private String stableId;
	private Integer variantCnt;

}
