package org.ega_archive.elixirbeacon.service.opencga;

import org.apache.commons.lang.StringUtils;
import org.ega_archive.elixirbeacon.dto.DatasetAlleleResponse;
import org.ega_archive.elixirbeacon.dto.Error;
import org.ega_archive.elixirbeacon.enums.ErrorCode;
import org.mortbay.log.Log;
import org.opencb.commons.datastore.core.Query;
import org.opencb.commons.datastore.core.QueryOptions;
import org.opencb.commons.datastore.core.QueryResponse;
import org.opencb.commons.utils.ListUtils;
import org.opencb.opencga.core.models.Individual;
import org.opencb.opencga.core.models.Project;
import org.opencb.opencga.core.models.Sample;
import org.opencb.opencga.core.models.Study;
import org.opencb.opencga.core.models.summaries.StudySummary;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BeaconSnpVisitorWithFilter implements StudyVisitor {

    private final OpencgaEnrichedClient opencga;
    private final Filter filter;
    private final Query queryTemplate;
    private final List<DatasetAlleleResponse> results = new ArrayList<DatasetAlleleResponse>();

    public BeaconSnpVisitorWithFilter(OpencgaEnrichedClient opencga, Query query, Filter filter) {
        this.opencga = opencga;
        this.queryTemplate = query;
        this.filter = filter;
    }

    private static String getSampleIds(List<Sample> samples) {
        return samples.stream().map(sample -> sample.getName()).collect(Collectors.joining(","));
    }

    @Override
    public void visit(Project project, Study study) {

        String studyId = project.getAlias() + ":" + study.getAlias();

        Query query = new Query(queryTemplate);
        query.put("study", studyId);
        QueryOptions queryOptions = QueryOptions.empty();

        DatasetAlleleResponse result = new DatasetAlleleResponse();
        result.setDatasetId(OpencgaDatasetId.translateOpencgaToBeacon(project.getId(), study.getId()));

        String errorMsg = "";
        StudySummary studySummary = null;
        List<Sample> samples = null;
        try {
            studySummary = getStudySummary(studyId);
            // opencga bug: if a study has no samples, a search for variants fails
            // mistaking the study ID for its name
            if (0 == studySummary.getSamples()) {
                samples = new ArrayList<>();
            } else {
                QueryResponse<Sample> response = opencga.getBeaconClient().getSamplesWithVariant(query, queryOptions);
                errorMsg = response.getError();
                Log.debug("> response.error = {}", response.getError());
                if (StringUtils.isBlank(errorMsg)) {
                    samples = response.allResults();
                    samples = filterSamples(project, study, samples);
                }
            }
        } catch (IOException e) {
            errorMsg = e.getMessage();
        }
        if (StringUtils.isNotBlank(errorMsg)) {
            Error error = new Error();
            error.setErrorCode(ErrorCode.GENERIC_ERROR);
            error.setMessage(errorMsg);
            result.setError(error);
        } else {
            boolean variantPresent = ListUtils.isNotEmpty(samples);
            result.setExists(variantPresent);
            if (variantPresent) {
                result.setSampleCount((long) samples.size());
                result.setVariantCount(1L);
                // TODO: if we have a token
                double frequency = (double)samples.size() / (double)studySummary.getSamples();
                result.setFrequency(new BigDecimal(frequency));
                // result.setExternalUrl();
                // result.setInfo();
                // result.setNote();
            }
        }
        results.add(result);
    }

    public List<DatasetAlleleResponse> getResults() {
        return results;
    }

    private StudySummary getStudySummary(String studyId) throws IOException {
        StudySummary summary = opencga.getStudyClient().getSummary(studyId, QueryOptions.empty()).firstResult();
        if (null != summary) {
            return summary;
        } else {
            throw new IOException("cannot retrieve study summary");
        }
    }

    private List<Sample> filterByHpo(List<Sample> samples) {
        if (ListUtils.isEmpty(filter.getHpos())) {
            return samples;
        } else {
            List<Sample> selection = new ArrayList();
            for (Sample sample : samples) {
                // samples should have all HPOs; we currently only support AND
                Set<String> sampleHpos = sample.getPhenotypes().stream()
                        .filter(phenotype -> "HPO".equals(phenotype.getSource()))
                        .map(hpo -> hpo.getId())
                        .collect(Collectors.toSet());
                boolean valid = sampleHpos.containsAll(filter.getHpos());
                if (valid) {
                    selection.add(sample);
                }
            }
            return selection;
        }
    }

    private List<Sample> filterByIcd10(List<Sample> samples) {
        if (ListUtils.isEmpty(filter.getIcd10s())) {
            return samples;
        } else {
            List<Sample> selection = new ArrayList();
            for (Sample sample : samples) {
                // samples should have all ICD-10s; we currently only support AND
                Set<String> sampleIcd10s = sample.getPhenotypes().stream()
                        .filter(phenotype -> "ICD10".equals(phenotype.getSource()))
                        .map(icd10 -> icd10.getId())
                        .collect(Collectors.toSet());
                boolean valid = sampleIcd10s.containsAll(filter.getIcd10s());
                if (valid) {
                    selection.add(sample);
                }
            }
            return selection;
        }
    }

    private List<Sample> filterBySample(List<Sample> samples) {
        if (!filter.needsSample()) {
            return samples;
        } else {
            List<Sample> selection = new ArrayList<Sample>();
            for (Sample sample : samples) {
                if (filter.filterSample(sample)) {
                    selection.add(sample);
                }
            }
            return selection;
        }
    }


    private List<Sample> filterByIndividual(Project project, Study study, List<Sample> samples) throws IOException {
        if (ListUtils.isEmpty(samples) || !filter.needsIndividual()) {
            return samples;
        } else {
            // query our samples' inidividuals here
            String studyId = project.getAlias() + ":" + study.getAlias();
            List<Long> sampleIds = samples.stream().map(sample -> sample.getId()).collect(Collectors.toList());
            String paramSampleIds = sampleIds.stream().map(sampleId -> sampleId.toString()).collect(Collectors.joining(","));

            Query query = new Query();
            query.put("study", studyId);
            query.put("samples", paramSampleIds);
            query.putIfNotNull("karyotypicSex", filter.getKaryotypicSex());
            query.putIfNotNull("sex", filter.getSex());
            QueryOptions queryOptions = QueryOptions.empty();

            QueryResponse<Individual> response = opencga.getIndividualClient().search(query, queryOptions);
            if (StringUtils.isNotBlank(response.getError())) {
                throw new IOException(response.getError());
            } else {
                Set<Long> individualSamples = new HashSet<>();
                List<Individual> individuals = response.allResults();
                for (Individual individual : individuals) {
                    individualSamples.addAll(individual.getSamples().stream().map(sample -> sample.getId()).collect(Collectors.toSet()));
                }
                List<Sample> selection = new ArrayList<>();
                for (Sample sample : samples) {
                    if (individualSamples.contains(sample.getId())) {
                        selection.add(sample);
                    }
                }
                return selection;
            }
        }
    }

    private List<Sample> filterSamples(Project project, Study study, List<Sample> samples) throws IOException {
        List<Sample> selection = filterBySample(samples);
        selection = filterByIndividual(project, study, selection);
        return selection;
    }
}
