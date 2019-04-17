package org.ega_archive.elixirbeacon.service.opencga;

class OpencgaDatasetId {

	public long getProjectId() {
		return projectId;
	}

	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}

	public long getStudyId() {
		return studyId;
	}

	public void setStudyId(long studyId) {
		this.studyId = studyId;
	}

	public static String translateOpencgaToBeacon(long projectId, long studyId) {
		return String.format("DATASET:%s:%s", projectId, studyId);
	}

	public static OpencgaDatasetId translateBeaconToOpencga(String datasetId) {
		if (datasetId.startsWith("DATASET:")) {
			String[] fields = datasetId.split(":", 3);
			if (3 == fields.length) {
				try {
					long projectId = Long.parseLong(fields[1]);
					long studyId = Long.parseLong(fields[2]);
					return new OpencgaDatasetId(projectId, studyId);
				} catch (NumberFormatException exc) {
					return null;
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	private OpencgaDatasetId(long projectId, long studyId) {
		this.projectId = projectId;
		this.studyId = studyId;
	}

	private long projectId;
	private long studyId;
}