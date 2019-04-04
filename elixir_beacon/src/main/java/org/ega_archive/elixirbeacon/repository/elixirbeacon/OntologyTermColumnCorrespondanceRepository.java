package org.ega_archive.elixirbeacon.repository.elixirbeacon;

import org.ega_archive.elixirbeacon.model.elixirbeacon.OntologyTermColumnCorrespondance;
import org.ega_archive.elixircore.repository.CustomQuerydslJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OntologyTermColumnCorrespondanceRepository extends
    CustomQuerydslJpaRepository<OntologyTermColumnCorrespondance, Integer> {

  OntologyTermColumnCorrespondance findByOntologyAndTerm(String ontology, String term);

}
