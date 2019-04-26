package org.ega_archive.elixirbeacon.service.opencga;

import org.ega_archive.elixirbeacon.dto.Dataset;
import org.ega_archive.elixirbeacon.dto.KeyValuePair;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.stats.VariantStats;
import org.opencb.commons.datastore.core.Query;
import org.opencb.commons.datastore.core.QueryOptions;
import org.opencb.opencga.core.models.Project;
import org.opencb.opencga.core.models.Study;
import org.opencb.opencga.core.models.summaries.StudySummary;
import org.opencb.opencga.core.results.VariantQueryResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DatasetLister implements StudyVisitor {

	private final OpencgaEnrichedClient opencga;
	private final List<Dataset> datasets = new ArrayList<>();

	DatasetLister(OpencgaEnrichedClient opencga) {
		this.opencga = opencga;
	}

	@Override
	public void visit(Project project, Study study) {

		Dataset dataset = new Dataset();
		String datasetId = OpencgaDatasetId.translateOpencgaToBeacon(project.getId(), study.getId());
		dataset.setId(datasetId);
		dataset.setName(project.getName() + " : " + study.getName());
		dataset.setDescription(study.getDescription());
		dataset.setAssemblyId(project.getOrganism().getAssembly());
		dataset.setCreateDateTime(OpencgaUtils.translateOpencgaDate(project.getCreationDate()));
		dataset.setAssemblyId(project.getOrganism().getAssembly());
		if (!opencga.isAccessAnonymous()) {
			dataset.setUpdateDateTime(OpencgaUtils.translateOpencgaDate(project.getLastModified()));
//			Map<String, Object> stats = study.getStats();
//			List<KeyValuePair> info = new ArrayList<KeyValuePair>();
//			for (String statsKey : stats.keySet()) {
//				info.add(new KeyValuePair(statsKey, stats.get(statsKey).toString()));
//			}
//			dataset.setInfo(info);
		}
		try {
			StudySummary summary = getStudySummary(opencga, project, study);
			dataset.setSampleCount(summary.getSamples());
		} catch (IOException ignored) {
		}
		datasets.add(dataset);
	}

	private static VariantStats getStudyStats(OpencgaEnrichedClient opencga, Project project, Study study, String variantId) throws IOException {
		Query query = new Query();
		query.put("study", getStudyFqn(project, study));
		query.put("id", variantId);
		VariantQueryResult<Variant> result = opencga.getVariantClient().query2(query, QueryOptions.empty());
		if (org.apache.commons.lang3.StringUtils.isNotBlank(result.getErrorMsg())) {
			throw new IOException(result.getErrorMsg());
		} else if (0 == result.getNumResults()) {
			throw new IOException("no-study-stats");
		} else {
			Variant variant = result.first();
			return variant.getStudies().get(0).getStats("ALL");
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

	public List<Dataset> getDatasets() {
		return datasets;
	}

}
