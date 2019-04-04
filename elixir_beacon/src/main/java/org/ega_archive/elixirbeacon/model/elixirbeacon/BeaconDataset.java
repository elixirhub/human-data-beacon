package org.ega_archive.elixirbeacon.model.elixirbeacon;

import java.io.Serializable;
import javax.persistence.*;

import lombok.Data;


/**
 * The persistent class for the beacon_dataset database table.
 * 
 */
@Data
@Entity
@Table(name="beacon_dataset", schema = "public")
@NamedQuery(name="BeaconDataset.findAll", query="SELECT b FROM BeaconDataset b")
public class BeaconDataset implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="access_type")
	private String accessType;

	@Column(name="call_cnt")
	private Integer callCnt;

	private String description;

	@Id
	private Integer id;

	@Column(name="reference_genome")
	private String referenceGenome;

	@Column(name="sample_cnt")
	private Integer sampleCnt;

	@Column(name="stable_id")
	private String stableId;

	@Column(name="variant_cnt")
	private Integer variantCnt;

}
