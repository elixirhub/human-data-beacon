package org.ega_archive.elixirbeacon.service.opencga;

import org.opencb.opencga.core.models.Individual;
import org.opencb.opencga.core.models.Sample;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Filter {

    private final Set<String> icd10s = new HashSet<String>();
    private final Set<String> hpos = new HashSet<String>();
    private Individual.Sex sex;
    private Individual.KaryotypicSex karyotypicSex;
    private boolean valid = true;

    public boolean isValid() {
        return valid;
    }

    public Set<String> getIcd10s() {
        return icd10s;
    }

    public Set<String> getHpos() {
        return hpos;
    }

    public Individual.Sex getSex() {
        return sex;
    }

    public Filter setSex(Individual.Sex sex) {
        this.sex = sex;
        return this;
    }

    public Individual.KaryotypicSex getKaryotypicSex() {
        return karyotypicSex;
    }

    public Filter setKaryotypicSex(Individual.KaryotypicSex kariotypicSex) {
        this.karyotypicSex = kariotypicSex;
        return this;
    }

    public boolean needsSample() {
        return !hpos.isEmpty() || !icd10s.isEmpty();
    }

    public boolean filterSample(Sample sample) {
        return filterByHpo(sample) && filterByIcd10(sample);
    }

    public boolean needsIndividual() {
        return (null != sex || null != karyotypicSex);
    }

    public boolean filterIndividual(Individual individual) {
        return (sex == null || sex == individual.getSex())
                && (null == karyotypicSex || karyotypicSex == individual.getKaryotypicSex());
    }



    public static Filter parse(List<String> filters) {
        return new Filter(filters);
    }

    private boolean filterByPhenotype(Sample sample, String ontology, Set<String> filter) {
        if (filter.isEmpty()) {
            return true;
        } else {
            Set<String> samplePhenotypes = sample.getPhenotypes().stream()
                    .filter(phenotype -> ontology.equals(phenotype.getSource()))
                    .map(phenotype -> phenotype.getId())
                    .collect(Collectors.toSet());
            return samplePhenotypes.containsAll(filter);
        }
    }

    private boolean filterByHpo(Sample sample) {
        return filterByPhenotype(sample, "HPO", hpos);
    }

    private boolean filterByIcd10(Sample sample) {
        return filterByPhenotype(sample, "ICD10", icd10s);
    }

    private Filter(List<String> filters) {
        for (String filter : filters) {
            if ("PATO:0000383".equals(filter)) {
                valid = (null == sex);
                sex = Individual.Sex.FEMALE;
            } else if ("PATO:0000384".equals(filter)) {
                valid = (null == sex);
                sex = Individual.Sex.MALE;
            } else if ("PATO:0020001".equals(filter)) {
                valid = (null == karyotypicSex);
                karyotypicSex = Individual.KaryotypicSex.XY;
            } else if ("PATO:0020002".equals(filter)) {
                valid = (null == karyotypicSex);
                karyotypicSex = Individual.KaryotypicSex.XX;
            } else if (filter.startsWith("HP:")) {
                hpos.add(filter);
            } else if (filter.startsWith("ICD10:")) {
                icd10s.add(filter.split(":", 2)[1]);
            }
        }
    }
}
