package org.ega_archive.elixirbeacon.controller;

import org.ega_archive.elixirbeacon.dto.BeaconOntology;
import org.ega_archive.elixirbeacon.service.BeaconOntologyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/beacon")
public class BeaconOntologyController {

  @Autowired
  private BeaconOntologyService beaconOntologyService;
  

  @GetMapping(value = "/filtering_terms")
  public BeaconOntology listOntologies() {

    return beaconOntologyService.listFilteringTerms();
  }
  
}
