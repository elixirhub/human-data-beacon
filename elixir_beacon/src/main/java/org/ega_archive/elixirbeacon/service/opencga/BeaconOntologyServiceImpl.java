package org.ega_archive.elixirbeacon.service.opencga;

import org.ega_archive.elixirbeacon.dto.BeaconOntology;
import org.ega_archive.elixirbeacon.dto.BeaconOntologyTerm;
import org.ega_archive.elixirbeacon.service.BeaconOntologyService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class BeaconOntologyServiceImpl implements BeaconOntologyService {

    // HACK: we should be using complete HPO and ICD10, which is what opencga supports
    // but J asked for this for the demo

    private static String[] supportedHpos = new String[]{
            "HP:0030853",
            "HP:0002313",
            "HP:0001638",
            "HP:0001511",
            "HP:0004322",
            "HP:0001250",
            "HP:0000488",
            "HP:0004482",
            "HP:0200134",
            "HP:0000252",
            "HP:0009830",
            "HP:0010864",
            "HP:0002060",
            "HP:0002376",
            "HP:0001249",
            "HP:0001290",
            "HP:0001298",
            "HP:0002926",
            "HP:0002342",
            "HP:0001256",
            "HP:0011343",
            "HP:0000256",
            "HP:0007105",
            "HP:0000729",
            "HP:0000118",
            "HP:0001328",
            "HP:0000525",
            "HP:0008897",
            "HP:0002187",
            "HP:0001263",
            "HP:0001871",
            "HP:0011342"
    };

    private static String[] supportedIcd10s = new String[]{
            "Q89",
            "G82",
            "I42",
            "P05",
            "R62",
            "G40",
            "H35",
            "Q75",
            "G93",
            "Q02",
            "G62",
            "F72",
            "Q04",
            "F82",
            "F79",
            "P94",
            "E07",
            "F71",
            "F70",
            "F89",
            "F84",
            "Q87",
            "H21",
            "FR62",
            "F73",
            "D61"
    };


    private static String[] supportedPatos = new String[] {
            "PATO:0000383", "PATO:0000384", "PATO:0020001", "PATO:0020002"
    };


    @Override
    public BeaconOntology listOntologyTerms() {
        BeaconOntology response = new BeaconOntology();
        List<BeaconOntologyTerm> ontologyTerms = new ArrayList<>();
        // pato
        for (String id : supportedPatos) {
            BeaconOntologyTerm term = new BeaconOntologyTerm();
            term.setOntology("PATO");
            term.setTerm(id);
            ontologyTerms.add(term);
        }
        // hpo
        for (String phenotype : supportedHpos) {
            BeaconOntologyTerm term = new BeaconOntologyTerm();
            term.setOntology("HPO");
            term.setTerm(phenotype);
            ontologyTerms.add(term);
        }
        // icd10
        for (String phenotype : supportedIcd10s) {
            BeaconOntologyTerm term = new BeaconOntologyTerm();
            term.setOntology("ICD10");
            term.setTerm(phenotype);
            ontologyTerms.add(term);
        }
        response.setOntologyTerms(ontologyTerms);
        return response;
    }
}
