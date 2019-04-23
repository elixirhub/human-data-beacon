package org.ega_archive.elixirbeacon.service.opencga;

import org.opencb.opencga.core.models.Project;
import org.opencb.opencga.core.models.Study;

import java.util.HashMap;
import java.util.Map;

public class DatasetAccessLevelVisitor implements StudyVisitor {

	private final Map<String, Object> datasetAccessLevels = new HashMap<>();

	@Override
	public void visit(Project project, Study study) {

		String datasetId = OpencgaDatasetId.translateOpencgaToBeacon(project.getId(), study.getId());
		datasetAccessLevels.put(datasetId, "PUBLIC");
	}

	public Map<String, Object> getDatasetAccessLevels() {
		return datasetAccessLevels;
	}

}
