package org.ega_archive.elixirbeacon.repository.elixirbeacon;

import org.ega_archive.elixirbeacon.model.elixirbeacon.OntologyTerm;
import org.ega_archive.elixircore.repository.CustomQuerydslJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OntologyTermRepository extends CustomQuerydslJpaRepository<OntologyTerm, Integer> {

}
