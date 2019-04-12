package org.ega_archive.elixirbeacon.service.opencga;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.ega_archive.elixirbeacon.dto.Dataset;
import org.ega_archive.elixirbeacon.dto.KeyValuePair;
import org.opencb.opencga.client.rest.OpenCGAClient;
import org.opencb.opencga.core.models.Project;
import org.opencb.opencga.core.models.Study;

public class DatasetLister implements StudyVisitor {

	private final List<Dataset> datasets = new ArrayList<Dataset>();

	@Override
	public void visit(Project project, Study study) {
		Dataset dataset = new Dataset();
		String datasetId = OpencgaDatasetId.translateOpencgaToBeacon(project.getId(), study.getId());
		dataset.setId(datasetId);
		dataset.setName(project.getName() + " : " + study.getName());
		dataset.setDescription(study.getDescription());
		dataset.setAssemblyId(project.getOrganism().getAssembly());
		dataset.setCreateDateTime(OpencgaUtils.translateOpencgaDate(project.getCreationDate()));
		dataset.setUpdateDateTime(OpencgaUtils.translateOpencgaDate(project.getLastModified()));
		
		List<KeyValuePair> info = new ArrayList<KeyValuePair>();
		info.add(new KeyValuePair("stats", "stats"));
		// info.put("stats", study.getStats());
		dataset.setInfo(info);

		datasets.add(dataset);
	}

	public List<Dataset> getDatasets() {
		return datasets;
	}

}
