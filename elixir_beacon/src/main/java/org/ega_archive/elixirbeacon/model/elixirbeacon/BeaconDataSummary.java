package org.ega_archive.elixirbeacon.model.elixirbeacon;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureParameter;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "beacon_data_summary", schema = "public")
@NamedStoredProcedureQuery(name = "querySummaryData", procedureName = "public.query_data_summary_response",
    resultClasses = {BeaconDataSummary.class}, parameters = {
    @StoredProcedureParameter(mode = ParameterMode.IN, name = "_variant_type", type = String.class),
    @StoredProcedureParameter(mode = ParameterMode.IN, name = "_start", type = Integer.class),
    @StoredProcedureParameter(mode = ParameterMode.IN, name = "_start_min", type = Integer.class),
    @StoredProcedureParameter(mode = ParameterMode.IN, name = "_start_max", type = Integer.class),
    @StoredProcedureParameter(mode = ParameterMode.IN, name = "_end", type = Integer.class),
    @StoredProcedureParameter(mode = ParameterMode.IN, name = "_end_min", type = Integer.class),
    @StoredProcedureParameter(mode = ParameterMode.IN, name = "_end_max", type = Integer.class),
    @StoredProcedureParameter(mode = ParameterMode.IN, name = "_chromosome", type = String.class),
    @StoredProcedureParameter(mode = ParameterMode.IN, name = "_reference_bases", type = String.class),
    @StoredProcedureParameter(mode = ParameterMode.IN, name = "_alternate_bases", type = String.class),
    @StoredProcedureParameter(mode = ParameterMode.IN, name = "_reference_genome", type = String.class),
    @StoredProcedureParameter(mode = ParameterMode.IN, name = "_dataset_ids", type = String.class),
    @StoredProcedureParameter(mode = ParameterMode.IN, name = "_filters", type = String.class)})
public class BeaconDataSummary {

  private static final long serialVersionUID = 1L;

  @Id
  private String id;

  @Column(name = "dataset_id")
  private Integer datasetId;

  @Column(name = "variant_cnt")
  private Integer variantCnt;

  @Column(name = "call_cnt")
  private Integer callCnt;

  @Column(name = "sample_cnt")
  private Integer sampleCnt;

  @Column(name = "frequency")
  private BigDecimal frequency;

  @Column(name = "num_variants")
  private Integer numVariants;


}
