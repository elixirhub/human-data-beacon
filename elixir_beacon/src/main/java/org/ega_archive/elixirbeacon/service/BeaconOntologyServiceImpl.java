package org.ega_archive.elixirbeacon.service;

import java.util.List;
import java.util.stream.Collectors;
import org.ega_archive.elixirbeacon.convert.Operations;
import org.ega_archive.elixirbeacon.dto.BeaconOntology;
import org.ega_archive.elixirbeacon.model.elixirbeacon.OntologyTerm;
import org.ega_archive.elixirbeacon.repository.elixirbeacon.OntologyTermRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BeaconOntologyServiceImpl implements BeaconOntologyService {

  @Autowired
  private OntologyTermRepository ontologyTermRepository;

  @Override
  public BeaconOntology listOntologyTerms() {
    List<OntologyTerm> all = ontologyTermRepository.findAll();
    BeaconOntology result = Operations.convertToBeaconOntologyTerm(all);
    return result;
  }
}
