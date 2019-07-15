package org.ega_archive.elixirbeacon.repository.elixirbeacon;

import java.util.List;
import org.ega_archive.elixirbeacon.model.elixirbeacon.BeaconDataSummary;
import org.ega_archive.elixircore.repository.CustomQuerydslJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BeaconSummaryDataRepository extends
    CustomQuerydslJpaRepository<BeaconDataSummary, Integer> {

  @Query(value = "SELECT * FROM query_data_summary_response("
      + "CAST(:_variant_type AS text), "
      + "CAST(CAST(:_start AS text) AS integer), "
      + "CAST(CAST(:_start_min AS text) AS integer), "
      + "CAST(CAST(:_start_max AS text) AS integer), "
      + "CAST(CAST(:_end AS text) AS integer), "
      + "CAST(CAST(:_end_min AS text) AS integer), "
      + "CAST(CAST(:_end_max AS text) AS integer), "
      + "CAST(:_chromosome AS varchar), "
      + "CAST(:_reference_bases AS text), "
      + "CAST(:_alternate_bases AS text), "
      + "CAST(:_reference_genome AS text), "
      + "CAST(:_dataset_ids AS text))", nativeQuery = true)
  @Transactional(transactionManager = "elixirbeaconTransactionManager", readOnly = true)
  List<BeaconDataSummary> searchForVariantsQuery(
      @Param("_variant_type") String variantType,
      @Param("_start") Integer start,
      @Param("_start_min") Integer startMin,
      @Param("_start_max") Integer startMax,
      @Param("_end") Integer end,
      @Param("_end_min") Integer endMin,
      @Param("_end_max") Integer endMax,
      @Param("_chromosome") String chrom,
      @Param("_reference_bases") String refBases,
      @Param("_alternate_bases") String altBases,
      @Param("_reference_genome") String refGenome,
      @Param("_dataset_ids") String datasetIds);

}
