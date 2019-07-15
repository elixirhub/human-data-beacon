package org.ega_archive.elixirbeacon.repository.elixirbeacon;

import java.util.List;

import org.ega_archive.elixirbeacon.model.elixirbeacon.BeaconDatasetConsentCode;
import org.ega_archive.elixircore.repository.CustomQuerydslJpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BeaconDatasetConsentCodeRepository extends CustomQuerydslJpaRepository<BeaconDatasetConsentCode, String> {

  @Query("SELECT c FROM BeaconDatasetConsentCode c WHERE c.id.datasetId=?1")
  public List<BeaconDatasetConsentCode> findByDatasetId(Integer datasetId);
  
}
