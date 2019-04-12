package org.ega_archive.elixirbeacon.service.opencga;

import org.opencb.opencga.core.models.Project;
import org.opencb.opencga.core.models.Study;

public interface StudyVisitor {

	void visit(Project project, Study study);

}
