package org.ega_archive.elixirbeacon.service.opencga;

import org.opencb.opencga.core.models.Individual;
import org.opencb.opencga.core.models.Sample;

import java.security.InvalidParameterException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Filter {

    private final Set<String> icd10s = new HashSet<>();
    private final Set<String> hpos = new HashSet<>();
    private Individual.Sex sex;
    private Individual.KaryotypicSex karyotypicSex;

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

    public static Filter parse(List<String> filters) {
        return new Filter(filters);
    }

    private Filter(List<String> filters) {
        for (String filter : filters) {
            if ("PATO:0000383".equals(filter)) {
                if (Objects.nonNull(sex)) {
                    throw new InvalidParameterException(filter);
                }
                sex = Individual.Sex.FEMALE;
            } else if ("PATO:0000384".equals(filter)) {
                if (Objects.nonNull(sex)) {
                    throw new InvalidParameterException(filter);
                }
                sex = Individual.Sex.MALE;
            } else if ("PATO:0020001".equals(filter)) {
                if (Objects.nonNull(karyotypicSex)) {
                    throw new InvalidParameterException(filter);
                }
                karyotypicSex = Individual.KaryotypicSex.XY;
            } else if ("PATO:0020002".equals(filter)) {
                if (Objects.nonNull(karyotypicSex)) {
                    throw new InvalidParameterException(filter);
                }
                karyotypicSex = Individual.KaryotypicSex.XX;
            } else if (filter.startsWith("HP:")) {
                hpos.add(filter);
            } else if (filter.startsWith("ICD10:")) {
                icd10s.add(filter.split(":", 2)[1]);
            }
        }
    }
}
