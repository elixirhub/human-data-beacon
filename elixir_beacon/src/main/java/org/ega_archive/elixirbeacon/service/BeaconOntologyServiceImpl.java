package org.ega_archive.elixirbeacon.service;

import lombok.extern.slf4j.Slf4j;
import org.ega_archive.elixirbeacon.constant.CsvsConstants;
import org.ega_archive.elixirbeacon.convert.Operations;
import org.ega_archive.elixirbeacon.dto.BeaconOntology;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BeaconOntologyServiceImpl implements BeaconOntologyService {

  @Value("${ontology.terms.default.yaml.filename}")
  private String defaultOntologyTermFileName;

  @Override
  public BeaconOntology listFilteringTerms() {

    BeaconOntology result = Operations.convertToBeaconOntologyTerm(CsvsConstants.CSVS_ONTOLOGY_TERMS);
    return result;
  }
}
