package org.generationcp.middleware.v2.domain.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.v2.domain.StudyNode;
import org.generationcp.middleware.v2.pojos.DmsProject;

public class StudyNodeBuilder extends Builder {

	public StudyNodeBuilder(
			HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public List<StudyNode> build(Collection<DmsProject> projects) {
		List<StudyNode> studyNodes = new ArrayList<StudyNode>();
		if (projects != null && projects.size() > 0) {
			for (DmsProject project : projects) {
				studyNodes.add(new StudyNode(project.getProjectId(), project.getName(), project.getDescription()));
			}
		}
		return studyNodes;
	}
}
