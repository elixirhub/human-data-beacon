package org.ega_archive.elixirbeacon.service.opencga;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.ega_archive.elixirbeacon.dto.DatasetAlleleResponse;
import org.ega_archive.elixirbeacon.dto.Error;
import org.ega_archive.elixirbeacon.enums.ErrorCode;
import org.mortbay.log.Log;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.stats.VariantStats;
import org.opencb.commons.datastore.core.ObjectMap;
import org.opencb.commons.datastore.core.Query;
import org.opencb.commons.datastore.core.QueryOptions;
import org.opencb.commons.datastore.core.QueryResponse;
import org.opencb.opencga.catalog.db.api.StudyDBAdaptor;
import org.opencb.opencga.client.rest.OpenCGAClient;
import org.opencb.opencga.core.models.Project;
import org.opencb.opencga.core.models.Study;
import org.opencb.opencga.core.results.VariantQueryResult;

public class BeaconSnpVisitorWithoutFilter implements StudyVisitor {

    private final OpenCGAClient opencga;
    private final Query queryTemplate;
    private final List<DatasetAlleleResponse> results = new ArrayList<DatasetAlleleResponse>();

    public BeaconSnpVisitorWithoutFilter(OpenCGAClient opencga, Query query) {
        this.opencga = opencga;
        this.queryTemplate = query;
    }

    @Override
    public void visit(Project project, Study study) {

        String studyId = project.getAlias() + ":" + study.getAlias();
        Query query = new Query(queryTemplate);
        query.put("study", studyId);

        QueryOptions options = QueryOptions.empty();
        DatasetAlleleResponse studyResult = new DatasetAlleleResponse();
        try {

            VariantQueryResult<Variant> result = opencga.getVariantClient().query2(query, options);
            studyResult.setDatasetId(OpencgaDatasetId.translateOpencgaToBeacon(project.getId(), study.getId()));
            boolean variantFound = 0 < result.getNumResults() && 0 < result.getResult().size();
            studyResult.setExists(variantFound);
            if (0 < result.getNumResults() && 0 < result.getResult().size()) {

                List<StudyEntry> studies = result.getResult().get(0).getStudies();
                if (0 < studies.size()) {
                    StudyEntry studyEntry = studies.get(0);
                    Map<String, VariantStats> stats = studyEntry.getStats();
                    VariantStats statsAll = stats.get("ALL");
                    studyResult.setSampleCount((long) statsAll.getAltAlleleCount());
                    studyResult.setFrequency(new BigDecimal(statsAll.getAltAlleleFreq()));
                    studyResult.setVariantCount(1L);
                    Map<String, Object> info = new HashMap<String, Object>();
                    info.put("stats", statsAll);
                    studyResult.setInfo(info);
                }
            }
        } catch (IOException e) {
            Error error = new Error();
            error.setErrorCode(ErrorCode.GENERIC_ERROR);
            error.setMessage(e.getMessage());
            studyResult.setError(error);
        }
        results.add(studyResult);
    }

    public List<DatasetAlleleResponse> getResults() {
        return results;
    }

}
