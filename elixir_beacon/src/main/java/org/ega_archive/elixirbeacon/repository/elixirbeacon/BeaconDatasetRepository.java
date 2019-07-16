package org.ega_archive.elixirbeacon.repository.elixirbeacon;

import java.util.List;
import org.ega_archive.elixirbeacon.model.elixirbeacon.BeaconDataset;
import org.ega_archive.elixircore.repository.CustomQuerydslJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BeaconDatasetRepository extends
    CustomQuerydslJpaRepository<BeaconDataset, Integer> {
  
  Page<BeaconDataset> findByReferenceGenome(String referenceGenome, Pageable page);
  
  BeaconDataset findByStableId(String stableId);
  
  @Query("SELECT id FROM BeaconDataset WHERE upper(accessType)=upper(:accessType) AND lower(referenceGenome) = lower(:referenceGenome)")
  List<Integer> findIdsByReferenceGenomeAndAccessType(
      @Param("referenceGenome") String referenceGenome, @Param("accessType") String accessType);

  @Query("SELECT id FROM BeaconDataset WHERE stableId IN :stableIds AND lower(referenceGenome) = lower(:referenceGenome)")
  List<Integer> findIdsByStableIdInAndReferenceGenome(@Param("stableIds") List<String> stableIds,
      @Param("referenceGenome") String referenceGenome);

}
