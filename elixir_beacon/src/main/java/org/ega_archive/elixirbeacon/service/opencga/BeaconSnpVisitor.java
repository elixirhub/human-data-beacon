package org.ega_archive.elixirbeacon.service.opencga;

import org.apache.commons.lang.StringUtils;
import org.ega_archive.elixirbeacon.dto.DatasetAlleleResponse;
import org.ega_archive.elixirbeacon.dto.Error;
import org.ega_archive.elixirbeacon.enums.ErrorCode;
import org.opencb.commons.datastore.core.Query;
import org.opencb.commons.datastore.core.QueryOptions;
import org.opencb.commons.datastore.core.QueryResponse;
import org.opencb.commons.utils.ListUtils;
import org.opencb.opencga.catalog.db.api.IndividualDBAdaptor;
import org.opencb.opencga.core.models.Individual;
import org.opencb.opencga.core.models.Project;
import org.opencb.opencga.core.models.Sample;
import org.opencb.opencga.core.models.Study;
import org.opencb.opencga.core.models.summaries.StudySummary;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BeaconSnpVisitor implements StudyVisitor {

    private final OpencgaEnrichedClient opencga;
    private final String variantId;
    private final Filter filter;

    private final List<DatasetAlleleResponse> results = new ArrayList<>();

    public BeaconSnpVisitor(OpencgaEnrichedClient opencga, String variantId, Filter filter) {
        this.opencga = opencga;
        this.variantId = variantId;
        this.filter = filter;
    }

    private List<String> filterStudySamples(Project project, Study study) throws IOException {
        Query query = new Query();
        query.put(IndividualDBAdaptor.QueryParams.STUDY.key(), getStudyFqn(project, study));
        query.putIfNotNull(IndividualDBAdaptor.QueryParams.SEX.key(), filter.getSex());
        query.putIfNotNull(IndividualDBAdaptor.QueryParams.KARYOTYPIC_SEX.key(), filter.getKaryotypicSex());

        List<String> phenotypes = new ArrayList<>();
        phenotypes.addAll(filter.getHpos());
        phenotypes.addAll(filter.getIcd10s());
        if (ListUtils.isNotEmpty(phenotypes)) {
            query.put(IndividualDBAdaptor.QueryParams.PHENOTYPES.key(), String.join(",", phenotypes));
        }
        QueryResponse<Individual> response = opencga.getIndividualClient().search(query, QueryOptions.empty());
        if (StringUtils.isNotBlank(response.getError())) {
            throw new IOException(response.getError());
        } else {
            // TODO: we should be paginating here, but we know for ENOD this is _currently_ not needed
            List<Individual> individuals = response.allResults();
            List<String> samples = new ArrayList<>();
            for (Individual individual : individuals) {
                List<String> individualSamples = individual.getSamples().stream().map(Sample::getName).collect(Collectors.toList());
                samples.addAll(individualSamples);
            }
            return samples;
        }
    }

    @Override
    public void visit(Project project, Study study) {

        DatasetAlleleResponse result = new DatasetAlleleResponse();
        result.setDatasetId(OpencgaDatasetId.translateOpencgaToBeacon(project.getId(), study.getId()));

        try {
            StudySummary studySummary = getStudySummary(opencga, project, study);
            if (0 == studySummary.getSamples()) {
                result.setExists(false);
            } else {
                // magic
                // HACK: we're assuming only 0/0, 0/1, 1/1 exists, and nothing else
                // barely good for a demo, intolerable for production code
                // but we are just changing requisites too much to spend more time here; wait until they stabilize
                long datasetCount01 = countSamplesWithVariantGenotype(opencga, project, study, variantId, "0/1");
                long datasetCount11 = countSamplesWithVariantGenotype(opencga, project, study, variantId, "1/1");
                long datasetSampleCount = datasetCount01 + datasetCount11;
                double datasetFrequency = (double)(datasetCount01 + 2*datasetCount11) / (double)(2 * studySummary.getSamples());

                result.setVariantCount(1L);
                result.setSampleCount(datasetSampleCount);
                result.setFrequency(new BigDecimal(datasetFrequency));
                result.setInfo(new HashMap<>());

                if (Objects.isNull(filter)) {
                    result.setExists(0 < datasetSampleCount);
                } else {
                    List<String> selection = filterStudySamples(project, study);
                    if (selection.isEmpty()) {
                        result.setExists(false);
                    } else {
                        long selectionCount01 = countSamplesWithVariantGenotype(opencga, project, study, variantId, "0/1", selection);
                        long selectionCount11 = countSamplesWithVariantGenotype(opencga, project, study, variantId, "1/1", selection);
                        long selectionSampleCount = selectionCount01 + selectionCount11;
                        double selectionFrequency = (double)(selectionCount01 + 2 * selectionCount11) / (double)(2 * selection.size());
                        result.setExists(0 < selectionSampleCount);
                        result.getInfo().put("filteredSampleCount", selectionSampleCount);
                        result.getInfo().put("filteredFrequency", selectionFrequency);
                    }
                }
            }
        } catch (IOException e) {
            Error error = new Error();
            error.setErrorCode(ErrorCode.GENERIC_ERROR);
            error.setMessage(e.getMessage());
            result.setError(error);
        }

        results.add(result);
    }

    public List<DatasetAlleleResponse> getResults() {
        return results;
    }

    private static long countSamplesWithVariantGenotype(OpencgaEnrichedClient opencga, Project project, Study study, String variantId, String genotype) throws IOException {
        return countSamplesWithVariantGenotype(opencga, project, study, variantId, genotype, null);
    }

    private static long countSamplesWithVariantGenotype(OpencgaEnrichedClient opencga, Project project, Study study, String variantId, String genotype, List<String> samples) throws IOException {
        Query query = new Query();
        query.put("study", getStudyFqn(project, study));
        query.put("id", variantId);
        query.put("genotypes", genotype);
        query.put("all", false);
        if (Objects.nonNull(samples)) {
            query.put("samples", String.join(",", samples));
        }
        QueryResponse<Sample> response = opencga.getBeaconClient().getSamplesWithVariant(query, QueryOptions.empty());
        if (org.apache.commons.lang3.StringUtils.isNotBlank(response.getError())) {
            throw new IOException(response.getError());
        } else {
            return response.allResultsSize();
        }
    }

    private static StudySummary getStudySummary(OpencgaEnrichedClient opencga, Project project, Study study) throws IOException {
        String studyId = getStudyFqn(project, study);
        StudySummary summary = opencga.getStudyClient().getSummary(studyId, QueryOptions.empty()).firstResult();
        if (Objects.nonNull(summary)) {
            return summary;
        } else {
            throw new IOException("cannot retrieve study summary");
        }
    }

    private static String getStudyFqn(Project project, Study study) {
        return String.format("%s:%s", project.getAlias(), study.getAlias());
    }
}
