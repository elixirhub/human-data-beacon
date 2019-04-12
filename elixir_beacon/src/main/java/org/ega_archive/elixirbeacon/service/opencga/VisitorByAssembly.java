package org.ega_archive.elixirbeacon.service.opencga;

import org.apache.commons.lang3.StringUtils;
import org.opencb.opencga.core.models.Project;
import org.opencb.opencga.core.models.Study;

public class VisitorByAssembly implements StudyVisitor {

	private final String assembly;
	private final StudyVisitor baseVisitor;

	public VisitorByAssembly(String assembly, StudyVisitor baseVisitor) {
		this.assembly = assembly;
		this.baseVisitor = baseVisitor;
	}

	@Override
	public void visit(Project project, Study study) {
		boolean valid = StringUtils.isBlank(assembly) || StringUtils.equals(project.getOrganism().getAssembly(), assembly);
		if (valid) {
			baseVisitor.visit(project, study);
		}
	}

}
