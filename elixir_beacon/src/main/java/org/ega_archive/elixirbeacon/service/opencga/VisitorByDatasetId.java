package org.ega_archive.elixirbeacon.service.opencga;

import java.util.List;
import java.util.stream.Collectors;

import org.opencb.commons.utils.ListUtils;
import org.opencb.opencga.core.models.Project;
import org.opencb.opencga.core.models.Study;

public class VisitorByDatasetId implements StudyVisitor {

	private final List<OpencgaDatasetId> datasetIds;
	private final StudyVisitor baseVisitor;

	public VisitorByDatasetId(List<String> datasetIds, StudyVisitor baseVisitor) {
		this.baseVisitor = baseVisitor;
		this.datasetIds = ListUtils.isEmpty(datasetIds) ? null : datasetIds.stream().map(datasetId -> OpencgaDatasetId.translateBeaconToOpencga(datasetId))
				.filter(datasetId -> null != datasetId).collect(Collectors.toList());
	}

	@Override
	public void visit(Project project, Study study) {		
		boolean valid = ListUtils.isEmpty(datasetIds) || this.datasetIds.stream().anyMatch(
				studyId -> (studyId.getProjectId() == project.getId() && studyId.getStudyId() == study.getId()));
		if (valid) {
			this.baseVisitor.visit(project, study);
		}
	}
}
